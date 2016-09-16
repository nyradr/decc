package decc.dht.packet;

import decc.dht.Key;
import decc.packet.Packet;

/**
 * answer packet for find successor request
 * @author nyradr
 */
public class FindSucRPck extends Packet{

	private Key key;
	private String ip;
	
	public FindSucRPck(Key k, String ip) {
		key = k;
		this.ip = ip;
	}
	
	public FindSucRPck(String data) {
		extract(data);
	}
	
	/**
	 * Get DHT key
	 * @return
	 */
	public Key getKey(){
		return key;
	}
	
	/**
	 * Get sender IP
	 * @return
	 */
	public String getIp(){
		return ip;
	}
	
	@Override
	public String getPck() {
		return key.toString() + "\n" + ip;
	}

	@Override
	public boolean extract(String args) {
		boolean suc = false;
		int i = args.indexOf("\n");
		
		if(i > 0 && i +1 < args.length()){
			key = Key.load(args.substring(0, i));
			ip = args.substring(i +1);
			suc = true;
		}
		
		return suc;
	}

	
}
