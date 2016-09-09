package decc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * List of all the roads passing though this peer
 * @author nyradr
 *
 */
class RoadList {
	public Set<Road> roads;
	
	public RoadList(){
		this.roads = new HashSet<Road>();
	}
	
	/**
	 * Add road to the list
	 * @param r road
	 */
	public void add(Road r){
		this.roads.add(r);
	}
	
	/**
	 * Remove road from the list
	 * @param r
	 */
	public void remove(Road r){		
		this.roads.remove(r);
	}
	
	/**
	 * Get all the roads with the comid
	 * @param comid
	 * @return empty list if no road correspond to the comid
	 */
	public List<Road> getComid(String comid){
		return (List<Road>) this.roads.stream()
				.filter(x -> x.getComid().equals(comid))
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the roads passing by the peer
	 * @param p
	 * @return
	 */
	public List<Road> getPeer(Node p){
		return (List<Road>) this.roads.stream()
				.filter(x -> x.getA() == p || x.getB() == p)
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the roads with the couple comid/peer
	 * @param comid
	 * @param p
	 * @return
	 */
	public List<Road> getPeerComid(String comid, Node p){
		return (List<Road>) this.roads.stream()
				.filter(x -> (x.getA() == p || x.getB() == p) && x.getComid().equals(comid))
				.collect(Collectors.toList());
	}
	
	/**
	 * Get all the roads
	 * @return
	 */
	public Set<Road> getRoads(){
		return this.roads;
	}
}
