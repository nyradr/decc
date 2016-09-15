package decc;

/** 
 * Network command
 * @author nyradr
 */
enum Command{
	NONE		(0x00),		// No command
	IP			(0x01),		// peer IP and my DHT ID	(IP, ID)
	BRCAST		(0x02),		// broadcast message asking ip for connection	TODO : remove (useless with DHT)
	
	ROUTE		(0x10),		// trace a road	(COMID, ORI, DEST)
	EROUTE		(0x11),		// abort road (COMID)
	EROUTEPDC	(0x12),		// one peer of the road disconnected (COMID, FLAG)
	
	MESS		(0x20),		// send a message through a road (COMID, MESS)
	
	
	DNOTIF		(0x30),		// send DHT notify request (ID)
	DSTABI		(0x31),		// ask for predecessor
	DSTABIR		(0x32),		// predecessor answer (ID)
	DFINDSUC	(0x33),		// send find successor request (ID)
	DFINDSUCR	(0x34);		// answer for find successor (ID, IP)
	
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
