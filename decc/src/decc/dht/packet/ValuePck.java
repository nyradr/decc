package decc.dht.packet;

import java.util.Date;

import decc.dht.Value;
import decc.packet.IPacket;

/**
 * Store a value in a packet
 * @author nyradr
 */
class ValuePck extends Value implements IPacket {
	
	public ValuePck(Date post, String val, String s){
		super(post, val, s);
	}
	
	public ValuePck(String data){
		extract(data);
	}

	@Override
	public String getPck() {
		String str = post.toGMTString();
		str += "\n" + value + "\n" + sign;
		return str;
	}

	@Override
	public boolean extract(String args) {
		// TODO securisation
		
		int begin = 0;
		int end = args.indexOf("\n");
		post = new Date(Date.parse(args.substring(begin, end)));
		
		begin = end +1;
		end = args.indexOf("\n", begin);
		value = args.substring(begin, end);
		
		sign = args.substring(end +1);
		return true;
	}
	
}
