package decc.accounts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

/**
 * Represent a DECC account
 * @author nyradr
 */
public class Account {
	
	private String name;
	private String pass;
	private PGPPublicKeyRing publicKey;
	private PGPSecretKeyRing privateKeyRing;
	private PGPPrivateKey privateKey;
	
	/**
	 * Create account with private key (can decrypt)
	 * @param name
	 * @param pubk
	 * @param prvk
	 * @throws PGPException 
	 */
	public Account(String name, String pass, PGPPublicKeyRing pubk, PGPSecretKeyRing prvk) throws PGPException{
		this.name = name;
		this.pass = pass;
		
		publicKey = pubk;
		privateKeyRing = prvk;
		
		BcPBESecretKeyDecryptorBuilder skdb = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider());
		PBESecretKeyDecryptor skd = skdb.build(pass.toCharArray());
		privateKey = privateKeyRing.getSecretKey().extractPrivateKey(skd);
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
		return privateKeyRing;
	}
	
	/**
	 * Encrypt data with the account public key
	 * @param data data to encrypt
	 * @return
	 * @throws PGPException 
	 * @throws IOException 
	 */
	public String encrypt(String data) throws IOException, PGPException{
		final int buff_size = 64;
		InputStream is = new ByteArrayInputStream(data.getBytes());
		OutputStream os = new ByteArrayOutputStream();
		
		// encryption
		BcPGPDataEncryptorBuilder deb = new BcPGPDataEncryptorBuilder(PGPEncryptedData.AES_256);
		deb.setSecureRandom(new SecureRandom());
		deb.setWithIntegrityPacket(true);
		
		PGPEncryptedDataGenerator edg = new PGPEncryptedDataGenerator(deb);
		edg.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey.getPublicKey()));
		
		OutputStream eos = edg.open(os, new byte[buff_size]);
		
		PGPCompressedDataGenerator cdg = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
		OutputStream cos = cdg.open(eos);
		
		// signature
		PGPContentSignerBuilder csb = new BcPGPContentSignerBuilder(
				privateKeyRing.getSecretKey().getPublicKey().getAlgorithm(),
				HashAlgorithmTags.SHA256);
		PGPSignatureGenerator sg = new PGPSignatureGenerator(csb);
		sg.init(PGPSignature.BINARY_DOCUMENT, privateKey);
		
		Iterator<String> it = privateKeyRing.getSecretKey().getUserIDs();
		if(it.hasNext()){
			PGPSignatureSubpacketGenerator ssg = new PGPSignatureSubpacketGenerator();
			ssg.setSignerUserID(false, it.next());
			sg.setHashedSubpackets(ssg.generate());
		}
		
		sg.generateOnePassVersion(false).encode(cos);
		
		PGPLiteralDataGenerator ldg = new PGPLiteralDataGenerator();
		OutputStream los = ldg.open(cos, PGPLiteralData.BINARY, "data", new Date(), new byte[buff_size]);
		
		// encrypt and sign
		byte[] buff = new byte[buff_size];
		int len = 0;
		
		while((len = is.read(buff)) > 0){
			los.write(buff);
			sg.update(buff, 0, len);
		}
		is.close();
		
		sg.generate().encode(cos);
		cdg.close();
		edg.close();
		
		String ret = os.toString();
		os.close();
		
		return ret;
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
		return new Account(name, pass, krg.generatePublicKeyRing(), krg.generateSecretKeyRing());
	}
}
