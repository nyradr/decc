package decc;

/**
 * Represent a communication between 2 user
 * @author nyradr
 *
 */
class Communication implements ICom{
	
	private String comid;	// communication comid
	private String target;	// name of the targer
	
	private Peer peer;		// first peer
	
	private boolean linked;	// true if a link is established to the target
	
	/**
	 * ctor
	 * @param comid communication comid
	 * @param peer first peer
	 */
	public Communication(String comid, String target, Peer peer){
		this.comid = comid;
		this.target = target;
		this.peer = peer;
		this.linked = false;
	}
	
	@Override
	public String getComid(){
		return comid;
	}
	
	/**
	 * @return first peer
	 */
	public Peer getPeer(){
		return peer;
	}
	
	public boolean isLinked(){
		return linked;
	}
	
	public void setLinked(boolean l){
		linked = l;
	}
	
	public String getTarget(){
		return target;
	}
	
	@Override
	public String getTargetName() {
		return target;
	}
	
	/**
	 * Génère un nouveau Comid
	 * @param target String definissant la cible
	 * @param from String definissant l'envoyeur
	 * @return nouveau Comid
	 */
	public static String generateComid(String target, String from){
		return target + from;
	}

	
}
