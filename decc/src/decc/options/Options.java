package decc.options;

/**
 * Options of the protocol
 * @author nyradr
 */
public abstract class Options {
	
	protected int		maxpeer;	// number max of peer allowed
	protected int		maxRoads;	// number max of roads allowed
	protected boolean	allowBS;	// allow message with bad signature
	
	/**
	 * Get the maximal number of connected peers
	 * @return
	 */
	public int maxPeers(){
		return maxpeer;
	}
	
	/**
	 * Get the maximum roads allowed (including communications)
	 * @return
	 */
	public int maxRoads(){
		return maxRoads;
	}
	
	/**
	 * Return true if message with bad signature is allowed
	 * @return
	 */
	public boolean allowBadSign(){
		return allowBS;
	}
}
