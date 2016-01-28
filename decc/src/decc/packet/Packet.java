package decc.packet;

/**
 * Base transmission packet
 * @author nyradr
 *
 */
public abstract class Packet {
	
	public abstract String getPck();
	
	public abstract boolean extract(String args);
	
	
}
