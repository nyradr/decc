package decc;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import decc.options.Options;
import decc.options.OptionsBuilder;
import decc.packet.EroutedPck;
import decc.packet.MessPck;
import decc.packet.RoadPck;
import decc.ui.ICom;
import decc.ui.IDecc;
import decc.ui.IDeccUser;

/**
 * Instance of the decc protocol<br>
 * @author nyradr
 */
class DeccInstance extends Thread implements IPeerReceive, IDecc{
	
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
	
	/// IDecc
	
	@Override
	public void close(){
		
		// close server
		try {
			isRunning = false;
			serv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// disconnect all peer
		for(Peer p : pairs.values()){
			disconnect(p.getHostName());
		}
			
	}
	
	@Override
	public boolean connect(String host){ //throws UnknownHostException, IOException, SocketTimeoutException{
		try{
			Peer pair = new Peer(this, host, this.port);
			pairs.put(pair.getHostName(), pair);
		
			if(pairs.size() == 1 && ip != null)
				pair.sendBrcast(ip);
			
			return true;
		} catch(Exception e){}
		
		return false;
	}
	
	@Override
	public boolean disconnect(String host){
		Peer p = pairs.get(host);
		
		if(p != null){
			// abort all roads passing by this peer
			for(Road r : roads.getPeer(p)){
				r.peerA.sendEroutePdc((new EroutedPck(r.getComid(), true)).getPck());
				r.peerB.sendEroutePdc((new EroutedPck(r.getComid(), false)).getPck());
				roads.remove(r);
			}
			
			try {
				p.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return p != null;
	}
	
	@Override
	public String startCom(String target){
		String comid = Communication.generateComid(target, this.name);
		
		for(Peer p : pairs.values()){
			Communication com = new Communication(comid, target, p);
			RoadPck rpck = new RoadPck(comid, this.name, target);
			
			p.sendRoute(rpck.getPck());
			this.coms.add(com);
		}
		
		return comid;
	}
	
	@Override
	public boolean closeCom(String comid){
		List<Communication> cmid = coms.getComid(comid);
		boolean fnd = !cmid.isEmpty();
		
		for(Communication c : cmid){
			c.close();
			coms.remove(c);
		}
		
		userclb.onComEnd(comid);
		
		return fnd;
	}
	
	@Override
	public boolean sendTo(String comid, String data){
		MessPck mpck = new MessPck(comid, data);
		
		for(Communication c : this.coms.getComid(comid)){
			c.getPeer().sendMess(mpck.getPck());
		}
		
		return true;
	}

	@Override
	public void setname(String name){
		this.name = name;
	}
	
	@Override
	public String[] getConnectedHosts(){
		return pairs.keySet().toArray(new String[0]);
	}
	
	@Override
	public ICom[] getComs(){
		return coms.getIComs();
	}
	
	@Override
	public ICom getCom(String comid){
		List<Communication> cms = coms.getComid(comid);
		cms = cms.stream().filter(x -> x.isLinked()).collect(Collectors.toList());
		
		if(!cms.isEmpty())
			return cms.get(0);
		
		return null;
	}
	
	@Override
	public String[] getRoadsComid(){
		return roads.roads.toArray(new String[0]);
	}
	
	/// DeccInstance functions
	
	public void run(){
		this.isRunning = true;
		System.out.println("Start Server");
		
		while(this.isRunning){
			try {
				Socket sock = serv.accept();
				if(pairs.size() < options.maxPeers()){
					Peer pair = new Peer(this, sock);
					pairs.put(pair.getHostName(), pair);
					userclb.onNewPeer(pair.getHostName());
				}else
					sock.close();
			} catch (SocketTimeoutException te){
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/// IPeer receive
	
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
		System.out.println("Peer deco : " + p.getHostName());
		
		try {
			p.close();
			pairs.remove(p.getHostName());
			
			// clean roads passing through this peer
			// just in case of dirty disconnection without peer cleanup
			for(Road r : roads.getPeer(p)){
				r.roadFrom(p).sendEroutePdc(new EroutedPck(r.getComid(), true).toString());
				roads.remove(r);
			}
			
			// close all roads passing through this peer
			for(Communication c : coms.getPeer(p)){
				coms.remove(c);
				// try to retrace the road
				if(!pairs.isEmpty())
					startCom(c.getTarget());
				else
					userclb.onComEnd(c.getComid());
			}
			
			if(ip != null){
				for(Peer pe : pairs.values())	//one less, ten found : send broadcast
					pe.sendBrcast(ip);
			}
			
			userclb.onPeerDeco(p.getHostName());
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
			
			if(erpck.getFlag() && !target.isEmpty()){
				//try to retrace new road to the target
				if(!pairs.isEmpty())
					startCom(target);
				else
					userclb.onComEnd(erpck.getComid());
			}
				
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
				
				// remove all traced roads for this COMID
				// set road as linked
				for(Communication com : coms.getComs())
					if(com.getPeer() != p){
						com.close();
						coms.remove(com);
					}else
						com.setLinked(true);
				
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
	
}
