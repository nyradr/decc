package decc;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Diffie-Hellman DES key exchange
 * @author nyradr
 */
class DiffieHellman {
	
	private static final int keysize = 1024;
	private KeyPair dekp;
	
	/**
	 * Initialize the key exchange
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public DiffieHellman() throws NoSuchAlgorithmException, NoSuchProviderException{
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH", "BC");
		kpg.initialize(keysize);
		
		dekp = kpg.generateKeyPair();
	}
	
	/**
	 * Encode the Diffie-Hellman public key to string
	 * @return
	 */
	public String encodePublic(){
		return Base64.getEncoder().encodeToString(
				dekp.getPublic().getEncoded());
	}
	
	/**
	 * Receive the target public key and generate the secret key
	 * @param pk target encoded public key
	 * @return generated DES public key or null
	 */
	public Key receivePublic(String pk){
		try {
			// extract public key
			KeyFactory bkf = KeyFactory.getInstance("DH", "BC");
			X509EncodedKeySpec ks = new X509EncodedKeySpec(
					Base64.getDecoder().decode(pk.getBytes()));
			PublicKey bpk = bkf.generatePublic(ks);
			
			// key agreement
			KeyAgreement ka = KeyAgreement.getInstance("DH", "BC");
			ka.init(dekp.getPrivate());
			ka.doPhase(bpk, true);
			
			// DES key factory
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES", "BC");
			DESKeySpec dks = new DESKeySpec(ka.generateSecret());
			return skf.generateSecret(dks);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
