package decc;

/**
 * Represent a road from the peerA to the peerB with a comid
 * @author nyradr
 *
 */
class Road {
	
	private String comid;
	
	Peer peerA;
	Peer peerB;
	
	/**
	 * @param comid road identifier
	 * @param a origin
	 * @param b destination
	 */
	public Road(String comid, Peer a, Peer b){
		this.comid = comid;
		this.peerA = a;
		this.peerB = b;
	}
	
	/**
	 * @return get the origin
	 */
	public Peer getA(){
		return this.peerA;
	}
	
	/**
	 * @return get the destination
	 */
	public Peer getB(){
		return this.peerB;
	}
	
	/**
	 * @return get the COMID of the road
	 */
	public String getComid(){
		return this.comid;
	}
	
	/**
	 * Get the next peer from the peer p
	 * @param p
	 * @return null if p in not in the road
	 */
	public Peer roadFrom(Peer p){
		if(p == peerA)
			return peerB;
		
		if(p == peerB)
			return peerA;
		
		return null;
	}
	
}
