package decc.options;

/**
 * Options of the protocol
 * @author nyradr
 */
public abstract class Options {
	
	private int		maxpeer;	// number max of peer allowed
	private int		maxRoads;	// number max of roads allowed
	
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
	
}
