package decc.ui;

/**
 * Interface for user of the Decc protocol
 * @author nyradr
 */
public interface IDeccUser extends IComClb{
	
	/**
	 * Produce when new peer connect
	 * @param host peer IP
	 */
	public void onNewPeer(String host);
	
	/**
	 * Produce when peer disconnect
	 * @param host peer IP
	 */
	public void onPeerDeco(String host);
	
	/**
	 * Produce when the target is impossible to reach
	 * @param comid communication COMID
	 * @param target target name
	 */
	public void onComFail(String comid, String target);
	
	/**
	 * Produce when new road pass through you
	 * @param comid	conversation comid
	 * @param hosta A peer
	 * @param hostb B peer
	 */
	public void onNewRoad(String comid, String hosta, String hostb);
	
	/**
	 * Produce when road is destroy
	 * @param comid road comid
	 * @param hosta A peer
	 * @param hostb B peer
	 */
	public void onEroute(String comid, String hosta, String hostb);
	
}
