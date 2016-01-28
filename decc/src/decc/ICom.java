package decc;

/**
 * Interface for getting informations on a communication
 * @author nyradr
 */
public interface ICom {
	
	/**
	 * Get the communication comid
	 * @return
	 */
	public String getComid();
	
	/**
	 * Get the target name
	 * @return
	 */
	public String getTargetName();
}
