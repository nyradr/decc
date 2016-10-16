package decc;

/** 
 * Network command
 * @author nyradr
 */
enum Command{
	// basic networking
	NONE		(0x00),		// No command
	IP			(0x01),		// peer IP and my DHT ID	(IP, ID)
	
	// routing
	ROUTE		(0x10),		// trace a road	(COMID, ORI, DEST)
	EROUTE		(0x11),		// abort road (COMID)
	EROUTEPDC	(0x12),		// one peer of the road disconnected (COMID, FLAG)
	
	// messaging
	MESS		(0x20),		// send a message through a road (COMID, MESS)
	
	// DHT
	DNOTIF		(0x30),		// send DHT notify request (ID)
	DSTABI		(0x31),		// ask for predecessor
	DSTABIR		(0x32),		// predecessor answer (ID)
	DFINDSUC	(0x33),		// send find successor request (ID)
	DFINDSUCR	(0x34),		// answer for find successor (ID, IP)
	DSTORE		(0x35),		// store key/value in the DHT (KEY, VAL)
	DSTORER		(0x36),		// notify if the storage success or fail (KEY, FLAG)
	DLOOKUP		(0x37),		// search for key (KEY)
	DLOOKUPR	(0x38),		// answer a lookup demand (KEY, VAL/NULL)
	DREPLICA	(0x39);		// replication of a key (KEY, VAL)
	
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
