package decc.accounts;

import decc.dht.Key;
import decc.dht.Value;
import decc.dht.ui.IDhtClb;

/**
 * DHT callback base class for Account and Contact
 * @author nyradr
 */
abstract class DHTCallClb implements IDhtClb{

	@Override
	public void onStore(Key k, char flag) {}

	@Override
	public void onLookup(Key k, Value v) {}

}
