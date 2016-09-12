package decc.dht.packet;

import decc.dht.Key;
import decc.packet.Packet;

/**
 * Packet for find successor request
 * @author nyradr
 */
public class FindSucPck extends Packet{

	private Key key;
	private String ip;
	
	public FindSucPck(Key k, String ip) {
		key = k;
		this.ip = ip;
	}
	
	public FindSucPck(String data) {
		extract(data);
	}
	
	@Override
	public String getPck() {
		return key.toString() + "\n" + ip;
	}

	@Override
	public boolean extract(String args) {
		boolean suc = false;
		int i = args.indexOf("\n");
		
		if(i > 0){
			key = Key.load(args.substring(0, i));
			ip = args.substring(i);
			suc = true;
		}
		
		return suc;
	}

	
}
