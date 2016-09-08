package decc.netw;

/**
 * Callback functions for Listener
 * @author nyradr
 */
public interface IListenerClb {

	/**
	 * Called when a new peer is connected
	 * @param p
	 */
	public void onNewPeer(Peer p);
	
}
