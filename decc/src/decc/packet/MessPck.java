package decc.packet;

/**
 * Packet for a message
 * @author nyradr
 *
 */
public class MessPck extends Packet{
	/**
	 * No command
	 */
	public static final int CMD_NONE = 0;
	/**
	 * The Route command found his target
	 */
	public static final int CMD_CFND = 1;
	
	private String comid;
	private int cmd;
	private String data;
	
	/**
	 * Create new message packet
	 * @param comid comid of the conversation
	 * @param cmd internal command, could be anythings but some values are reserved (see MessPck.CMD_ constants)
	 * @param data data to send
	 */
	public MessPck(String comid, int cmd, String data){
		this.comid = comid;
		this.cmd = cmd;
		this.data = data;
	}
	
	/**
	 * Create new message packet without internal command
	 * @param comid comid of the conversation
	 * @param data data to send
	 */
	public MessPck(String comid, String data){
		this.comid = comid;
		this.cmd = CMD_NONE;
		this.data = data;
	}
	
	/**
	 * Extract message packet from string
	 * @param data
	 */
	public MessPck(String data){
		extract(data);
	}
	
	public String getData(){
		return this.data;
	}
	
	public String getComid(){
		return this.comid;
	}
	
	@Override
	public String getPck(){
		return this.comid + "\n" +  (char) cmd + this.data;
	}

	@Override
	public boolean extract(String args) {
		int index = args.indexOf("\n");
		
		if(index >= 0 && args.length() >= index +2){
			this.comid = args.substring(0, index);
			
			this.cmd = args.charAt(index +1);
			try{
				this.data = args.substring(index +2);
			}catch (Exception e){
				this.data = "";
			}
		}else
			this.comid = data;
		return false;
	}
}
