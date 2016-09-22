package decc.dht.packet;

import java.util.Date;

import decc.dht.Value;
import decc.packet.Packet;

/**
 * Store a value in a packet
 * @author nyradr
 */
abstract class ValuePck extends Packet {
	
	private Value val;
	
	public ValuePck(Value val){
		this.val = val;
	}
	
	public ValuePck(String data){
		extract(data);
	}
	
	/**
	 * Empty constructor
	 * super.extract MUST be called by the top class
	 */
	protected ValuePck(){}
	
	public Value getVal(){
		return val;
	}

	@Override
	public String getPck() {
		String str = val.getDate().toGMTString();
		str += "\n" + val.getVal() + "\n" + val.getSign();
		return str;
	}

	@Override
	public boolean extract(String args) {
		// TODO securisation
		
		int begin = 0;
		int end = args.indexOf("\n");
		Date post = new Date(Date.parse(args.substring(begin, end)));
		
		begin = end +1;
		end = args.indexOf("\n", begin);
		String value = args.substring(begin, end);
		
		String sign = args.substring(end +1);
		
		val = new Value(post, value, sign);
		return true;
	}
	
}
