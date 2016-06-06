package decc.accounts;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account extends Contact{
	
	private PrivateKey privatekey;
	
	/**
	 * Create account with private key (can decrypt)
	 * @param name
	 * @param pubk
	 * @param prvk
	 * @throws PGPException 
	 */
	public Account(String name, PublicKey pubk, PrivateKey prvk){
		super(name, pubk);
		
		privatekey = prvk;
	}
	
	/**
	 * Get the public key encoded with base 64 encoder
	 * @return
	 */
	public String getPublicStr(){
		return Base64.getEncoder().encodeToString(publickey.getEncoded());
	}
	
	/**
	 * Get the account private PGP key
	 * @return
	 */
	public PrivateKey getPrivate(){
		return privatekey;
	}
	
	/**
	 * Generate message signature
	 * @param mess message to sign
	 * @return message signature or "" if the operation fail
	 */
	public String generateSign(String mess){
		String sign = "";
		
		try{
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initSign(privatekey);
			
			sig.update(mess.getBytes());
			
			sign = Base64.getEncoder().encodeToString(sig.sign());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sign;
	}
	
	/**
	 * Get the account public key signature
	 * @return
	 */
	public String getKeySign(){
		return generateSign(getPublicStr());
	}
	
	/**
	 * Create new account and generate key pair
	 * @param name user name
	 * @param size RSA key size
	 * @return account
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static Account create(String name, int size) throws NoSuchAlgorithmException, NoSuchProviderException{
		Security.addProvider(new BouncyCastleProvider());
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(size);
		
		KeyPair kp = kpg.generateKeyPair();
		
		return new Account(name, kp.getPublic(), kp.getPrivate());
	}
}
