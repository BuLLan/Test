/**
 * @author    Igor Misirev
 * @version   0.4
 * @since     28-12-2014
 * 
 */
package utfcatering.model.validation;
import org.hibernate.validator.constraints.NotEmpty;
import org.salespointframework.catalog.ProductIdentifier;

public class ProductInsertForm { 
	//@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private ProductIdentifier id;
	
	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private String amount;
	


	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public ProductIdentifier getId() {
		return id;
	}

	public void setId(ProductIdentifier id) {
		this.id = id;
	}

	

}
