package decc.ui;

/**
 * Interface for communication callback
 * @author nyradr
 *
 */
public interface IComClb {
	
	/**
	 * Produce when new communication is linked
	 * @param comid communication comid
	 */
	public void onNewCom(String comid);
	
	/**
	 * Produce when communication is delinked
	 * @param comid communication comid
	 */
	public void onComEnd(String comid);
	
	/**
	 * Produce when message arrive to you
	 * @param comid communication comid
	 * @param mess message data
	 * @param verified true if the message is verified
	 */
	public void onMess(String comid, String mess, boolean verified);
}
