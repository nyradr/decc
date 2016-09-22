package decc.dht.packet;

import decc.dht.Key;

/**
 * Packet for the store command
 * @author nyradr
 */
public class StorePck extends ValuePck{
	
	private Key key;
	
	public StorePck(String data) {
		extract(data);
	}

	/**
	 * 
	 */
	public boolean extract(String data){
		return true;
	}
	
	
}
