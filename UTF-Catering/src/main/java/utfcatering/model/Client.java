package utfcatering.model;

import javax.persistence.Entity;

import org.salespointframework.useraccount.UserAccount;

/**
 * Used as subclass of {@link utfcatering.model.User} to differentiate between {@link utfcatering.model.Employee}s and Clients
 * @author Dennis
 *
 */
@Entity
public class Client extends User {
	
	@Deprecated
	protected Client()
	{
	}
	
	/**
	 * Constructor 
	 * uses the {@link utfcatering.model.User}
	 * @param userAccount is the userAccount of a client
	 * @param username is the username of a client
	 * @param address is the address of a client
	 */
	public Client(UserAccount userAccount, String username, String address)
	{
		super(userAccount, username, address);
	}

}