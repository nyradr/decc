package decc.dht.packet;

import decc.dht.Key;
import decc.dht.Value;

/**
 * Packet for the store command
 * @author nyradr
 */
public class StorePck extends KeyValuePck{
	
	public StorePck(Key k, Value v){
		super(k, v);
	}
	
	public StorePck(String args){
		super(args);
	}
}
