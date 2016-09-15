package decc.dht.packet;

import decc.dht.Key;

/**
 * Packet for Notify request
 * Notify request tell successor about predecessor
 * @author nyradr
 */
public class NotifyPck extends KeyPck{

	public NotifyPck(Key k) {
		super(k);
	}
	
	public NotifyPck(String args){
		super(args);
	}

}
