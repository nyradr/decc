package decc.dht;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Represent a DHT key
 * A key can be any non null/empty string
 * All keys is hashed with SHA-256
 * @author voyez
 *
 */
public class Key {
	private static int radix = 16;
	
	private BigInteger key;
	
	/**
	 * Get key as BigInteger
	 * @return
	 */
	public BigInteger getKey(){
		return key;
	}
	
	@Override
	public String toString(){
		return key.toString(radix);
	}
	
	/**
	 * Create new key
	 * @param key key value
	 * @return can return null when SHA-256 is not a valid algorithm
	 */
	public static Key create(String key){
		Key k = new Key();
		
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hkey = md.digest(key.getBytes());
			k.key = new BigInteger(hkey);
		}catch(Exception e){
			e.printStackTrace();
			k = null;
		}
		
		return k;
	}
	
	/**
	 * Load hash as key
	 * @param hkey hash (SHA-256)
	 * @return
	 */
	public static Key load(String hkey){
		Key k = new Key();
		k.key = new BigInteger(hkey, radix);
		return k;
	}
}
