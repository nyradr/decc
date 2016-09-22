package decc.packet;

/**
 * Packet for the eroute peer deconnection
 * @author nyradr
 */
public class EroutedPck extends Packet{

	private String comid;
	private boolean flag;
	
	public EroutedPck(String comid, boolean flag) {
		this.comid = comid;
		this.flag = flag;
	}
	
	public EroutedPck(String data){
		extract(data);
	}
	
	/**
	 * Get route COMID
	 * @return
	 */
	public String getComid(){
		return comid;
	}
	
	/**
	 * Get flag
	 * @return true means try to retrace the road
	 */
	public boolean getFlag(){
		return flag;
	}
	
	@Override
	public String getPck() {
		return (flag? "1" : "0") + comid;
	}

	@Override
	public boolean extract(String args) {
		if(args.length() > 1){
			flag = args.charAt(0) == '1';
			comid = args.substring(1);
		}
		return args.length() > 1;
	}

}
