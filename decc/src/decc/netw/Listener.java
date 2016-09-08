package decc.netw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Asynchronous listener for new TCP connection
 * @author nyradr
 */
public class Listener extends Thread{
	
	private ServerSocket serv;
	private IListenerClb clb;
	
	private boolean isRunning;
	
	/**
	 * Create new listener
	 * The timeout for the accept method is set to 5000ms by default
	 * @param clb callback object
	 * @param port port to listen
	 * @throws IOException produced when the port cannot be used
	 */
	public Listener(IListenerClb clb, int port) throws IOException{
		init(clb, port, 5000);
	}
	
	/**
	 * Create new listener
	 * @param clb callback object
	 * @param port port to listen
	 * @param timeout timeout
	 * @throws IOException
	 */
	public Listener(IListenerClb clb, int port, int timeout) throws IOException{
		init(clb, port, timeout);
	}
	
	/**
	 * Create new listener
	 * @param clb
	 * @param port
	 * @param timeout
	 * @throws IOException
	 */
	private void init(IListenerClb clb, int port, int timeout) throws IOException {
		serv = new ServerSocket(port);
		serv.setSoTimeout(timeout);
		this.clb = clb;
		isRunning = false;
	}
	
	public void run(){
		isRunning = true;
		
		while(isRunning){
			try{
				Socket sock = serv.accept();
				Peer p = new Peer(sock);
				
				clb.onNewPeer(p);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close the listener
	 * @throws IOException
	 */
	public void close() throws IOException{
		if(isRunning){
			isRunning = false;
			serv.close();
		}
	}
}
