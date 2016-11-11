package decc.accounts;

import decc.dht.Key;
import decc.dht.Value;
import decc.dht.ui.IDhtClb;
import decc.dht.ui.StoreFlags;

/**
 * DHT callback base class for Account and Contact
 * @author nyradr
 */
abstract class DHTCallClb implements IDhtClb{

	@Override
	public void onStore(Key k, StoreFlags flag) {}

	@Override
	public void onLookup(Key k, Value v) {}

}
