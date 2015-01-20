package utfcatering.model.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistrationFormExternal {
	@NotEmpty(message = "{RegistrationFormExternal.firstname.NotEmpty}") 
	@Pattern(regexp="[a-zA-Z]+", message ="{RegistrationFormExternal.firstname.Pattern}")
	private String firstname;
	
	@NotEmpty(message = "{RegistrationFormExternal.lastname.NotEmpty}")
	@Pattern(regexp="[a-zA-Z]+", message ="{RegistrationFormExternal.lastname.Pattern}")
	private String lastname;

	@NotEmpty(message = "{RegistrationFormExternal.password.NotEmpty}")
	private String password;

	@NotEmpty(message = "{RegistrationFormExternal.address.NotEmpty}")
	private String address;
	
	@NotEmpty(message = "{RegistrationFormExternal.username.NotEmpty}")
	private String username;
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
