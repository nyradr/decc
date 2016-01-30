package decc.secu;

/**
 * Methods to transform byte array to string and string to bytes
 * @author nyradr
 */
public class Encode {
	
	/**
	 * Byte array to string
	 * @param data
	 * @return
	 */
	public static String bts(byte[] data){
		String s = "";
		
		for(byte b : data)
			s += (char) b;
		
		return s;
	}
	
	/**
	 * String to byte array
	 * @param str
	 * @return
	 */
	public static byte[] stb(String str){
		byte[] b = new byte[str.length()];
		
		for(int i = 0; i < b.length; i++)
			b[i] = (byte) str.charAt(i);
		
		return b;
	}
}
