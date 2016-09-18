package decc.dht;

import java.math.BigInteger;

/**
 * Current DHT node used by the program
 * @author nyradr
 */
public abstract class CurrentNode extends Node{

	protected Key successor;
	protected Key predecessor;
	
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
			if(finger.compareTo(key.getKey()) > 0 && finger.compareTo(key.getKey()) < 0)
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
	
	
}
