package decc;

import java.io.IOException;

import decc.dht.Key;
import decc.dht.packet.FindSucPck;
import decc.dht.packet.FindSucRPck;
import decc.dht.packet.LookupPck;
import decc.dht.packet.LookupRPck;
import decc.dht.packet.NotifyPck;
import decc.dht.packet.StabilizeRPck;
import decc.dht.packet.StorePck;
import decc.dht.packet.StoreRPck;
import decc.netw.Peer;
import decc.packet.EroutedPck;
import decc.packet.IpPck;
import decc.packet.MessPck;
import decc.packet.Packet;
import decc.packet.RoadPck;

/**
 * Represent network peer
 * @author nyradr
 *
 */
class Node extends decc.dht.Node{
	
	private Peer peer;
	
	public Node(Peer p){
		peer = p;
		p.start();
	}
	
	/**
	 * Get the DHT key of this node
	 * @return
	 */
	public Key getKey(){
		return key;
	}
	
	/**
	 * Set the DHT key of this node
	 * @param k
	 */
	public void setKey(Key k){
		key = k;
	}
	
	public String getHostName(){
		return peer.getHostName();
	}
	
	public void close(){
		try {
			peer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data to peer
	 * @param data
	 */
	private void send(String data){
		System.out.println(getHostName() + " < " + data);
		
		peer.send(data);
	}
	
	/**
	 * Send command and packet to peer
	 * @param cmd command
	 * @param pck packet
	 */
	private void send(Command cmd, Packet pck){
		send(cmd.toString() + pck.getPck());
	}
	
	/**
	 * Send IP command to peer
	 * @param k
	 */
	public void sendIP(Key k){
		IpPck pck = new IpPck(peer.getHostName(), k);
		
		send(Command.IP, pck);
	}
	
	/**
	 * Send route signal
	 * @param data
	 */
	public void sendRoute(String data){
		send(Command.ROUTE.toString() + data);
	}
	
	/**
	 * Send route signal
	 * @param pck road packet
	 */
	public void sendRoute(RoadPck pck){
		send(Command.ROUTE, pck);
	}
	
	/**
	 * Send eroute signal
	 * @param comid
	 */
	public void sendEroute(String comid){
		send(Command.EROUTE.toString() + comid);
	}
	
	/**
	 * send Eroute signal
	 * @param pck eroute packet
	 */
	public void sendEroute(EroutedPck pck){
		send(Command.EROUTE, pck);
	}
	
	/**
	 * Send Eroute signal for peer disconnection
	 * @param data
	 */
	public void sendEroutePdc(String data){
		send(Command.EROUTEPDC.toString() + data);
	}
	
	/**
	 * Send Eroute signal for peer disconnection
	 * @param pck
	 */
	public void sendEroutePdc(EroutedPck pck){
		send(Command.EROUTEPDC, pck);
	}
	
	/**
	 * Send mess signal
	 * @param mess
	 */
	public void sendMess(String mess){
		send(Command.MESS.toString() + mess);
	}
	
	/**
	 * Send mess signal
	 * @param pck message packet
	 */
	public void sendMess(MessPck pck){
		send(Command.MESS, pck);
	}
	
	/// DHT
	
	/**
	 * Send DHT find successor request to node
	 * @param pck
	 */
	public void sendFindSuccessor(FindSucPck pck){
		send(Command.DFINDSUC, pck);
	}
	
	/**
	 * Send DHT find successor answer to node
	 * @param pck
	 */
	public void sendFindSuccessorRep(FindSucRPck pck){
		send(Command.DFINDSUCR, pck);
	}

	/**
	 * Send DHT stabilize request (get the node predecessor)
	 */
	public void sendStabilize(){
		send(Command.DSTABI.toString());
	}

	/**
	 * Answer to DHT stabilize request (key of the node predecessor)
	 * @param pck
	 */
	public void sendStabilizeRep(StabilizeRPck pck){
		send(Command.DSTABIR, pck);
	}

	/**
	 * Send notify to successor node
	 * @param pck
	 */
	public void sendNotify(NotifyPck pck){
		send(Command.DNOTIF, pck);
	}
	
	/**
	 * Send store request
	 * @param pck
	 */
	public void sendStore(StorePck pck){
		send(Command.DSTORE, pck);
	}
	
	/**
	 * Send store answer
	 * @param pck
	 */
	public void sendStoreRep(StoreRPck pck){
		send(Command.DSTORER, pck);
	}
	
	/**
	 * Send lookup request
	 * @param pck
	 */
	public void sendLookup(LookupPck pck){
		send(Command.DLOOKUP, pck);
	}
	
	/**
	 * Send lookup answer
	 * @param pck
	 */
	public void sendLoockupRep(LookupRPck pck){
		send(Command.DLOOKUPR, pck);
	}
}
