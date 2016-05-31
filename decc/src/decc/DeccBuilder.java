package decc;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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
		return new DeccInstance(4242, "", clb);
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
	public static IDecc getDecc(int port, String name, IDeccUser clb) throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		return new DeccInstance(port, name, clb);
	}
}
