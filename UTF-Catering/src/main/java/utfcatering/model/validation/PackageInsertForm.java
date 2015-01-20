package utfcatering.model.validation;

import java.util.LinkedList;

import org.hibernate.validator.constraints.NotEmpty;
/**
 * @author Kevin
 *
 */
public class PackageInsertForm {
	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private String name;
	
	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private String price;
	
	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private LinkedList<String> Inhalt = new LinkedList<String>();
	
	public void setInhalt(LinkedList<String> Inhalt){
		this.Inhalt = Inhalt;
	}
	
	public LinkedList<String> getInhalt(LinkedList<String> Inhalt){
		return Inhalt;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
