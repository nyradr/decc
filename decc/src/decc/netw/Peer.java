package decc.netw;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Represent a network peer
 * Asynchronous listening for data
 * @author nyradr
 */
public class Peer extends Thread{

	private Socket sock;
	
	private boolean isRunning;
	private String recv;
	private IPeerReceive clb;
	
	public Peer(IPeerReceive clb, Socket sock){
		this.sock = sock;
		this.clb = clb;		
	}
	
	/**
	 * Listen for data from peer
	 * send back data to callback
	 */
	public void run(){
		this.isRunning = true;
		this.recv = "";
		
		while(this.isRunning){
			try {
				int data = sock.getInputStream().read();
				
				if(data != -1){	
					if(data == '\0'){
						this.clb.onPeerReceive(this, this.recv);
						this.recv = "";
					}else
						this.recv += (char) data;
				}else	// end of stream == deco
					clb.onPeerDeco(this);
			} catch (SocketException e){	// socket error : disconnected?
				e.printStackTrace();
				isRunning = false;
				clb.onPeerDeco(this);
			} catch(SocketTimeoutException e){
				
			} catch (Exception e) {		//other error
				e.printStackTrace();
				this.recv = "";
			}
		}
	}
	
	/**
	 * Close the peer
	 * @throws IOException
	 */
	public void close() throws IOException{
		if(isRunning){
			isRunning = false;
			sock.close();
		}
	}
	
	/**
	 * Send message to the peer
	 * @param mess message to send
	 */
	public void send(String mess){
		try{
			if(!mess.isEmpty()){
				if(mess.charAt(mess.length() -1) != '\0')
					mess += '\0';
				sock.getOutputStream().write(mess.getBytes());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the host name or Ip
	 * @return
	 */
	public String getHostName(){
		return sock.getInetAddress().getHostAddress();
	}
}
