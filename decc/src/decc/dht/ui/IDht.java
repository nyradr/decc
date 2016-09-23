package decc.dht.ui;

import decc.dht.Key;
import decc.dht.Value;

/**
 * Interface for basic DHT functions
 * @author nyradr
 */
public interface IDht {

	/**
	 * Store a key/value pair in the DHT
	 * @param k key
	 * @param v value
	 */
	public void store(IDhtClb clb, Key k, Value v);
	
	/**
	 * Lookup for a key in the DHT
	 * @param k
	 */
	public void lookup(IDhtClb clb, Key k);
}
