package utfcatering.model.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;


public class RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.firstname.NotEmpty}") 
	@Pattern(regexp="[a-zA-Z]+", message ="{RegistrationForm.firstname.Pattern}")
	private String firstname;
	
	@NotEmpty(message = "{RegistrationForm.lastname.NotEmpty}")
	@Pattern(regexp="[a-zA-Z]+", message ="{RegistrationForm.lastname.Pattern}")
	private String lastname;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}")
	private String password;

	@NotEmpty(message = "{RegistrationForm.address.NotEmpty}")
	private String address;
	
	@NotEmpty(message = "{RegistrationForm.username.NotEmpty}")
	private String username;
	
	@NotEmpty(message = "{RegistrationForm.birthdate.NotEmpty}")
	@Pattern(regexp="^(0?[1-9]|[12][0-9]|3[01])(\\.)(0?[1-9]|1[012])(\\.)[0-9]{4}$", message ="{RegistrationForm.birthdate.Pattern}")
	private String birthdate;
	
	@NotEmpty(message = "{RegistrationForm.role.NotEmpty}")
	private String role;
	
	@NotEmpty(message = "{RegistrationForm.salary.NotEmpty}")
	@Pattern(regexp="[0-9]+(\\.)[0-9]+", message ="{RegistrationForm.salary.Pattern}")
	private String salary;
	
	
	
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
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
}

