package decc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import decc.ui.ICom;

/**
 * List of communications
 * @author nyradr
 */
class ComsList {
	
	private List<Communication> coms;	//storage
	
	/**
	 * ctor
	 */
	public ComsList(){
		this.coms = new ArrayList<Communication>();
	}
	
	/**
	 * Add new communication
	 * @param c communication instance
	 */
	public void add(Communication c){
		this.coms.add(c);
	}
	
	/**
	 * Remove communication<br>
	 * The communication is not closed
	 * @param c communication instance
	 */
	public void remove(Communication c){
		c.close();
		this.coms.remove(c);
	}
	
	/**
	 * Search for the communications with the comid
	 * @param comid comid to search
	 * @return
	 */
	public List<Communication> getComid(String comid){
		return (List<Communication>) this.coms.stream().filter(x -> x.getComid().equals(comid)).collect(Collectors.toList());
	}
	
	/**
	 * Search for the communication with this peer
	 * @param p peer to search
	 * @return
	 */
	public List<Communication> getPeer(Peer p){
		return (List<Communication>) this.coms.stream().filter(x -> x.getPeer() == p).collect(Collectors.toList());
	}
	
	/**
	 * Search for the couple comid/peer
	 * @param comid comid to search
	 * @param p peer to search
	 * @return list of communication found<br>
	 * 		if no error should be of size 1
	 */
	public List<Communication> getPeerComid(String comid, Peer p){
		return (List<Communication>) this.coms.stream()
				.filter(x -> x.getPeer() == p && x.getComid().equals(comid))
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the coms
	 * @return
	 */
	public List<Communication> getComs(){
		return coms;
	}

	public ICom[] getIComs(){
		ICom icl[] = new ICom[coms.size()];
		
		for(int i = 0; i < coms.size(); i++)
			icl[i] = this.coms.get(i);
		
		return icl;
	}
}
