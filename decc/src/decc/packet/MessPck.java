package decc.packet;

/**
 * Packet for a message
 * @author nyradr
 *
 */
public class MessPck extends Packet{
	/**
	 * No command, normal message
	 */
	public static final int CMD_NONE = 0x01;
	/**
	 * The Route command found his target
	 */
	public static final int CMD_CFND = 0x02;
	/**
	 * Public key exchange
	 */
	public static final int CMD_PK = 0x03;
	/**
	 * Diffie-Hellman key exchange
	 */
	public static final int CMD_DH = 0x04;
	
	private String comid;
	private int cmd;
	private String data;
	private String sign;
	
	/**
	 * Create new message packet
	 * @param comid comid of the conversation
	 * @param cmd internal command, could be anythings but some values are reserved (see MessPck.CMD_ constants)
	 * @param data data to send
	 * @param sign data signature
	 */
	public MessPck(String comid, int cmd, String data, String sign){
		initC(comid, cmd, data, sign);
	}
	
	/**
	 * Create new message packet without internal command
	 * @param comid comid of the conversation
	 * @param data data to send
	 * @param sign data signature
	 */
	public MessPck(String comid, String data, String sign){
		initC(comid, CMD_NONE, data, sign);
	}
	
	private void initC(String comid, int cmd, String data, String sign){
		this.comid = comid;
		this.cmd = cmd;
		this.data = data;
		this.sign = sign;
	}
	
	/**
	 * Extract message packet from string
	 * @param data
	 */
	public MessPck(String data){
		extract(data);
	}
	
	/**
	 * Get message
	 * @return
	 */
	public String getData(){
		return data;
	}
	
	public String getSign(){
		return sign;
	}
	
	/**
	 * Get road COMID
	 * @return
	 */
	public String getComid(){
		return comid;
	}
	
	/**
	 * Get message internal command
	 * @return
	 */
	public int getCommand(){
		return cmd;
	}
	
	@Override
	public String getPck(){
		return comid + "\n" +  (char) cmd + data + "\n" + sign;
	}

	@Override
	public boolean extract(String args) {
		int indexfst = args.indexOf("\n");
		int indexlst = args.lastIndexOf("\n");
		
		if(indexfst >= 0 && args.length() >= indexfst +2){
			comid = args.substring(0, indexfst);
			
			cmd = args.charAt(indexfst +1);
			try{
				data = args.substring(indexfst +2, indexlst);
				sign = args.substring(indexlst +1);
			}catch (Exception e){
				data = "";
				sign = "";
			}
		}else
			comid = data;
		return false;
	}
}
