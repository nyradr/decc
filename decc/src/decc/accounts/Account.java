package decc.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account {
	
	private String name;
	private PGPPublicKeyRing publicKey;
	private PGPSecretKeyRing privateKey;
	
	/**
	 * Create account with private key (can decrypt)
	 * @param name
	 * @param pubk
	 * @param prvk
	 */
	public Account(String name, PGPPublicKeyRing pubk, PGPSecretKeyRing prvk){
		
	}
	
	/**
	 * Create account with only a public key
	 * @param name
	 * @param pubk
	 */
	public Account(String name, PGPPublicKeyRing pubk){
		
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
	public PGPPublicKeyRing getPublic(){
		return publicKey;
	}
	
	/**
	 * Get the account private PGP key
	 * @return
	 */
	public PGPSecretKeyRing getPrivate(){
		return privateKey;
	}
	
	/**
	 * Encrypt data with the account public key
	 * @param data data to encrypt
	 * @return
	 */
	public String encrypt(String data){
		return "";
	}
	
	/**
	 * Decrypt data with the account private key
	 * @param data data to decrypt
	 * @return decrypted data
	 */
	public String decrypt(String data){
		return "";
	}
	
	/**
	 * Sign data with this account
	 * @param data data to sign
	 * @return signature
	 */
	public String sign(String data){
		return "";
	}
	
	/**
	 * Create new account and generate PGP key pair
	 * @param name user name
	 * @param pass PGP password
	 * @param size key size
	 * @return
	 * @throws PGPException 
	 */
	public static Account create(String name, String pass, int size) throws PGPException{
		// rsa key generation
		RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
		kpg.init(new RSAKeyGenerationParameters(
				BigInteger.valueOf(0x10001), new SecureRandom(), size, 12
				));
		
		// key creation
		PGPKeyPair key = new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, kpg.generateKeyPair(), new Date());
		
		// key signature creation
		PGPSignatureSubpacketGenerator sign = new PGPSignatureSubpacketGenerator();
		sign.setKeyFlags(false, KeyFlags.SHARED);
		sign.setPreferredHashAlgorithms(false, new int[]{
				HashAlgorithmTags.SHA512,
				HashAlgorithmTags.SHA384,
				HashAlgorithmTags.SHA256
		});
		sign.setPreferredSymmetricAlgorithms(false, new int[]{
				SymmetricKeyAlgorithmTags.AES_256,
				SymmetricKeyAlgorithmTags.TWOFISH,
				SymmetricKeyAlgorithmTags.BLOWFISH
		});
		sign.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);
		
		// hashs
		PGPDigestCalculator sha1c = new BcPGPDigestCalculatorProvider()
				.get(HashAlgorithmTags.SHA1);
		PGPDigestCalculator sha256c = new BcPGPDigestCalculatorProvider()
				.get(HashAlgorithmTags.SHA256);
		
		// private key encryption
		PBESecretKeyEncryptor ske = new BcPBESecretKeyEncryptorBuilder(
				PGPEncryptedData.AES_256, sha256c, 0x60)
				.build(pass.toCharArray());
		
		// create key ring
		PGPKeyRingGenerator krg = new PGPKeyRingGenerator(
				PGPSignature.POSITIVE_CERTIFICATION, key,
				name, sha1c, sign.generate(), null,
				new BcPGPContentSignerBuilder(key.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
				ske);
				
		// create account
		return new Account(name, krg.generatePublicKeyRing(), krg.generateSecretKeyRing());
	}
}
