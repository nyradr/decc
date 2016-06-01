package decc;

import java.security.Key;
import java.security.PrivateKey;
import java.util.concurrent.LinkedBlockingDeque;

import javax.crypto.Cipher;

import decc.accounts.Contact;
import decc.packet.MessPck;
import decc.ui.ICom;
import decc.ui.IComClb;

/**
 * Represent a communication between 2 user
 * @author nyradr
 *
 */
class Communication implements ICom{
	
	private String comid;	// communication comid
	private String target;	// name of the target
	private Contact ctarget; // target as a contact
	private int cstate; 	// contact state
	private Key sessionkey;	// communication session key
	
	private Peer peer;		// first peer
	
	private boolean linked;	// true if a link is established to the target
	
	private IComClb clb;
	
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
	
	/**
	 * Close the communication<br>
	 * Send ERoute message to the peer and set linked to false
	 */
	public void close(){
		peer.sendEroute(comid);
		linked = false;
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
	
	public Contact getTargetContact(){
		return ctarget;
	}
	
	public void setTargetContact(Contact c){
		ctarget = c;
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

	@Override
	public void send(String mess) {
		if(linked){
			String emess = "";
			
			if(ctarget != null){
				try{
					Key key = ctarget.getPublic();
					String crmode = "RSA/ECB/PKCS1Padding";
					
					if(sessionkey == null){
						crmode = "DES/ECB/PKCS5Padding";
						key = sessionkey;
					}
				
					Cipher cip = Cipher.getInstance(crmode, "BC");
					cip.init(Cipher.ENCRYPT_MODE, key);
					
					for(byte b : cip.doFinal(mess.getBytes()))
						emess += (char) b;
				}catch(Exception e){
					e.printStackTrace();
				}
			}else
				emess = mess;
			
			peer.sendMess(new MessPck(comid, emess).getPck());
		}
	}
}
