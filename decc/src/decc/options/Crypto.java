package decc.options;

/**
 * DECC cryptographic constants
 * @author nyradr
 */
public class Crypto {
	
	public static final String Provider = "BC";
	
	public static final String ACC_ALGO = "RSA";
	public static final int DEF_RSA_LEN = 1024;
	
	public static final String DH = "DH";
	public static final int DH_SIZE = 1024;
	public static final String CONV_ALGO = "DES";
	public static final String CONV_ENC = "DES/ECB/PKCS5Padding";
	
	public static final String SIGN_ALGO = "SHA1withRSA";
}
