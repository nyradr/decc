package decc.secu;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AES {
	public static int LEN_128 = 128;
	public static int LEN_256 = 256;
	public static int LEN_512 = 512;
	
	/**
	 * Generate new secret AES key
	 * @param len length of the key (must be 128, 256, 512)
	 * @return new AES key
	 * @throws Exception
	 */
	public static SecretKey generateKey(int len) throws Exception{
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		kg.init(len);
		return kg.generateKey();
	}
	
	/**
	 * Encrypt byte array with AES
	 * @param data byte array to encrypt
	 * @param key AES key
	 * @return data encrypted
	 * @throws Exception
	 */
	public static byte[] encrypt(byte [] data, SecretKey key) throws Exception{
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(data);
	}
	
	/**
	 * Decrypt byte array encoded with AES
	 * @param data data to decrypt
	 * @param key AES key
	 * @return data decrypted
	 * @throws Exception
	 */
	public static byte[] decrypt(byte [] data, SecretKey key) throws Exception{
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(data);
	}
}
