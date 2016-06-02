package decc.accounts;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account {
	
	private String name;
	private PublicKey pubkey;
	private PrivateKey privkey;
	
	/**
	 * Create account with private key (can decrypt)
	 * @param name
	 * @param pubk
	 * @param prvk
	 * @throws PGPException 
	 */
	public Account(String name, PublicKey pubk, PrivateKey prvk){
		this.name = name;
		
		pubkey = pubk;
		privkey = prvk;
	}
	
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
	public PublicKey getPublic(){
		return pubkey;
	}
	
	/**
	 * Get the public key encoded with base 64 encoder
	 * @return
	 */
	public String getPublicStr(){
		return Base64.getEncoder().encodeToString(pubkey.getEncoded());
	}
	
	/**
	 * Get the account private PGP key
	 * @return
	 */
	public PrivateKey getPrivate(){
		return privkey;
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
