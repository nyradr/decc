package decc.dht.packet;

import decc.dht.Key;

/**
 * Answer a stabilize request (ask predecessor)
 * @author nyradr
 */
public class StabilizeRPck extends KeyPck{

	private String ip;
	
	public StabilizeRPck(Key k, String ip) {
		super(k);
	}

	public StabilizeRPck(String data){
		super(data);
	}
	
	/**
	 * Get predecessor IP
	 * @return
	 */
	public String getIp(){
		return ip;
	}
	
	@Override
	public String getPck(){
		return super.getPck() + "\n" + ip;
	}
	
	@Override
	public boolean extract(String data){
		int i = data.indexOf("\n");
		
		if(i > 0){
			key = Key.load(data.substring(0, i));
			ip = data.substring(i);
		}
		
		return i > 0 && ip.length() > 0;
	}
}
