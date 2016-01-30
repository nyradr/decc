package decc.secu;

import java.security.PrivateKey;
import java.security.PublicKey;

public class DsaRsa {
	
	/**
	 * Sign message with DSA via RSA
	 * @param text message to sign
	 * @param key RSA private key
	 * @return message signature
	 * @throws Exception
	 */
	public static byte[] sign(byte[] text, PrivateKey key) throws Exception{		
		return RSA.encrypt(Hash.sha512(text), key);	
	}
	
	/**
	 * Verify the message with DSA via RSA
	 * @param text message to verify
	 * @param sign message signature
	 * @param key RSA public key
	 * @return true if the message is verified
	 * @throws Exception
	 */
	public static boolean verify(byte[] text, byte[] sign, PublicKey key) throws Exception{ 	
		byte [] ns = RSA.decrypt(sign, key);
		byte [] ha = Hash.sha512(text);
		
		for(int i = 0; i < ns.length; i++)
			if(i < ha.length)
				if(ns[i] != ha[i])
					return false;
		
		return true;
	}
}
