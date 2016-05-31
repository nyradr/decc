package decc.accounts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Cipher;

import org.bouncycastle.openpgp.PGPPublicKeyRing;

import decc.options.AccountsOptions;

/**
 * Manage all DECC accounts and account contacts
 * @author nyradr
 */
public class AccountsManager {
	
	private Account user;
	private Map<String, Contact> contacts;
	
	/**
	 * Create account manager from account
	 * @param iacc user account
	 */
	public AccountsManager(Account iacc){
		user = iacc;
		
		contacts = new TreeMap<>();
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
	 * Get a specific contact
	 * @param name
	 * @return
	 */
	public Contact getContact(String name){
		return contacts.get(name);
	}
	
	/**
	 * Search for all contact matching name
	 * @param name name to search
	 * @return
	 */
	public Set<String> searchContact(String name){
		return contacts.keySet().stream()
				.filter(x -> x.toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toSet());
	}
}
