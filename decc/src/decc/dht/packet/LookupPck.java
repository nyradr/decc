package decc.dht.packet;

import decc.dht.Key;

/**
 * Packet for a Lookup command
 * @author nyradr
 */
public class LookupPck extends KeyPck{

	public LookupPck(Key k){
		super(k);
	}
	
	public LookupPck(String args){
		super(args);
	}
}
