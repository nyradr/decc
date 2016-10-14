package decc;


import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import decc.accounts.Account;
import decc.accounts.AccountsManager;
import decc.accounts.Contact;
import decc.dht.CurrentNode;
import decc.dht.DhtRoutingTable;
import decc.dht.Key;
import decc.dht.Value;
import decc.dht.packet.FindSucPck;
import decc.dht.packet.FindSucRPck;
import decc.dht.packet.LookupPck;
import decc.dht.packet.LookupRPck;
import decc.dht.packet.NotifyPck;
import decc.dht.packet.StabilizeRPck;
import decc.dht.packet.StorePck;
import decc.dht.packet.StoreRPck;
import decc.dht.ui.IDhtClb;
import decc.netw.IListenerClb;
import decc.netw.IPeerReceive;
import decc.netw.Listener;
import decc.netw.Peer;
import decc.options.Crypto;
import decc.options.Options;
import decc.options.OptionsBuilder;
import decc.packet.EroutedPck;
import decc.packet.IpPck;
import decc.packet.MessPck;
import decc.packet.RoadPck;
import decc.packet.Packet;
import decc.ui.ICom;
import decc.ui.IDecc;
import decc.ui.IDeccUser;

/**
 * Instance of the decc protocol<br>
 * @author nyradr
 */
class DeccInstance extends CurrentNode implements IListenerClb, IPeerReceive, IDecc{
	
	Listener netw;						// network listener
	private String ip;					// public ip
	
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
	public DeccInstance(int port, Account user, IDeccUser clb) throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		super(Key.create(user.getName()));
		
		this.options = OptionsBuilder.getDefault();
		
		netw = new Listener(this, this, port);
		accman = new AccountsManager(user);
		
		this.pairs = new TreeMap<String, Node>();
		this.coms = new ComsList();
		this.roads = new RoadList();
		this.nodesroads = new DhtRoutingTable();
		
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
			Node peer = new Node(netw.connect(host));
			peer.sendIP(key);
			
			pairs.put(peer.getHostName(), peer);
			
			// empty ring -> join ring
			if(isEmptyRing())
				initFingerTable(peer);
			
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
			Node peer = new Node(p);
			pairs.put(p.getHostName(), peer);
			
			peer.sendIP(key);
			
			userclb.onNewPeer(p.getHostName());
			
			if(isEmptyRing())
				initFingerTable(peer);
			
		}else{
			try{
				p.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
		
	/// IPeer receive
	
	@Override
	public void onPeerDeco(Peer peer) {
		Node p = pairs.get(peer.getHostName());
		System.out.println("Peer deco : " + p.getHostName());
		
		p.close();
		
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
		
		userclb.onPeerDeco(p.getHostName());
		
		// DHT check predecessor
		if(p.getKey() == predecessor)
			predecessor = null;
		
		pairs.remove(p.getHostName());
	}
	
	@Override
	public void onPeerReceive(Peer peer, String m) {
		Node p = pairs.get(peer.getHostName());
		
		System.out.println(p.getHostName() + " > " + m);
		
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
			
		case DFINDSUC:
			onFindSuccessor(p, args);
			break;
			
		case DFINDSUCR:
			onFindSuccessorR(p, args);
			break;
			
		case DNOTIF:
			onNotify(p, args);
			break;
			
		case DSTABI:
			onStabilize(p, args);
			break;
			
		case DSTABIR:
			onStabilizeRep(p, args);
			break;
			
		case DSTORE:
			onStore(p, args);
			break;
			
		case DSTORER:
			onStoreRep(p, args);
			break;
			
		case DLOOKUP:
			onLookup(p, args);
			break;
			
		case DLOOKUPR:
			onLookupRep(p, args);
			break;
			
		default:
			System.out.println("Unknow packet receveid from " + p.getHostName() + " with " + m);
			break;
		}
		
	}
	
	/**
	 * Produce when ip message is received
	 * launched after connection to a peer
	 * @param p
	 * @param args
	 */
	private void onIP(Node p, String args){
		IpPck pck = new IpPck(args);
		
		this.ip = pck.getIp();
		p.setKey(pck.getKey());
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
	
	/// DHT
	
	/**
	 * Initialize the finger table with initial peer
	 * @param peer initial peer
	 */
	private void initFingerTable(Node peer){
		for(int k = m; k >= 1; k--){
			Key finger = Key.load(super.finger(k));
			
			nodesroads.put(finger, key);
			peer.sendFindSuccessor(new FindSucPck(finger));
		}
	}
	
	/**
	 * Get the connected node with this key
	 * @param key key to find
	 * @return Node or null (if no node with this key)
	 */
	private Node getNodeWithKey(Key k){
		Node n = null;
		
		List<Node> ln = pairs.values().parallelStream()
			.filter(x -> x != null)
			.filter(x -> x.getKey().equals(k))
			.collect(Collectors.toList());
	
		if(!ln.isEmpty())
			n = ln.get(0);
		
		return n;
	}
	
	/**
	 * When a find successor message is received
	 * @param p peer
	 * @param args message
	 */
	private void onFindSuccessor(Node p, String args){
		FindSucPck pck = new FindSucPck(args);
		
		Key k = findSuccessor(pck.getKey());
		Node n = getNodeWithKey(k);
		
		if(k.equals(successor) || k.equals(key)){ // successor found
			// potential successor IP
			String ip = "";
			if(n == null)
				ip = this.ip;
			else
				ip = n.getHostName();
			
			p.sendFindSuccessorRep(new FindSucRPck(pck.getKey(), ip));
		}else{
			// no successor found
			if(n != p){
				nodesroads.put(pck.getKey(), p.getKey());
				n.sendFindSuccessor(pck);
			}else	// but no valid node to forward the query, I'm the successor
				p.sendFindSuccessorRep(new FindSucRPck(pck.getKey(), ip));
		}
		
	}

	/**
	 * When a find successor request succeed
	 * @param p
	 * @param args
	 */
	private void onFindSuccessorR(Node p, String args){
		FindSucRPck pck = new FindSucRPck(args);
		Set<Key> ks = nodesroads.get(pck.getKey());
		
		if(ks.contains(key)){
			// reached we find our successor
			ks.remove(key);
			
			// get node with successor key (if already connected
			Node nsuc = pairs.get(pck.getIp());
			
			// connect to node (if not already connected)
			if(nsuc == null && !pck.getIp().equals(ip)){
				if(connect(pck.getIp()))
					nsuc = getNodeWithKey(pck.getKey());
			}
			
			// successor node found
			if(nsuc != null){
				Key suc = Key.load(super.finger(1));
				
				if(pck.getKey().equals(suc)){
					// immediate successor
					if(!successor.equals(nsuc.getKey())){
						successor = nsuc.getKey();
						nsuc.sendNotify(new NotifyPck(key));
						nsuc.sendStabilize();
						System.out.println("Successor : " + successor.toString());
					}
				}else
					System.out.println("Finger found : " + nsuc.getKey().toString());
			}
		}
		
		// transmit to all remaining
		if(!ks.isEmpty()){
			for(Key k : ks){
				Node n = getNodeWithKey(k);
				if(n != null)
					n.sendFindSuccessorRep(pck);
				ks.remove(k);
			}
		}
	}
	
	/**
	 * When a stabilize request come
	 * This node predecessor must be returned
	 * @param p
	 * @param args
	 */
	private void onStabilize(Node p, String args){
		if(predecessor != null){
			Node pred = getNodeWithKey(predecessor);
			
			if(pred != null)
				p.sendStabilizeRep(new StabilizeRPck(predecessor, pred.getHostName()));
		}
	}
	
	/**
	 * When a node answer the stabilize request
	 * @param p
	 * @param args
	 */
	private void onStabilizeRep(Node p, String args){
		StabilizeRPck pck = new StabilizeRPck(args);
		
		// test consistency
		if(pck.getKey().getKey().compareTo(key.getKey()) > 0 &&
				pck.getKey().getKey().compareTo(successor.getKey()) < 0)
			successor = pck.getKey();
		
		// get node
		Node suc = getNodeWithKey(successor);
		if(suc == null){
			// connect if unknown node
			connect(pck.getIp());
			suc = getNodeWithKey(successor);
		}
		
		// send request
		suc.sendNotify(new NotifyPck(key));;
	}
	
	/**
	 * When a notify command is received
	 * @param p
	 * @param args
	 */
	private void onNotify(Node p, String args){
		NotifyPck pck = new NotifyPck(args);
		
		notify(pck.getKey());
	}
	
	/**
	 * When a store request is received
	 * @param p
	 * @param args
	 */
	private void onStore(Node p, String args){
		StorePck pck = new StorePck(args);
		
		Key k = findSuccessor(pck.getKey());
		Node n = getNodeWithKey(k);
		
		if(k.equals(successor) || k.equals(key)){
			char flag =
					(tryStore(pck.getKey(), pck.getVal()))?
							StoreRPck.FLAG_SUCCESS : StoreRPck.FLAG_FAILURE;
			
			p.sendStoreRep(new StoreRPck(pck.getKey(), flag));
		}else{
			if(n != p){
				ksroads.put(pck.getKey(), p.getKey());
				n.sendStore(pck);
			}else{
				char flag =
						(tryStore(pck.getKey(), pck.getVal()))?
								StoreRPck.FLAG_SUCCESS : StoreRPck.FLAG_FAILURE;
				
				p.sendStoreRep(new StoreRPck(pck.getKey(), flag));
			}
		}
	}
	
	/**
	 * When a store answer is received
	 * @param p
	 * @param args
	 */
	public void onStoreRep(Node p, String args){
		StoreRPck pck = new StoreRPck(args);
		Set<Key> ks = ksroads.get(pck.getKey());
		
		if(ks.contains(key)){
			ks.remove(key);
			
			IDhtClb clb = reqclbs.get(pck.getKey());
			if(clb != null){
				clb.onStore(pck.getKey(), pck.getFlag());
				System.out.println("Store : " + pck.getFlag() + " : " + pck.getKey());
			}
		}
		
		for(Key k : ks){
			ks.remove(k);
			Node n = getNodeWithKey(k);
			
			if(n != null)
				n.sendStoreRep(pck);
		}
	}
	
	/**
	 * When a lookup command is received
	 * @param p
	 * @param args
	 */
	public void onLookup(Node p, String args){
		LookupPck pck = new LookupPck(args);
		
		Key k = findSuccessor(pck.getKey());
		Node n = getNodeWithKey(k);
		
		if(k.equals(successor) || k.equals(key)){
			Value val = tryLookup(pck.getKey());
			p.sendLoockupRep(new LookupRPck(pck.getKey(), val));
		}else{
			if(n != p){
				klroads.put(pck.getKey(), p.getKey());
				n.sendLookup(pck);
			}else{
				Value val = tryLookup(pck.getKey());
				p.sendLoockupRep(new LookupRPck(pck.getKey(), val));
			}
		}
	}
	
	/**
	 * When a lookup answer is received
	 * @param p
	 * @param args
	 */
	public void onLookupRep(Node p, String args){
		LookupRPck pck = new LookupRPck(args);
		Set<Key> ks = klroads.get(pck.getKey());
		
		if(ks.contains(key)){
			ks.remove(key);
			
			IDhtClb clb = reqclbs.get(pck.getKey());
			if(clb != null){
				clb.onLookup(pck.getKey(), pck.getVal());
				System.out.println("Lookup : " + pck.getKey() + "\n\t" + pck.getVal());
			}
		}
		
		for(Key k : ks){
			ks.remove(k);
			Node n = getNodeWithKey(k);
			
			if(n != null)
				n.sendLoockupRep(pck);
		}
	}
	
	@Override
	public void stabilize(){
		Node suc = getNodeWithKey(successor);
		
		if(suc != null && ! suc.equals(key))
			suc.sendStabilize();
	}
	
	@Override
	public void store(IDhtClb clb, Key k, Value v){
		Key suc = findSuccessor(k);
		Node n = getNodeWithKey(suc);
		
		if(suc.equals(successor) || suc.equals(key)){
			clb.onStore(k, tryStoreToFlag(k, v));
		}else{
			reqclbs.put(k, clb);
			ksroads.put(k, key);
			
			n.sendStore(new StorePck(k, v));
		}
	}
	
	@Override
	public void lookup(IDhtClb clb, Key k){
		Key suc = findSuccessor(k);
		Node n = getNodeWithKey(suc);
		
		if(suc.equals(successor) || suc.equals(key)){
			clb.onLookup(k, tryLookup(k));
		}else{
			reqclbs.put(k, clb);
			klroads.put(k, key);
			
			n.sendLookup(new LookupPck(k));
		}
	}
	
	@Override
	public BigInteger finger(int k){
		BigInteger finger = super.finger(k);
		BigInteger matching = null;
		
		/* Look through all connected peers the most valid
		 * peer matching finger[k]
		*/
		for(String nip : pairs.keySet()){
			Node n = pairs.get(nip);
			
			BigInteger nk = n.getKey().getKey();
			
			
			if(finger.compareTo(nk) <= 0){
				if(matching == null || nk.compareTo(matching) < 0)
					matching = nk;
			}
		}
		
		return matching;
	}
	
}
