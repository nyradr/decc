package decc.dht.packet;

import decc.dht.Key;
import decc.packet.Packet;

/**
 * Packet for find successor request
 * @author nyradr
 *
 */
public class FindSucPck extends KeyPck{

	public FindSucPck(Key k) {
		super(k);
	}

	public FindSucPck(String data){
		super(data);
	}

}
