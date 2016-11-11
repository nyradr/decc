package decc.dht.packet;

import decc.dht.Key;
import decc.dht.ui.StoreFlags;

/**
 * Answer packet for a store request
 * @author nyradr
 */
public class StoreRPck extends KeyPck{
	
	private StoreFlags flag;
	
	public StoreRPck(Key k, StoreFlags flag) {
		super(k);
		this.flag = flag;
	}
	
	public StoreRPck(String args){
		super();
		extract(args);
	}
	
	public StoreFlags getFlag(){
		return flag;
	}
	
	@Override
	public String getPck(){
		return flag + super.getPck();
	}
	
	@Override
	public boolean extract(String args){
		if(args.length() > 1){
			flag = StoreFlags.fromChar((args.charAt(0)));
			
			return super.extract(args.substring(1));
		}
		
		return false;
	}
}
