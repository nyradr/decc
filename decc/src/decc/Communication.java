package decc;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Base64;
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
	 * Return true if encryption is enable
	 * @return
	 */
	public boolean isCryptoEnable(){
		return ctarget != null || sessionkey != null;
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
					
					if(sessionkey != null){
						crmode = "DES/ECB";
						key = sessionkey;
					}
				
					Cipher cip = Cipher.getInstance(crmode, "BC");
					cip.init(Cipher.ENCRYPT_MODE, key);
					
					emess = Base64.getEncoder().encodeToString(cip.doFinal(mess.getBytes()));
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}else
				emess = mess;
			
			peer.sendMess(new MessPck(comid, emess, "").getPck());
		}
	}
	
	/**
	 * Decrypt received message
	 * @param mess 
	 * @param upk
	 * @return
	 */
	public String receive(String mess, PrivateKey upk){
		String clear = "";
		
		if(ctarget != null){
			try{
				Key key = upk;
				String crmode = "RSA/ECB/PKCS1Padding";
			
				if(sessionkey != null){
					crmode = "DES/ECB/PKC5Padding";
					key = sessionkey;
				}
			
				Cipher cip = Cipher.getInstance(crmode, "BC");
				cip.init(Cipher.DECRYPT_MODE, key);
				
				for(byte b : cip.doFinal(
						Base64.getDecoder().decode(mess.getBytes())))
					clear += (char) b;
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}else
			clear = mess;
	
		return clear;
	}
}
