package decc.accounts;

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
}
