package decc.accounts.ui;

/**
 * User interface to look on a contact
 * @author nyradr
 */
public interface IContact {
	
	/**
	 * Get the contact name
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the contact status
	 * @return
	 */
	public ContactStatus getStatus();
}
