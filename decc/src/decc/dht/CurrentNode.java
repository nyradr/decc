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
	 *  Find the successor of key k
	 * @param k
	 * @return
	 */
	protected abstract Key findSuccessor(Key k);
	
	/**
	 * Notify the node with key k that we are his predecessor
	 * @param k
	 */
	protected abstract void notify(Key k);
	
}
