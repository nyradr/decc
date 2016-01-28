package decc;

/** 
 * Network command
 * @author nyradr
 */
enum Command{
	NONE		(0x00),		// No command
	IP			(0x01),		// Ip of the peer
	BRCAST		(0x02),		// broadcast message asking ip for connection
	
	ROUTE		(0x10),		// trace a road
	EROUTE		(0x11),		// abort road
	EROUTEPDC	(0x12),		// one peer of the road disconnected
	
	MESS		(0x20);		// send a message through a road
	
	private final char cmd;
	
	private Command(int cmd) {
		this.cmd = (char) cmd;
	}
	
	public String toString(){
		return Character.toString(this.cmd);
	}
	
	/**
	 * Get the Command value for the first character in the string
	 * @param s string
	 * @return command value
	 */
	public static Command parse(String s){
		if(s != null){
			if(s.length() > 0){
				char val = s.charAt(0);
				
				for(Command c : Command.values())
					if(val == c.cmd)
						return c;
			}
		}
		
		return null;
	}
	
}
