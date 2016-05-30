package decc.accounts;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

/**
 * Represent a DECC contact
 * @author nyradr
 */
public class Contact {
	
	private String name;
	private PGPPublicKeyRing publickey;
	
	/**
	 * Create contact
	 * @param name contact name
	 * @param pk public key
	 */
	public Contact(String name, PGPPublicKeyRing pk){
		
	}
	
	/**
	 * get contact name
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	public PGPPublicKeyRing getKeyRing(){
		return publickey;
	}
	
	/**
	 * Get contact public key (for encryption)
	 * @return
	 */
	public PGPPublicKey getPublicKeyEnc(){
		return publickey.getPublicKey();
	}
}
