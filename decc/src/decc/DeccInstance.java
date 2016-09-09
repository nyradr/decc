package decc;


import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import decc.accounts.Account;
import decc.accounts.AccountsManager;
import decc.accounts.Contact;
import decc.netw.IListenerClb;
import decc.netw.IPeerReceive;
import decc.netw.Listener;
import decc.netw.Peer;
import decc.options.Crypto;
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
class DeccInstance implements IListenerClb, IPeerReceive, IDecc{
	
	Listener netw;						// network listener
	private String ip;					// public ip
	
	//private String name;				//user name
	private AccountsManager accman;		// accounts manager
	
	private Map<String, Node> pairs;	//list of connected peers
	
	private ComsList coms;				//current communications
	private RoadList roads;				//current roads
	
	private IDeccUser userclb;			//callback for the user
	private Options options;			//decc options
	
	/**
	 * ctor
	 * @param port TCP port
	 * @param name user name
	 * @param clb callback
	 * @throws IOException error when the socket is open
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public DeccInstance(int port, String name, IDeccUser clb) throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		this.options = OptionsBuilder.getDefault();
		
		netw = new Listener(this, this, port);
		Account user = Account.create(name, Crypto.DEF_RSA_LEN);
		accman = new AccountsManager(user);
		
		this.pairs = new TreeMap<String, Node>();
		this.coms = new ComsList();
		this.roads = new RoadList();
		
		this.userclb = clb;
	}
	
	/// IDecc
	
	@Override
	public void start() {
		netw.start();
	}
	
	@Override
	public void close(){
		
		// close server
		try {
			netw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// close communications
		List<Communication> com = coms.getComs();
		for(Communication c : com){
			c.close();
			userclb.onComEnd(c.getComid());
		}
		
		// disconnect all peer
		for(Node p : pairs.values()){
			disconnect(p.getHostName());
		}
			
	}
	
	@Override
	public boolean connect(String host){
		try{
			Node pair = new Node(netw.connect(host));
			pairs.put(pair.getHostName(), pair);
		
			if(pairs.size() == 1 && ip != null)
				pair.sendBrcast(ip);
			
			return true;
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean disconnect(String host){
		Node p = pairs.get(host);
		
		if(p != null){
			// abort all roads passing by this peer
			for(Road r : roads.getPeer(p)){
				r.peerA.sendEroutePdc((new EroutedPck(r.getComid(), true)));
				r.peerB.sendEroutePdc((new EroutedPck(r.getComid(), false)));
				roads.remove(r);
			}
			
			p.close();
		}
		
		return p != null;
	}
	
	@Override
	public String startCom(String target){
		String comid = Communication.generateComid(target, this.accman.getUser().getName());
		
		for(Node p : pairs.values()){
			Communication com = new Communication(comid, target, p, accman, userclb);
			RoadPck rpck = new RoadPck(comid, this.accman.getUser().getName(), target);
			
			p.sendRoute(rpck);
			this.coms.add(com);
		}
		
		if(pairs.isEmpty())
			userclb.onComFail(comid, target);
		
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
		
		for(Communication c : this.coms.getComid(comid)){
			c.send(data);
		}
		
		return true;
	}

	@Override
	public void setname(String name){
		try{
			Account n = Account.create(name, Crypto.DEF_RSA_LEN);
			accman.changeUser(n);
		}catch (Exception e){
			e.printStackTrace();
		}
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
	
	/// Listener
	
	@Override
	public void onNewPeer(Peer p) {
		if(pairs.size() < options.maxPeers()){
			pairs.put(p.getHostName(), new Node(p));
			userclb.onNewPeer(p.getHostName());
		}
	}
		
	/// IPeer receive
	
	@Override
	public void onPeerReceive(Peer peer, String m) {
		Node p = pairs.get(peer.getHostName());
		
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
	public void onPeerDeco(Peer peer) {
		Node p = pairs.get(peer.getHostName());
		System.out.println("Peer deco : " + p.getHostName());
		
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
		}
		
		if(ip != null){
			for(Node pe : pairs.values())	//one less, ten found : send broadcast
				pe.sendBrcast(ip);
		}
		
		userclb.onPeerDeco(p.getHostName());
		
	}
	
	/**
	 * Produce when ip message is received
	 * launched after connection to a peer
	 * @param p
	 * @param args
	 */
	private void onIP(Node p, String args){
		this.ip = args;	//TODO coherence control
	}
	
	/**
	 * Produce when Route message is received
	 * @param p
	 * @param args
	 */
	private void onRoute(Node p, String args){
		RoadPck rpck = new RoadPck(args);
		System.out.println("Try to trace road from " + p.getHostName() + " with comid " + rpck.getComid() + " ori " + rpck.getOri() + " dest " + rpck.getDest());
		
		
		if(this.roads.getComid(rpck.getComid()).isEmpty()){			
			//no road with the comid
			
			if(rpck.getDest().equals(this.accman.getUser().getName())){
				// target reached
				
				if(this.coms.getComid(rpck.getComid()).isEmpty()){		//no coms with the comid
					Communication com = new Communication(rpck.getComid(), rpck.getOri(), p, accman, userclb);
					com.setLinked(true);
					this.coms.add(com);	//Add new conv
					
					//send arrival confirmation with the user public key
					MessPck mpck = new MessPck(rpck.getComid(),
							MessPck.CMD_CFND,
							accman.getUser().getPublicStr(), accman.getUser().getKeySign());
					p.sendMess(mpck);
					
				}else
					p.sendEroute(rpck.getComid());
			}else{
				//transmission to all
				for(Node peer : this.pairs.values())	
					if(peer != p){
						this.roads.add(new Road(rpck.getComid(), p, peer));
						peer.sendRoute(rpck);
						this.userclb.onNewRoad(rpck.getComid(), p.getHostName(), peer.getHostName());
					}
				
				// only one peer : no transmission possible : send ERoute
				if(this.pairs.size() == 1)
					p.sendEroute(rpck.getComid());
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
	private void onEroute(Node p, String args){
		System.out.println("Eroute message from " + p.getHostName() + " for comid " + args);
		
		List<Road> curroads = this.roads.getComid(args);
		
		if(!curroads.isEmpty()){											//some roads exist
			if(curroads.size() == 1)										//but only one
				curroads.get(0).roadFrom(p).sendEroute(args);				//transmiting the eroute signal to the other peer	;
			
			this.roads.remove(this.roads.getPeerComid(args, p).get(0));		//remove the road
		
		}else if (!coms.getComid(args).isEmpty()){							//no roads exist, check coms
			
			// remove communication
			List<Communication> curcom = this.coms.getPeerComid(args, p);
			boolean islinked = this.coms.isComidLinked(args);
			String target = "";
			
			if(!curcom.isEmpty()){
				target = curcom.get(0).getTarget();
				this.coms.remove(curcom.get(0));
			}
			
			if(this.coms.getComid(args).isEmpty() && !islinked)
				this.userclb.onComFail(args, target);
		}
	}
	
	/**
	 * When a EroutePdc command is received
	 * Retro-transmission to the conversation origin.
	 * If origin reached, retrace the road
	 * @param p
	 * @param args
	 */
	private void onEroutePdc(Node p, String args){
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
			}
				
		}
	}
	
	/**
	 * When message signal received<br>
	 * Transmite the message though road or put it to the user
	 * @param p
	 * @param args
	 */
	private void onMess(Node p, String args){
		MessPck mpck = new MessPck(args);
		System.out.println("Routing message from " + p.getHostName() + " say " + mpck.getData());
		
		if(this.coms.getComid(mpck.getComid()).isEmpty()){
			// not the target of this message, route it
			List<Road> curroad = this.roads.getPeerComid(mpck.getComid(), p);
			
			if(!curroad.isEmpty())
				curroad.get(0).roadFrom(p).sendMess(mpck);
			else
				System.out.println("Packet transmission error");
		}else{
			// target reached
			
			onMessArrived(mpck, p);
		}
		
	}
	
	/**
	 * Produce when a message reached his target<br>
	 * Manage message internal commands
	 * @param mp
	 */
	private void onMessArrived(MessPck mpck, Node p){
		List<Communication> comsComid = coms.getComid(mpck.getComid());
		Communication comComid = coms.getComidLinked(mpck.getComid());
		
		switch (mpck.getCommand()) {	// manage internal command
		case MessPck.CMD_CFND:	// success road traced
			
			// remove all traced roads for this COMID
			// set road as linked
			for(Communication com : comsComid)
				if(com.getPeer() != p){
					com.close();
					coms.remove(com);
				}else{
					com.setLinked(true);
					comComid = com;
				}
			// communication established, send public key
			p.sendMess(new MessPck(mpck.getComid(),
					MessPck.CMD_PK,
					accman.getUser().getPublicStr(), accman.getUser().getKeySign()));
			
			// no break for getting the target public key
		case MessPck.CMD_PK:
			try {
				KeyFactory kf = KeyFactory.getInstance(Crypto.ACC_ALGO, Crypto.Provider);
				PublicKey pub = kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(mpck.getData())));
				
				// communication found, add to contact
				if(comComid != null){
					Contact c = new Contact(comsComid.get(0).getTarget(), pub);	
					accman.addContact(c);
				}
				
				comComid.startDh();
			} catch (Exception e){
				e.printStackTrace();
			}
			break;
			
		case MessPck.CMD_DH:
			comComid.receiveDh(mpck.getData(), mpck.getSign());
			break;
			
		default:	// no valid command : it's a normal message
			comComid.receive(mpck.getData(), mpck.getSign());
			break;
		}
	}
	
	/**
	 * When a broadcast message is received
	 * @param p peer
	 * @param args broadcast ip
	 */
	private void onBrcast(Node p, String args){
		boolean co = false;
		
		if(!args.equals(ip) && pairs.size() < options.maxPeers() && !pairs.containsKey(args))
			if(Math.random() >= 0.0){ // 1 probability : debug
				try{
					connect(args);
					co = true;
				}catch(Exception e){}
			}

	
		if(!co){
			for(Node pe : pairs.values())
				if(p != pe)
					pe.sendBrcast(args);
		}
	}


	
}
