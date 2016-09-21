package decc.dht.packet;

import decc.dht.Value;
import decc.packet.IPacket;
import decc.packet.Packet;

/**
 * Store a value in a packet
 * @author nyradr
 */
class ValuePck implements IPacket {
	
	private Value val;
	
	public ValuePck(Value v){
		val = v;
	}
	
	public ValuePck(String data){
		extract(data);
	}

	@Override
	public String getPck() {
		String str = val.getDate().toGMTString();
		str += "\n" + val.getVal() + "\n" + val.getSign();
		
		return str;
	}

	@Override
	public boolean extract(String args) {
		int pos = args.indexOf("\n");
		
		
		
		
	}
	
}
