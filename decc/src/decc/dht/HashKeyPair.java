package decc.dht;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Represent a pair of key/value
 * The key is hashed with SHA-256 algorithm
 * @author nyradr
 */
public class HashKeyPair {

	private Key key;
	private String value;
	
	/**
	 * Get the hash of the key
	 * @return
	 */
	public Key getKey(){
		return key;
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
	public static HashKeyPair create(String key, String value){
		HashKeyPair kpv = new HashKeyPair();
		
		kpv.key = Key.create(key);
		if(kpv.key == null)	// just in case key creation return null
			kpv = null;
		else
			kpv.value = value;
		
		return kpv;
	}
	
	/**
	 * Create new HashKeyPair instance with key already hashed
	 * @param hkey key hashed
	 * @param value value to store
	 * @return
	 */
	public static HashKeyPair load(String hkey, String value){
		HashKeyPair kpv = new HashKeyPair();
		kpv.key = Key.load(hkey);
		kpv.value = value;
		
		return kpv;
	}
}
