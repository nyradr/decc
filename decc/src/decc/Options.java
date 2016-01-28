package decc;

/**
 * Options of the protocol
 * @author nyradr
 */
public class Options {
	
	private boolean useSteg;	// use of the steganographic transmission protocol
	private int		maxpeer;	// number max of peer allowed
	private int		maxRoads;	// number max of roads allowed
	
	public Options(){
		
	}
	
	public boolean loadFromFile(String file){
		return false;
	}
	
	public boolean useSteg(){
		return useSteg;
	}
	
	public void setUseSteg(boolean v){
		useSteg = v;
	}
	
	public int maxPeers(){
		return maxpeer;
	}
	
	public void setMaxPeers(int i){
		maxpeer = i;
	}
	
	public int maxRoads(){
		return maxRoads;
	}
	
	public void setMaxRoads(int i){
		maxRoads = i;
	}
}
