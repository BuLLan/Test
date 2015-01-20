/** 
 * LoanArticle default:AVAILABLE
 *
 * @author    Igor Misirev
 * @version   0.3
 * @since     12-12-2014
 * 
 */
package utfcatering.model;

import javax.persistence.Entity;

import org.joda.money.Money;

@Entity
public class LoanArticle extends Article {
	
	
	public static enum Availability{
		AVAILABLE, UNAVAILABLE;
	}
	
	private Availability availability; 
	
	/**
	 * Constructor
	 * @param name the name of the article
	 * @param price the price for one article
	 * @param mwSt the VAT of the article
	 * @param availability the availability of the article
	 */ 
	
	public LoanArticle(String name, Category category, Money price, double mwSt) {
		super(name, category, price, mwSt);
		this.availability = Availability.AVAILABLE;
	}

	/**
	 * @return availability of the article
	 */

	public Availability getAvailability() {
		return availability;
	}

	/**
	 * sets availability of the article
	 * @param availability of the article
	 */
	
	public void setAvailability(Availability availability) {
		this.availability = availability;
	}
	
}
