package decc.accounts;

import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.util.encoders.Base64Encoder;

import decc.accounts.ui.ContactStatus;
import decc.accounts.ui.IContact;
import decc.dht.Key;
import decc.dht.Value;
import decc.dht.ui.IDht;
import decc.dht.ui.IDhtClb;
import decc.options.Crypto;

/**
 * Represent a DECC contact
 * @author nyradr
 */
public class Contact implements IContact{
	
	/**
	 * DHT lookup request handler
	 * @author nyradr
	 *
	 */
	private class PkVerifClb implements IDhtClb{

		@Override // NOT USED
		public void onStore(Key k, char flag) {}

		@Override
		public void onLookup(Key k, Value v) {
			// verification of the public key with the DHT public key
			// TODO : more advanced verification
			
			Base64.Encoder b64 = Base64.getEncoder();
			
			String pkct = b64.encodeToString(publickey.getEncoded());
			
			if(pkct.equals(v.getVal()))
				status = ContactStatus.VERIFIED;
			else
				status = ContactStatus.INVALID;
		}
		
	}
	
	protected String name;
	protected PublicKey publickey;
	protected ContactStatus status;
	private PkVerifClb dhtclb;
	
	/**
	 * Create contact
	 * @param name contact name
	 * @param pk public key
	 */
	public Contact(String name, PublicKey pk){
		this.name = name;
		publickey = pk;
		status = ContactStatus.UNVERIFIED;
		dhtclb = new PkVerifClb();
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public ContactStatus getStatus(){
		return status;
	}
	
	/**
	 * Get the public key
	 * @return
	 */
	public PublicKey getPublic(){
		return publickey;
	}
	
	
	/**
	 * Verify the account public key
	 * @param dht DHT instance
	 */
	public void verifyPublicKey(IDht dht){
		status = ContactStatus.VERIFICATION;
		dht.lookup(dhtclb, Key.create(name));
	}
	
	/**
	 * Verify a string signed for this account
	 * @param mess message to verify
	 * @param sign message signature
	 * @return true if the message is verified
	 */
	public boolean verifySign(String mess, String sign){
		boolean isverif = false;
		try{
			Signature sig = Signature.getInstance(Crypto.SIGN_ALGO, Crypto.Provider);
			sig.initVerify(publickey);
			
			sig.update(mess.getBytes());
			
			isverif = sig.verify(Base64.getDecoder().decode(sign.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isverif;
	}
}
