package decc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import decc.netw.IPeerReceive;
import decc.packet.EroutedPck;
import decc.packet.MessPck;
import decc.packet.Packet;
import decc.packet.RoadPck;

/**
 * Represent network peer
 * @author nyradr
 *
 */
class Peer extends decc.netw.Peer{
	
	public Peer(IPeerReceive clb, Socket sock) {
		super(clb, sock);
	}
	
	/**
	 * Build new peer when the "server" receive connection
	 * @param callback
	 * @param sock
	 * @throws IOException
	 *
	public Peer(IPeerReceive callback, Socket sock) throws IOException{
		System.out.println("Co du pair " + sock.getInetAddress().toString());
		
		this.sock = sock;
		sock.setSoTimeout(timeout);
		this.callback = callback;
			
		this.stegin = this.sock.getInputStream();
		this.stegout = this.sock.getOutputStream();
		
		this.sendIP();
		
		this.start();
	}*/
	
	/**
	 * Create new pair and connect to it
	 * @param callback
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 *
	public Peer(IPeerReceive callback, String host, int port) throws UnknownHostException, IOException, SocketTimeoutException{
		this.sock = new Socket();
		SocketAddress addr = new InetSocketAddress(host, port);
		sock.connect(addr, timeout);
		
		this.callback = callback;
		
		this.stegin = this.sock.getInputStream();
		this.stegout = this.sock.getOutputStream();
		
		sendIP();
		
		this.start();
	}*/
	
	private void sendIP(){
		send(Command.IP.toString() + getHostName());
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
