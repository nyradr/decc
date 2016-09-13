package decc.dht;

import java.util.HashSet;
import java.util.Set;

/**
 * A route on the DHT
 * @author nyradr
 */
public class DhtRoute {

	private static final int TIMER = 60000;
	
	private Key node;
	private int delay;
	
	/**
	 * Create new route from node n
	 * @param n node key
	 */
	public DhtRoute(Key n) {
		node = n;
		restart();
	}
	
	/**
	 * Get the node key
	 * @return
	 */
	public Key getNode(){
		return node;
	}
	
	/**
	 * Subtract ms from remaining delay
	 * @param ms time to substract
	 * @return true if the route is alive
	 */
	public boolean delay(int ms){
		delay -= ms;
		return delay > 0;
	}
	
	/**
	 * Restart delay counter
	 */
	public void restart(){
		delay = TIMER;
	}
}
