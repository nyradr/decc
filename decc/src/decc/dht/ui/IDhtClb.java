package decc.dht.ui;

import decc.dht.Key;
import decc.dht.Value;

/**
 * Callback interface for the DHT
 * @author nyradr
 *
 */
public interface IDhtClb {
	
	/**
	 * When a store request is finished
	 * @param k key stored
	 * @param flag error code
	 */
	public void onStore(Key k, StoreFlags flag);
	
	/**
	 * When a lookup request is finished
	 * @param k key looked
	 * @param v key value
	 */
	public void onLookup(Key k, Value v);
}
