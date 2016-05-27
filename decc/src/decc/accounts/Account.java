package decc.accounts;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account {
	
	private String name;
	private PGPPublicKeyRing publicKey;
	private PGPSecretKeyRing privateKey;
	
	/**
	 * Get the account name
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the account public PGP key
	 * @return
	 */
	public PGPPublicKeyRing getPublic(){
		return publicKey;
	}
	
	/**
	 * Get the account private PGP key
	 * @return
	 */
	public PGPSecretKeyRing getPrivate(){
		return privateKey;
	}
	
	/**
	 * Encrypt data with the account public key
	 * @param data data to encrypt
	 * @return
	 */
	public String encrypt(String data){
		return "";
	}
	
	/**
	 * Decrypt data with the account private key
	 * @param data data to decrypt
	 * @return decrypted data
	 */
	public String decrypt(String data){
		return "";
	}
	
	/**
	 * Sign data with this account
	 * @param data data to sign
	 * @return signature
	 */
	public String sign(String data){
		return "";
	}
	
	/**
	 * Create new account and generate PGP key pair
	 * @param name
	 * @return
	 */
	public static Account create(String name){
		return null;
	}
	
	/**
	 * Load account informations from PGP keys ring
	 * @param name 
	 * @param pubs
	 * @param privs
	 * @return
	 */
	public static Account load(String name, PGPPublicKeyRingCollection pubs, PGPSecretKeyRingCollection privs){
		return null;
	}
	
	public static Account load(String name, PGPPublicKeyRingCollection pubs){
		return null;
	}
}
