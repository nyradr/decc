package decc.dht;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import decc.dht.ui.IDht;
import decc.dht.ui.IDhtClb;

/**
 * Current DHT node used by the program
 * @author nyradr
 */
public abstract class CurrentNode extends Node implements IDht{

	protected Key successor;
	protected Key predecessor;
	protected DhtRoutingTable nodesroads;	// nodes lookup roads
	protected DhtRoutingTable ksroads;		// key store roads
	protected DhtRoutingTable klroads;		// key lookup roads
	
	private Map<Key, Value> keys;	// keys stored in this node
	// key storage range : [predecessor, successor]
	
	public CurrentNode(Key k){
		nodesroads = new DhtRoutingTable();
		ksroads = new DhtRoutingTable();
		klroads = new DhtRoutingTable();
		keys = new TreeMap<>();
		
		// create empty DHT ring
		key = k;
		predecessor = null;
		successor = key;
	}
	
	/**
	 * Return true if the DHT ring is empty
	 * The ring is considered empty if the successor is the node key and the predecessor is null
	 * @return
	 */
	public boolean isEmptyRing(){
		return successor.equals(key) && predecessor == null;
	}
	
	/**
	 * Calculate finger[k]
	 * (n + 2^(k-1)) % 2^m
	 * @param k k belong to [1, m]
	 * @return
	 */
	protected BigInteger finger(int k){
		BigInteger res = new BigInteger("2");
		res = res.pow(k -1);
		res = key.getKey().add(res);
		BigInteger mod = new BigInteger("2");
		mod = mod.pow(m);
		
		return res.mod(mod);
	}
	
	/**
	 * Calculate the closest preceding node for the key id
	 * @param id key to calculate
	 * @return id of closest node
	 */
	protected BigInteger closestPrecedingNode(Key id){
		for(int k = m; k >= 1; k--){
			BigInteger finger = finger(k);
			
			if(finger != null)
				if(finger.compareTo(key.getKey()) > 0 && finger.compareTo(id.getKey()) < 0)
					return finger;
		}
		
		return key.getKey();
	}
	

	/**
	 * Find the successor of key
	 */
	public Key findSuccessor(Key id) {
		Key suc;
		
		if(	id.getKey().compareTo(key.getKey()) > 0
			&& id.getKey().compareTo(successor.getKey()) <= 0)
			suc = successor;
		else
			// /!\ need to send find_successor request on this key 
			suc = Key.load(closestPrecedingNode(id));
		
		return suc;
	}
	
	/**
	 * id may be or predecessor
	 */
	public void notify(Key id) {
		if (predecessor == null ||
			(id.getKey().compareTo(predecessor.getKey()) > 0 && id.getKey().compareTo(key.getKey()) < 0))
			predecessor = id;
		
		System.out.println("Predecessor : " + id.toString());
	}
	
	@Override
	public void store(IDhtClb clb, Key k, Value v){
		
	}
	
	@Override
	public void lookup(IDhtClb clb, Key k){
		
	}
}
