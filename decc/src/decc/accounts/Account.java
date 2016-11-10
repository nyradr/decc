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
import java.util.Date;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import decc.accounts.ui.ContactStatus;
import decc.dht.Key;
import decc.dht.Value;
import decc.dht.ui.IDht;
import decc.options.Crypto;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account extends Contact{
	/**
	 * Callback produced when the key is stored in the DHT
	 * @author nyradr
	 */
	private class PkStoreClb extends DHTCallClb{
		@Override
		public void onStore(Key k, char flag){
			
		}
	}
	
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
		status = ContactStatus.TRUSTED;
		privatekey = prvk;
		dhtclb = new PkStoreClb();
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
	 * Store the public the in the DHT
	 * @param dht DHT instance
	 */
	public void storePublic(IDht dht){
		String pkstr = Base64.getEncoder().encodeToString(publickey.getEncoded());
		String pksign = generateSign(pkstr);
		
		dht.store(dhtclb, Key.create(name), new Value(new Date(), pkstr, pksign));
	}
	
	/**
	 * Generate message signature
	 * @param mess message to sign
	 * @return message signature or "" if the operation fail
	 */
	public String generateSign(String mess){
		String sign = "";
		
		try{
			Signature sig = Signature.getInstance(Crypto.SIGN_ALGO, Crypto.Provider);
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
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(Crypto.ACC_ALGO, Crypto.Provider);
		kpg.initialize(size);
		
		KeyPair kp = kpg.generateKeyPair();
		
		return new Account(name, kp.getPublic(), kp.getPrivate());
	}
}
