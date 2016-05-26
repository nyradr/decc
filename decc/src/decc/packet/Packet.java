package decc.packet;

/**
 * Base transmission packet
 * @author nyradr
 *
 */
public abstract class Packet {
	
	/**
	 * Get String representation of the packet
	 * @return
	 */
	public abstract String getPck();
	
	/**
	 * Extract packet from String
	 * @param args
	 * @return
	 */
	public abstract boolean extract(String args);
	
	
}
