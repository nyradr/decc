package decc.dht.packet;

import decc.dht.Key;
import decc.packet.Packet;

/**
 * Packet for find successor request
 * @author nyradr
 *
 */
public class FindSucPck extends Packet{

	private Key key;
	
	/**
	 * Create new packet
	 * @param k key to find
	 */
	public FindSucPck(Key k){
		key = k;
	}
	
	public Key getKey(){
		return key;
	}
	
	/**
	 * Load packet from received data
	 * @param data
	 */
	public FindSucPck(String data) {
		extract(data);
	}
	
	@Override
	public String getPck() {
		return key.toString();
	}

	@Override
	public boolean extract(String args) {
		key = Key.load(args);
		return key != null;
	}

}
