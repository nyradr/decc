package decc.ui;

import decc.accounts.ui.IContact;

/**
 * Interface for getting informations on a communication
 * @author nyradr
 */
public interface ICom {
	
	/**
	 * Get the communication comid
	 * @return
	 */
	public String getComid();
	
	/**
	 * Get the contact of the communication
	 * @return
	 */
	public IContact getContact();
	
	/**
	 * Send message through this communication
	 * @param mess 
	 */
	public void send(String mess);
}
