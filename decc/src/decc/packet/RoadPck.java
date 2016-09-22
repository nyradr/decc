package decc.packet;

/**
 * Packet for a road between 2 peers
 * @author nyradr
 *
 */
public class RoadPck extends Packet{
	
	private String comid;	// COMID
	private String ori;		// origin peer
	private String dest;	// destination peer
	
	public RoadPck(String args){
		extract(args);
	}
	
	public RoadPck(String comid, String ori, String dest){
		this.comid = comid;
		this.ori = ori;
		this.dest = dest;
	}
	
	@Override
	public String getPck(){
		return comid + "\n" + ori + "\n" + dest;
	}
	
	@Override
	public boolean extract(String args){
		String [] lines = new String[3];
		int ilines = 0;
		
		String line = "";
		
		for(char c : args.toCharArray()){
			if(c == '\n'){
				lines[ilines] = line;
				ilines++;
				line = "";
			}else
				line += c;
		}
		
		lines[ilines] = line;
			
		if(ilines == 2){
			this.comid = lines[0];
			this.ori = lines[1];
			this.dest = lines[2];
		}
		
		return ilines == 2;
	}
	
	/**
	 * Get road COMID
	 * @return
	 */
	public String getComid(){
		return comid;
	}
	
	/**
	 * Get road origin
	 * @return
	 */
	public String getOri(){
		return ori;
	}
	
	/**
	 * Get road target
	 * @return
	 */
	public String getDest(){
		return dest;
	}
	
}
