package decc;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import decc.options.Options;
import decc.options.OptionsBuilder;
import decc.packet.EroutedPck;
import decc.packet.MessPck;
import decc.packet.RoadPck;

/**
 * Instance of the decc protocol
 * @author nyradr
 */
public class DeccInstance extends Thread implements IPeerReceive{
	
	private ServerSocket serv;			// serveur
	private int port;					// port
	private String ip;					// public ip
	
	private String name;				//user name
	
	private boolean isRunning;			//for the thread
	
	private Map<String, Peer> pairs;	//list of connected peers
	
	private ComsList coms;				//current communications
	private RoadList roads;				//current roads
	
	private IDeccUser userclb;			//callback for the user
	private Options options;			//decc options
	
	/**
	 * ctor
	 * @param port tcp port
	 * @param name user name
	 * @param clb callback
	 * @throws IOException error when the socket is open
	 */
	public DeccInstance(int port, String name, IDeccUser clb) throws IOException{
		this.options = OptionsBuilder.getDefault();
		
		this.name = name;
		
		this.serv = new ServerSocket(port);
		serv.setSoTimeout(5000);
		
		this.pairs = new TreeMap<String, Peer>();
		this.coms = new ComsList();
		this.roads = new RoadList();
		
		this.port = port;
		
		this.userclb = clb;
	}
	
	/**
	 * Stop the decc instance<br>
	 * Terminate all roads and communications
	 */
	public void close(){
		//TODO close roads
		
		try {
			isRunning = false;
			serv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Communication c : coms.getComs()){
			c.getPeer().sendEroute(c.getComid());
			coms.remove(c);
		}
		
		for (Road r : roads.getRoads()){
			r.peerA.sendEroutePdc((new EroutedPck(r.getComid(), true)).getPck());
			r.peerB.sendEroutePdc((new EroutedPck(r.getComid(), false)).getPck());
			roads.remove(r);
		}
		
		for(Peer p : pairs.values()){
			try {
				p.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	public void run(){
		this.isRunning = true;
		System.out.println("Start Server");
		
		while(this.isRunning){
			try {
				Socket sock = serv.accept();
				if(pairs.size() < options.maxPeers()){
					Peer pair = new Peer(this, sock);
					pairs.put(pair.getHostName(), pair);
				}else
					sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Try to connect to host
	 * @param host IP or host name
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect(String host) throws UnknownHostException, IOException, SocketTimeoutException{
		Peer pair = new Peer(this, host, this.port);
		pairs.put(pair.getHostName(), pair);
		
		if(pairs.size() == 1)
			pair.sendBrcast(ip);
	}
	
	/**
	 * Create road to someone (if the target doesn't exist you will speak with nobody)
	 * @param target
	 * @return comid of the new road
	 */
	public String roadTo(String target){
		String comid = Communication.generateComid(target, this.name);
		
		for(Peer p : this.pairs.values()){
			Communication com = new Communication(comid, target, p);
			RoadPck rpck = new RoadPck(comid, this.name, target);
			
			p.sendRoute(rpck.getPck());
			this.coms.add(com);
		}
		
		this.userclb.onNewCom(comid);
		
		return comid;
	}

	@Override
	public void received(Peer p, String m) {
		System.out.println(p.getHostName() + " : " + m);
		
		Command cmd = Command.parse(m);
		String args = m.substring(1);
		
		switch (cmd) {
		case ROUTE:
			onRoute(p, args);
			break;

		case EROUTE:
			onEroute(p, args);
			break;
			
		case EROUTEPDC:
			onEroutePdc(p, args);
			break;
			
		case MESS:
			onMess(p, args);
			break;
			
		case IP:
			onIP(p, args);
			break;
			
		case BRCAST:
			onBrcast(p, args);
			break;
			
		default:
			System.out.println("Unknow packet receveid from " + p.getHostName() + " with " + m);
			
			break;
		}
		
	}
	
	@Override
	public void deco(Peer p) {
		System.out.println("Peer error : deco : " + p.getHostName());
		
		try {
			p.close();
			pairs.remove(p.getHostName());
			
			for(Peer pe : pairs.values())	//one less, ten found : send broadcast
				pe.sendBrcast(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Produce when ip message is received
	 * launched after connection to a peer
	 * @param p
	 * @param args
	 */
	private void onIP(Peer p, String args){
		this.ip = args;	//TODO coherence control
	}
	
	/**
	 * Produce when Route message is received
	 * @param p
	 * @param args
	 */
	private void onRoute(Peer p, String args){
		RoadPck rpck = new RoadPck(args);
		System.out.println("Try to trace road from " + p.getHostName() + " with comid " + rpck.getComid() + " ori " + rpck.getOri() + " dest " + rpck.getDest());
		
		
		if(this.roads.getComid(rpck.getComid()).isEmpty()){			//no road with the comid
			
			if(rpck.getDest().equals(this.name)){
				
				if(this.coms.getComid(rpck.getComid()).isEmpty()){		//no coms with the comid
					this.coms.add(new Communication(rpck.getComid(), rpck.getOri(), p));	//Add new conv
					
					MessPck mpck = new MessPck(rpck.getComid(), MessPck.CMD_CFND, "");	//send arrival confirmation
					p.sendMess(mpck.getPck());
					
					this.userclb.onNewCom(rpck.getComid());	// to user
				}else
					p.sendEroute(rpck.getComid());
			}else{
				for(Peer peer : this.pairs.values())	//transmission to all
					if(peer != p){
						this.roads.add(new Road(rpck.getComid(), p, peer));
						peer.sendRoute(rpck.getPck());
						this.userclb.onNewRoad(rpck.getComid(), p.getHostName(), peer.getHostName());
					}
			}
		}else			// already pass by this peer, abortion of the road
			p.sendEroute(rpck.getComid());
		
	}
	
	/**
	 * When eroute signal received<br>
	 * Abort the road with this comid to this peer
	 * @param p
	 * @param args
	 */
	private void onEroute(Peer p, String args){
		System.out.println("Eroute message from " + p.getHostName() + " for comid " + args);
		
		List<Road> curroads = this.roads.getComid(args);
		
		if(!curroads.isEmpty()){											//some roads exist
			if(curroads.size() == 1)										//but only one
				curroads.get(0).roadFrom(p).sendEroute(args);				//transmiting the eroute signal to the other peer	;
			
			this.roads.remove(this.roads.getPeerComid(args, p).get(0));		//remove the road
		}else{																//no roads exist, check coms
			List<Communication> curcom = this.coms.getPeerComid(args, p);
			if(!curcom.isEmpty())
				this.coms.remove(curcom.get(0));
			
			if(this.coms.getComid(args).isEmpty()){
				this.userclb.onComEnd(args);
			}
		}
	}
	
	/**
	 * When a EroutePdc command is received
	 * Retro-transmission to the conversation origin.
	 * If origin reached, retrace the road
	 * @param p
	 * @param args
	 */
	private void onEroutePdc(Peer p, String args){
		EroutedPck erpck = new EroutedPck(args);
		
		if(coms.getPeerComid(erpck.getComid(), p).isEmpty()){			// simple node on the road -> route the message
			for(Road r : roads.getPeerComid(erpck.getComid(), p))		// to each roads with this comid (only 1 normally)
				r.roadFrom(p).sendEroutePdc(args);
		}else{
			String target = "";
			for(Communication c : coms.getPeerComid(erpck.getComid(), p)){	//to each coms with this couple peer/comid (1)
				target = c.getTarget();										// get the target name
				coms.remove(c);												// remove it from the coms (invalid road -> com)
			}
			
			if(erpck.getFlag())
				roadTo(target);			//retrace new road to the target
		}
	}
	
	/**
	 * When message signal received<br>
	 * Transmite the message though road or put it to the user
	 * @param p
	 * @param args
	 */
	private void onMess(Peer p, String args){
		MessPck mpck = new MessPck(args);
		System.out.println("Routing message from " + p.getHostName() + " say " + mpck.getData());
		
		if(this.coms.getComid(mpck.getComid()).isEmpty()){	// not the target of this message, route it
			List<Road> curroad = this.roads.getPeerComid(mpck.getComid(), p);
			
			if(!curroad.isEmpty())
				curroad.get(0).roadFrom(p).sendMess(mpck.getPck());
			else
				System.out.println("Packet transmission error");
		}else{	// target reached
			switch (mpck.getCommand()) {	// manage internal command
			case MessPck.CMD_CFND:	// success road traced
				this.userclb.onNewCom(mpck.getComid());
				break;

			default:	// no valid command : it's a normal message
				this.userclb.onMess(mpck.getComid(), mpck.getData());
				break;
			}
		}
		
	}
	
	/**
	 * When a broadcast message is received
	 * @param p peer
	 * @param args broadcast ip
	 */
	private void onBrcast(Peer p, String args){
		boolean co = false;
		
		if(!args.equals(ip) && pairs.size() < options.maxPeers())
			if(Math.random() >= 0.5){
				try{
					connect(args);
					co = true;
				}catch(Exception e){}
			}

	
		if(!co){
			for(Peer pe : pairs.values())
				if(p != pe)
					pe.sendBrcast(args);
		}
	}
	
	/**
	 * Send a message to the comid
	 * @param comid
	 * @param data
	 */
	public void send(String comid, String data){
		MessPck mpck = new MessPck(comid, data);
		
		for(Communication c : this.coms.getComid(comid)){
			c.getPeer().sendMess(mpck.getPck());
		}
	}
	
	/**
	 * Send message to all
	 * @param mess
	 */
	public void send(String mess){
		for(Peer p : this.pairs.values())
			p.send(mess);
	}

	/**
	 * Define the name
	 * @param name
	 */
	public void setname(String name){
		this.name = name;
	}
	
	/**
	 * Get all the ip of every connected peer
	 * @return
	 */
	public String[] getIpPeer(){
		return pairs.keySet().toArray(new String[0]);
	}
	
	/**
	 * Disconnect a peer
	 * @param host IP of the peer to disconnect
	 * @return true if the peer exist
	 */
	public boolean disconnectPeer(String host){
		Peer p = pairs.get(host);
		if(p != null)
			deco(p);
		
		return p != null;
	}
	
	/**
	 * Get all the ICom interface for every communication
	 * @return
	 */
	public ICom[] getComs(){
		return coms.getIComs();
	}
	
	/**
	 * Close conversations with this comid
	 * @param comid comid to close
	 * @return true if at least one conversation is found
	 */
	public boolean closeCom(String comid){
		List<Communication> cmid = coms.getComid(comid);
		boolean fnd = !cmid.isEmpty();
		
		for(Communication c : cmid)
			coms.remove(c);
		
		return fnd;
	}
}
