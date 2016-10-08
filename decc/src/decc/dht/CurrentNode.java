package decc.dht;

import java.math.BigInteger;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
	protected Map<Key, IDhtClb> reqclbs;		// request callbacks
	
	private Timer stabilizeTimer;			// timer for frequent stabilize method
	
	private Map<Key, Value> keys;	// keys stored in this node
	// key storage range : [predecessor, successor]
	
	protected CurrentNode(Key k){
		nodesroads = new DhtRoutingTable();
		ksroads = new DhtRoutingTable();
		klroads = new DhtRoutingTable();
		keys = new TreeMap<>();
		
		// create empty DHT ring
		key = k;
		predecessor = null;
		successor = key;
		
		// network stabilization
		stabilizeTimer = new Timer();
		stabilizeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				stabilize();
			}
		}, 60000, 60000);
	}
	
	/**
	 * Return true if the DHT ring is empty
	 * The ring is considered empty if the successor is the node key and the predecessor is null
	 * @return
	 */
	protected boolean isEmptyRing(){
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
	protected Key findSuccessor(Key id) {
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
	protected void notify(Key id) {
		if (predecessor == null ||
			(id.getKey().compareTo(predecessor.getKey()) > 0 && id.getKey().compareTo(key.getKey()) < 0))
			predecessor = id;
		
		System.out.println("Predecessor : " + id.toString());
	}
	
	/**
	 * Try to store a key/value pair into the local key storage
	 * If the key is already stored the new value must be
	 * verified by the old key
	 * @param k key to store
	 * @param v value to store
	 * @return true is the key/value pair is succefuly stored
	 */
	protected boolean tryStore(Key k, Value v){
		boolean suc = true;
		
		if(keys.containsKey(k)){
			// test consistency
			// TODO signature verification
			
			keys.put(k, v);
		}else{
			// no keys already stored
			keys.put(k, v);
		}
		
		return suc;
	}
	
	/**
	 * Try to get a key locally stored
	 * @param k key to look for
	 * @return the value stored or NULL
	 */
	protected Value tryLookup(Key k){
		return keys.get(k);
	}
	
	/**
	 * Stabilize the network
	 * Ask to the successor about its predecessor and verify consistency
	 */
	public abstract void stabilize();
	
	@Override
	public abstract void store(IDhtClb clb, Key k, Value v);
	
	@Override
	public abstract void lookup(IDhtClb clb, Key k);
}
