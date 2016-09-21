package decc.packet;

import decc.dht.Key;

/**
 * Packet for IP command
 * IP command is used to carry the IP address (or host name) of the target peer
 * and the DHT key of the sender
 * @author nyradr
 */
public class IpPck implements IPacket{

	private String ip;
	private Key id;
	
	public IpPck(String ip, Key k){
		this.ip = ip;
		this.id = k;
	}
	
	public IpPck(String args) {
		extract(args);
	}
	
	/**
	 * Get the peer IP
	 * @return
	 */
	public String getIp(){
		return ip;
	}
	
	/**
	 * Get the sender key
	 * @return
	 */
	public Key getKey(){
		return id;
	}
	
	@Override
	public String getPck() {
		return ip + "\n" + id.toString();
	}

	@Override
	public boolean extract(String args) {
		int i = args.indexOf("\n");
		if(i > 0 && i +1 < args.length()){
			ip = args.substring(0, i);
			id = Key.load(args.substring(i +1));
		}
		
		return i > 0 && args.length() - i > 0;
	}

	
	
}
