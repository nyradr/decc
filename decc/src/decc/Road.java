package decc;

/**
 * Represent a road from the peerA to the peerB with a comid
 * @author nyradr
 *
 */
class Road {
	
	private String comid;
	
	Node peerA;
	Node peerB;
	
	/**
	 * @param comid road identifier
	 * @param a origin
	 * @param b destination
	 */
	public Road(String comid, Node a, Node b){
		this.comid = comid;
		this.peerA = a;
		this.peerB = b;
	}
	
	/**
	 * @return get the origin
	 */
	public Node getA(){
		return this.peerA;
	}
	
	/**
	 * @return get the destination
	 */
	public Node getB(){
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
	public Node roadFrom(Node p){
		if(p == peerA)
			return peerB;
		
		if(p == peerB)
			return peerA;
		
		return null;
	}
	
}
