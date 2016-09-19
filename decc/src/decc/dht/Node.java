package decc.dht;

/**
 * Represent a DHT node
 * @author nyradr
 */
public abstract class Node {
	/**
	 * Maximum number of nodes
	 * m = 256 (number of bits in the key hash algorithm
	 */
	protected static final int m = 256;
	
	protected Key key;
	
	
}
