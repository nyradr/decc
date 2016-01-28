package decc;

/**
 * Interface for Peer callback to NetInstance
 * @author nyradr
 */
interface IPeerReceive {
	
	/**
	 * Callback to call when a message is receved
	 * @param p
	 * @param m
	 */
	public void received(Peer p, String m);
	
	/**
	 * Callback on IO error on the socket<br>
	 * Consider it as a peer deconnection
	 * @param p
	 */
	public void deco(Peer p);
}
