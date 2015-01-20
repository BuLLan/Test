/**
 * 
 * @author    Dennis, Igor Misirev
 * @version   0.2
 * @since     27-12-2014
 * 
 */

package utfcatering.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.salespointframework.useraccount.UserAccount;

@Entity
/**
 * The User is the superclass of all users in the system. It mainly extends the userAccount with an address and a username
 * @author Dennis
 *
 */
public class User { 
	
	//@Id
	//@GeneratedValue
	//private Long id;
	
	private String address;
	
	@Id
	public String username;
	
	@OneToOne
	private UserAccount userAccount;
	
	@Deprecated
	protected User()
	{		
	}
	
	/**
	 * Creates a new user
	 * @param userAccount the UserAccount the user is going to have
	 * @param username the new user/loginname
	 * @param address the adress of the user
	 */
	public User(UserAccount userAccount, String username, String address)
	{
		this.userAccount = userAccount;
		this.username=username;
		this.address=address;
	}
	
	/**
	 * Gets the user/loginname
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Changes the user/loginname
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Gets the adress
	 * @return the adress
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * Changes the address
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * Gets the UserAccount of this user
	 * @return the UserAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}
	
}
