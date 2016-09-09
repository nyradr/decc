package decc.netw;

/**
 * Peer callback
 * @author nyradr
 */
public interface IPeerReceive {

	/**
	 * Produced when a peer receive a message
	 * @param p peer
	 * @param mess message (as a string)
	 */
	public void onPeerReceive(Peer p, String mess);
	
	/**
	 * Produced when a peer is closed
	 * @param p peer
	 */
	public void onPeerDeco(Peer p);
}
