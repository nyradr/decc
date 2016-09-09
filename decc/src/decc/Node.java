package decc;

import java.io.IOException;
import java.net.Socket;

import decc.netw.IPeerReceive;
import decc.netw.Peer;
import decc.packet.EroutedPck;
import decc.packet.MessPck;
import decc.packet.Packet;
import decc.packet.RoadPck;

/**
 * Represent network peer
 * @author nyradr
 *
 */
class Node{
	
	private Peer peer;
	
	public Node(Peer p){
		peer = p;
		p.start();
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
	
	private void send(String data){
		peer.send(data);
	}
	
	private void sendIP(){
		send(Command.IP.toString() + peer.getHostName());
	}
	
	private void send(Command cmd, Packet pck){
		send(cmd.toString() + pck.getPck());
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
	
	/**
	 * Send broadcast signal
	 * @param ip peer IP
	 */
	public void sendBrcast(String ip){
		send(Command.BRCAST + ip);
	}
}
