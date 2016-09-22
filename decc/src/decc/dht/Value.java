package decc.dht;

import java.util.Date;

/**
 * Represent a value stored in the DHT
 * @author nyradr
 */
public class Value{
	protected Date post;
	protected String value;
	protected String sign;
	
	public Value(Date post, String val, String s){
		this.post = post;
		value = val;
		sign = s;
	}
	
	/**
	 * Get the post date
	 * @return
	 */
	public Date getDate(){
		return post;
	}
	
	/**
	 * Get the value
	 * @return
	 */
	public String getVal(){
		return value;
	}
	
	/**
	 * Get the value signature
	 * @return
	 */
	public String getSign(){
		return sign;
	}
}
