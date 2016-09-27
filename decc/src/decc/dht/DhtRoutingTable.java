package decc.dht;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represent the queries on DHT
 * @author nyradr
 */
public class DhtRoutingTable {

	private static final long DELAY = 60;
	
	private Map<Key, Map<Key, Long>> routes;
	private long lastClear;
	
	public DhtRoutingTable(){
		routes = new TreeMap<>();
		lastClear = Instant.now().getEpochSecond();
	}
	
	/**
	 * Apply delay on the routing table
	 * This will delete all out dated roads
	 * @param s
	 */
	private void applyDelay(long s){
		for(Key k : routes.keySet()){
			Map<Key, Long> rts = routes.get(k);
			
			for(Key v : rts.keySet()){
				Long delay =  rts.get(v);
				delay -= s;
				
				if(delay > 0)
					rts.put(v, delay);
				else
					rts.remove(v);
			}
			
			if(rts.isEmpty())
				routes.remove(k);
		}
	}
	
	/**
	 * Clean routing table from out dated elements
	 * FIXME concurrent access exception
	 */
	private void clean(){
		long ts = Instant.now().getEpochSecond();
		applyDelay(ts - lastClear);
		lastClear = ts;
	}
	
	/**
	 * Put value on the routing table
	 * Update out dated elements
	 * @param k Key to search
	 * @param v Node key
	 */
	public void put(Key k, Key v){
		//clean();
		
		Map<Key, Long> rts = routes.get(k);
		
		// key not exist
		if(rts == null){
			rts = new TreeMap<>();
			routes.put(k, rts);
		}
		
		rts.put(v, DELAY);
	}
	
	/**
	 * Get all nodes from Key
	 * @param k key
	 * @return
	 */
	public Set<Key> get(Key k){
		//clean();
		Map<Key, Long> rts = routes.get(k);
		Set<Key> ks = null;
		if(rts != null)	// key not found security
			ks = rts.keySet();
		
		return ks;
	}
}
