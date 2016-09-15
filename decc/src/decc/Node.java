package decc;

import java.io.IOException;
import java.net.Socket;

import decc.dht.Key;
import decc.dht.packet.FindSucPck;
import decc.dht.packet.FindSucRPck;
import decc.netw.IPeerReceive;
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


}
