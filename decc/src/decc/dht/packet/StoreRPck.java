package decc.dht.packet;

import decc.dht.Key;

/**
 * Answer packet for a store request
 * @author nyradr
 */
public class StoreRPck extends KeyPck{

	public static final char FLAG_SUCCESS = 1;
	public static final char FLAG_FAILURE = 0;
	
	private char flag;
	
	public StoreRPck(Key k, char flag) {
		super(k);
	}
	
	public StoreRPck(String args){
		super();
		extract(args);
	}
	
	public char getFlag(){
		return flag;
	}
	
	@Override
	public String getPck(){
		return flag + super.getPck();
	}
	
	@Override
	public boolean extract(String args){
		if(args.length() > 1){
			flag = args.charAt(0);
			
			return super.extract(args.substring(1));
		}
		
		return false;
	}
}
