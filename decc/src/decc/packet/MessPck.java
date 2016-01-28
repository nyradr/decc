package decc.packet;

/**
 * Packet for a message
 * @author nyradr
 *
 */
public class MessPck extends Packet{
	
	private String comid;
	private String data;
	
	public MessPck(String comid, String data){
		this.comid = comid;
		this.data = data;
	}
	
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
		return this.comid + "\n" +  this.data;
	}

	@Override
	public boolean extract(String args) {
		int index = args.lastIndexOf("\n");
		
		if(index >= 0){
			this.comid = args.substring(0, index);
			this.data = args.substring(index +1);
		}else
			this.comid = data;
		return false;
	}
}
