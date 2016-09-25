package decc;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import decc.accounts.Account;
import decc.options.Crypto;
import decc.ui.IDecc;
import decc.ui.IDeccUser;

/**
 * Build functions for DECC
 * @author nyradr
 */
public class DeccBuilder {
	
	/**
	 * Build DECC with default values<br>
	 * By default DECC is on the port 4242 and has no name
	 * @param clb user callback
	 * @return DECC interface
	 * @throws IOException
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static IDecc getDefault(IDeccUser clb) throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		return new DeccInstance(4242, Account.create("", Crypto.DEF_RSA_LEN), clb);
	}
	
	/**
	 * Build DECC with user set values
	 * @param port port to use
	 * @param name DECC name
	 * @param clb user callback
	 * @return DECC interface
	 * @throws IOException
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static IDecc getDecc(int port, Account acc, IDeccUser clb) throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		return new DeccInstance(port, acc, clb);
	}
}
