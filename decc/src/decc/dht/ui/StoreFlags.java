package decc.dht.ui;

/**
 * DHT store answer flags
 * @author nyradr
 */
public enum StoreFlags {
	/**
	 * The storage of the key/value failed
	 */
	FAILURE (0x00),
	/**
	 * The storage of the key:value succeed
	 */
	SUCCESS (0x01);
	
	private char flag;
	
	private StoreFlags(int c){
		flag = (char) c;
	}
	
	public String toString(){
		return Character.toString(this.flag);
	}
	
	/**
	 * Get the Command value for the first character in the string
	 * @param s string
	 * @return command value
	 */
	public static StoreFlags parse(String s){
		if(s != null){
			if(s.length() > 0){
				char val = s.charAt(0);
				
				for(StoreFlags c : StoreFlags.values())
					if(val == c.flag)
						return c;
			}
		}
		
		return null;
	}
	
	public static StoreFlags fromChar(char c){
		return parse("" + c);
	}
}
