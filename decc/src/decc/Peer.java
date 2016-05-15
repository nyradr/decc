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

/**
 * Represent network peer
 * @author nyradr
 *
 */
class Peer extends Thread{
	public static final int STEG_KEY_LEN = 0xFF;	//default key len
	
	private Socket sock;							//socket
	private IPeerReceive callback;					//Callback to NetInstance when message received
	private final int timeout = 5000;				// socket timeout
	
	private InputStream stegin;						//input stream
	private OutputStream stegout;					//output stream
	private byte [] stegkey;						//cryptography key
	
	private String recv;							//reception buffer
	
	private boolean isrunning;						//peer running
	
	/**
	 * Build new peer when the "server" receive connection
	 * @param callback
	 * @param sock
	 * @throws IOException
	 */
	public Peer(IPeerReceive callback, Socket sock) throws IOException{
		System.out.println("Co du pair " + sock.getInetAddress().toString());
		
		this.sock = sock;
		sock.setSoTimeout(timeout);
		this.callback = callback;
		
		this.sendIP();
		
			
		this.stegin = this.sock.getInputStream();
		this.stegout = this.sock.getOutputStream();
			
		this.start();
		
	}
	
	/**
	 * Create new pair and connect to it
	 * @param callback
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Peer(IPeerReceive callback, String host, int port) throws UnknownHostException, IOException, SocketTimeoutException{
		this.sock = new Socket();
		SocketAddress addr = new InetSocketAddress(host, port);
		sock.connect(addr, timeout);
		
		this.sock.setTcpNoDelay(true);
		
		this.callback = callback;
		
		this.stegin = this.sock.getInputStream();
		this.stegout = this.sock.getOutputStream();
		
		this.start();
	}
	
	/**
	 * Main listen loop for the peer
	 */
	public void run(){
		this.isrunning = true;
		this.recv = "";
		
		while(this.isrunning){
			try {
				
				int data = this.stegin.read();
				
				if(data != -1){ 
					
					if(data == '\0'){
						System.out.println("Recv(" + recv.length() + ") " + this.recv);	//debug
						this.callback.received(this, this.recv);
						this.recv = "";
					}else
						this.recv += (char) data;
				}
			} catch (SocketException | SocketTimeoutException e){	// socket error : disconnected?
				e.printStackTrace();
				isrunning = false;
				callback.deco(this);
			} catch (Exception e) {		//other error
				e.printStackTrace();
				this.recv = "";
			}
		}
	}
	
	/**
	 * Close the peer<br>
	 * Stop Socket and Thread
	 * @throws IOException
	 */
	public void close() throws IOException{
		this.isrunning = false;
		this.sock.close();
	}
	
	/**
	 * Get the host name or Ip
	 * @return
	 */
	public String getHostName(){
		return sock.getInetAddress().getHostAddress();
	}
	
	private void sendIP(){
		send(Command.IP + getHostName());
	}
	
	/**
	 * Send data
	 * @param data
	 */
	public void send(String data){
		data += '\0';
		//this.stegout.add(data.getBytes());
		System.out.println("Send(" + data.getBytes().length + ")" + this.getHostName() + " : " + data);
		try {
			this.stegout.write(data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Send route signal
	 * @param data
	 */
	public void sendRoute(String data){
		send(Command.ROUTE.toString() + data);
	}
	
	/**
	 * Send eroute signal
	 * @param comid
	 */
	public void sendEroute(String comid){
		send(Command.EROUTE.toString() + comid);
	}
	
	public void sendEroutePdc(String data){
		send(Command.EROUTEPDC.toString() + data);
	}
	
	/**
	 * Send mess signal
	 * @param mess
	 */
	public void sendMess(String mess){
		send(Command.MESS.toString() + mess);
	}

	public void sendBrcast(String ip){
		send(Command.BRCAST + ip);
	}
}
