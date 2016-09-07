package decc.dht;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Represent a pair of key/value
 * The key is hashed with SHA-256 algorithm
 * @author nyradr
 */
public class HashKeyPair {

	private String keyhash;
	private String value;
	
	/**
	 * Get the hash of the key
	 * @return
	 */
	public String getKeyHash(){
		return keyhash;
	}
	
	/**
	 * Get the value
	 * @return
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Create new HashKeyPair
	 * @param key key to store (non hashed)
	 * @param value value to store
	 * @return can return null when SHA-256 is not a valid hash algorithm
	 */
	public static HashKeyPair createNew(String key, String value){
		HashKeyPair kpv = new HashKeyPair();
		
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hkey = md.digest(key.getBytes());
			BigInteger bihkey = new BigInteger(hkey);
			
			kpv.keyhash = bihkey.toString(16);
			kpv.value = value;
		}catch(Exception e){
			e.printStackTrace();
			kpv = null;
		}
		
		return kpv;
	}
	
	/**
	 * Create new HashKeyPair instance with key already hashed
	 * @param hkey key hashed
	 * @param value value to store
	 * @return
	 */
	public static HashKeyPair create(String hkey, String value){
		HashKeyPair kpv = new HashKeyPair();
		kpv.keyhash = hkey;
		kpv.value = value;
		
		return kpv;
	}
}
