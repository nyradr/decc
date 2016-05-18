package decc;

/**
 * User available functions for DECC
 * @author nyradr
 */
public interface IDecc {
	
	/**
	 * Stop the DECC instance<br>
	 * Terminate all roads and communications
	 */
	public void close();
	
	/**
	 * Try to connect to a DECC peer
	 * @param host IP or host name of the target
	 * @return true is the connection succeed
	 */
	public boolean connect(String host);
	
	/**
	 * Disconnect a DECC peer.<br>
	 * Terminate all roads passing through this peer.<br>
	 * Try to retrieve communication passing through this peer.
	 * @param host IP
	 * @return true if the peer is disconnected
	 */
	public boolean disconnect(String host);
	
	/**
	 * Start a communication with the target.<br>
	 * A {@link IDeccUser#onNewCom(String) } event will be fired if the target is reached.
	 * @param target target name
	 * @return the COMID of the new conversation
	 */
	public String startCom(String target);
	
	/**
	 * Try to close a communication
	 * @param comid communication COMID
	 * @return true if the communication is closed
	 */
	public boolean closeCom(String comid);
	
	/**
	 * Send message to the specified COMID
	 * @param comid communication COMID
	 * @param mess message to send. The message SHOULD not contain '\0'.
	 * @return true if the message is send.
	 */
	public boolean sendTo(String comid, String mess);
	
	/**
	 * Set peer name
	 * @param name new peer name
	 */
	public void setName(String name);
	
	/**
	 * Get the host name of all connected peers.
	 * @return
	 */
	public String[] getConnectedHosts();
	
	/**
	 * Get all communications
	 * @return
	 */
	public ICom[] getComs();
	
	/**
	 * Get communication by COMID
	 * @param comid communication COMID
	 * @return communication interface or null
	 */
	public ICom getCom(String comid);
	
	/**
	 * Get all roads COMID
	 * @return
	 */
	public String[] getRoadsComid();
}
