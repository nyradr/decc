package decc.dht.packet;

import decc.dht.Key;
import decc.dht.Value;

/**
 * Packet storing a key and a value
 * @author nyradr
 */
class KeyValuePck extends ValuePck{
	
	private Key key;
	
	public KeyValuePck(Key k, Value v) {
		super(v);
		key = k;
	}
	
	public KeyValuePck(String data){
		extract(data);
	}
	
	/**
	 * Get the key
	 * @return
	 */
	public Key getKey(){
		return key;
	}
	
	@Override
	public String getPck(){
		return key.toString() + "\n" + super.getPck();
	}

	@Override
	public boolean extract(String data){
		int pos = data.indexOf("\n");
		
		if(pos > 0){
			key = key.load(data.substring(0, pos));
			return super.extract(data.substring(pos +1));
		}
		
		return false;
	}
}
