package decc.accounts;

import java.security.Key;
import java.security.PublicKey;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

/**
 * Represent a DECC contact
 * @author nyradr
 */
public class Contact {
	
	private String name;
	private PublicKey publickey;
	private Key sessionKey;
	
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
	 * Get the session key
	 * @return
	 */
	public Key getSessionKey(){
		return sessionKey;
	}
	
	/**
	 * Set a new session key
	 * @param k
	 */
	public void setSessionKey(Key k){
		sessionKey = k;
	}
}
