package decc.accounts;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import decc.dht.ui.IDht;

/**
 * Manage all DECC accounts and account contacts
 * @author nyradr
 */
public class AccountsManager {
	
	private Account user;
	private Map<String, Contact> contacts;
	private IDht dht;
	
	/**
	 * Create account manager from account
	 * @param iacc user account
	 */
	public AccountsManager(Account iacc){
		user = iacc;
		this.dht = dht;
		
		contacts = new TreeMap<>();
	}
	
	/**
	 * Get the actual user
	 * @return
	 */
	public Account getUser(){
		return user;
	}
	
	/**
	 * Add contact to the contact list
	 * @param c contact
	 */
	public void addContact(Contact c){
		contacts.put(c.getName(), c);
	}
	
	/**
	 * Remove contact from the contact list
	 * @param name
	 */
	public void remContact(String name){
		contacts.remove(name);
	}
	
	/**
	 * Get all contacts name
	 * @return
	 */
	public Set<String> getContacts(){
		return contacts.keySet();
	}
	
	/**
	 * Get a specific contact (from the local contact list)
	 * @param name
	 * @return
	 */
	public Contact getContact(String name){
		return contacts.get(name);
	}
	
	/**
	 * Search for all contact matching name (from the local contact list)
	 * @param name name to search
	 * @return
	 */
	public Set<String> searchContact(String name){
		return contacts.keySet().stream()
				.filter(x -> x.toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toSet());
	}
}
