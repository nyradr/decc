package decc.accounts;

import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Represent a DECC contact
 * @author nyradr
 */
public class Contact {
	
	protected String name;
	protected PublicKey publickey;
	
	/**
	 * Create contact
	 * @param name contact name
	 * @param pk public key
	 */
	public Contact(String name, PublicKey pk){
		this.name = name;
		publickey = pk;
	}
	
	/**
	 * get contact name
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the public key
	 * @return
	 */
	public PublicKey getPublic(){
		return publickey;
	}
	
	/**
	 * Verify a string signed for this account
	 * @param mess message to verify
	 * @param sign message signature
	 * @return true if the message is verified
	 */
	public boolean verifySign(String mess, String sign){
		boolean isverif = false;
		try{
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initVerify(publickey);
			
			sig.update(mess.getBytes());
			
			isverif = sig.verify(Base64.getDecoder().decode(sign.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isverif;
	}
}
