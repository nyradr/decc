package decc.secu;

import java.security.MessageDigest;

/**
 * Hash methods
 * @author nyradr
 */
public class Hash {
	
	/**
	 * General hash function
	 * @param buff data to hash
	 * @param algo hash algorithm to use
	 * @return hash of data
	 * @throws Exception
	 */
	public static byte[] hash(byte [] buff, String algo) throws Exception{
		MessageDigest md = MessageDigest.getInstance(algo);
		return md.digest(buff);
	}
	
	/**
	 * Hash message with the SHA-512 algorithm
	 * @param buff data to hash
	 * @return data hashed
	 * @throws Exception
	 */
	public static byte[] sha512(byte [] buff) throws Exception{
		return hash(buff, "sha-512");
	}
}
