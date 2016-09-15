package decc.dht.packet;

import decc.dht.Key;

/**
 * Answer a stabilize request (ask predecessor)
 * @author nyradr
 */
public class StabilizeRPck extends KeyPck{

	public StabilizeRPck(Key k) {
		super(k);
	}

	public StabilizeRPck(String data){
		super(data);
	}
}
