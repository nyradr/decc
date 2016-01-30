package decc.secu;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

/**
 * RSA hight level implementation
 * @author nyradr
 *
 */
public class RSA {
	
	/**
	 * Generate RSA key pair
	 * @param length length of the key
	 * @return RSA key pair
	 * @throws Exception
	 */
	public static KeyPair generateKey(int length) throws Exception{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(length);
		return kpg.generateKeyPair();
	}
	
	/**
	 * Encrypt the data with RSA
	 * @param data data to encrypt
	 * @param key RSA public key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, Key key) throws Exception{
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(data);
	}
	
	/**
	 * Decrypt data with RSA
	 * @param data data to decrypt
	 * @param key RSA private key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, Key key) throws Exception{
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(data);
	}
}
