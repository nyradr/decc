package decc.dht.packet;

import decc.dht.Key;
import decc.dht.Value;

/**
 * Packet for a lookup command
 * @author nyradr
 */
public class LookupRPck extends KeyValuePck{
	
	public LookupRPck(Key k, Value v) {
		super(k, v);
	}
	
	public LookupRPck(String args){
		super(args);
	}
}
