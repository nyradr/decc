package decc;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.concurrent.LinkedBlockingDeque;

import javax.crypto.Cipher;

import decc.accounts.AccountsManager;
import decc.accounts.Contact;
import decc.options.Crypto;
import decc.packet.EroutedPck;
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
	
	private AccountsManager accman; // account manager
	
	private DiffieHellman dekey;
	
	private Node peer;		// first peer
	
	private boolean linked;	// true if a link is established to the target
	
	private IComClb clb;
	
	/**
	 * ctor
	 * @param comid communication comid
	 * @param peer first peer
	 */
	public Communication(String comid, String target, Node peer, AccountsManager accman, IComClb clb){
		this.comid = comid;
		this.target = target;
		this.peer = peer;
		this.linked = false;
		this.accman = accman;
		this.clb = clb;
		
		this.dekey = new DiffieHellman();
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
	public Node getPeer(){
		return peer;
	}
	
	/**
	 * Return true if the communication is linked to the target
	 * @return
	 */
	public boolean isLinked(){
		return linked;
	}
	
	/**
	 * Define the communication linked state
	 * @param l
	 */
	public void setLinked(boolean l){
		if(l != linked){
			if(l)
				clb.onNewCom(comid);
			else
				clb.onComEnd(comid);
		}
			
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
		return accman.getContact(target);
	}
	
	/**
	 * Return true if encryption is enable
	 * @return
	 */
	public boolean isCryptoEnable(){
		return dekey.getSecret() != null;
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
			if(dekey.getSecret() != null){
				try{
					Cipher cip = Cipher.getInstance(Crypto.CONV_ENC, Crypto.Provider);
					cip.init(Cipher.ENCRYPT_MODE, dekey.getSecret());
					
					emess = Base64.getEncoder().encodeToString(cip.doFinal(mess.getBytes()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else
				emess = mess;
			
			String sign = accman.getUser().generateSign(mess);
			
			peer.sendMess(new MessPck(comid, emess, sign));
		}
	}
	
	/**
	 * Decrypt received message
	 * @param mess received message
	 * @param sign message signature
	 */
	public void receive(String mess, String sign){
		String clear = "";
		
		if(dekey.getSecret() != null){
			try{
				Cipher cip = Cipher.getInstance(Crypto.CONV_ENC, Crypto.Provider);
				cip.init(Cipher.DECRYPT_MODE, dekey.getSecret());
				
				clear = new String(cip.doFinal(Base64.getDecoder().decode(mess.getBytes())));
			}catch(Exception e){
				e.printStackTrace();
			}
		}else
			clear = mess;
		
		boolean verified = accman.getContact(target).verifySign(clear, sign);
		
		clb.onMess(comid, clear, verified);
	}
	
	/**
	 * Receive a diffie-hellman key
	 * @param key
	 */
	public void receiveDh(String key, String sign){
		if(accman.getContact(target).verifySign(key, sign))
			dekey.receivePublic(key);
		else
			System.out.println("\tDH unverified");
	}
	
	public void startDh(){
		String depk = dekey.encodePublic();
		String sign = accman.getUser().generateSign(depk);
		MessPck mpck = new MessPck(comid, MessPck.CMD_DH, depk, sign);
		peer.sendMess(mpck);
	}
}
