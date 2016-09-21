package decc.dht.packet;

import decc.dht.Key;
import decc.packet.IPacket;

/**
 * Packet that contain a DHT key
 * @author nyradr
 */
abstract class KeyPck implements IPacket{
	
	protected Key key;
	
	/**
	 * Create new packet
	 * @param k key to send
	 */
	public KeyPck(Key k){
		key = k;
	}
	
	/**
	 * Load packet from received data
	 * @param data
	 */
	public KeyPck(String data) {
		extract(data);
	}
	
	/**
	 * Get the stored key
	 * @return
	 */
	public Key getKey(){
		return key;
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
