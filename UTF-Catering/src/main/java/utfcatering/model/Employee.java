package utfcatering.model;

import javax.persistence.Entity;

import org.salespointframework.useraccount.UserAccount;
//import org.salespointframework.core.money.Money; (vielleicht besser als double)

@Entity
/**
 * The Employee class is an subclass of User that was made to manage all employees of the company. It adds the employees birthday, their salary
 * @author Dennis
 *
 */
public class Employee extends User{
	
	
	private String birthdate; 
	private Double salary;
	
	
	@Deprecated
	protected Employee() {
	}
	
	/**
	 * Creates a new employee.
	 * @param userAccount the UserAccount of the employee
	 * @param username the username.
	 * @param address the adress, saved as a String
	 * @param birthdate the day of birth, saved as String
	 * @param salary the monthly salary as double
	 */
	public Employee(UserAccount userAccount, String username, String address, String birthdate, Double salary)
	{
		super(userAccount, username, address);
		this.birthdate = birthdate;
		this.salary = salary;
	}

	/**
	 * Returns the day of birth
	 * @return birthday as String
	 */
	public String getBirthdate() {
		return birthdate;
	}

	/**
	 * Sets the birthday
	 * @param birthdate the birthdate as a String
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * Returns the monthly salary
	 * @return the salary as Double
	 */
	public Double getSalary() {
		return salary;
	}

	/**
	 * Sets the monthly salary
	 * @param salary the Double salary
	 */
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
}