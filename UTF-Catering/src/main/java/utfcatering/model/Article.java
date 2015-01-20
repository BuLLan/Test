/** 
 * Article with set/get amount and attributes ID, name, price and category. attributes are not changeable
 * 
 * 
 *
 * 
 * @author    Anne-Liesse Fiutak
 * @author    Igor Misirev
 * @version   0.4
 * @since     28-12-2014
 * 
 */

package utfcatering.model;

import javax.persistence.Entity;

import org.joda.money.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Units;

@Entity
public class Article extends Product{
	
	public static enum Category{
		SPEISEN, GETRAENKE, LEIHWAREN, PACKAGE;
	}

	private int amount;
	private double mwSt;
	private Category category; 
	
	@Deprecated
	protected Article(){}
	
	/**
	 * Constructor
	 * @param name the name of the article
	 * @param price the price for one article
	 * @param mwSt the VAT of the article 
	 */ 
	public Article( String name, Category category, Money price, double mwSt){
		super(name, price, Units.METRIC);
		this.category = category;
		this.addCategory(category.toString());
		this.mwSt = mwSt;
	}

	/**
	 * @return category of the article
	 */
	
	public Category getCategory() {
		return category;
	}

	/**
	 * @return amount of the article
	 */
	
	public int getAmount(){
		return amount;
	}
	
	/**
	 * sets the amount of article
	 */
	
	public void setAmount(int amount){
		this.amount=amount;
	}
	
	/**
	 * converts a article to product
	 * @param pr is the converted Product
	 * @return pr
	 */

	public Product createProductFromArticle(){
		Product pr = new Product(getName(), getPrice(), Units.METRIC);
		pr.addCategory(getCategories().iterator().next());
		return pr;
	}
	
	/**
	 * @return VAT of the article
	 */
	
	public double getMwSt() {
		return mwSt;
	}
	
	/**
	 * sets VAT of the article
	 */

	public void setMwSt(double mwSt) {
		//TODO price update
		this.mwSt = mwSt;
	}
	
	/**
	 * calculates the total price
	 * @param res is the total Price
	 * @return res and the Unit
	 */

	@Deprecated
	public Money calculateMwSt(){
		double res = getPrice().getAmount().doubleValue()*this.mwSt/(100 + this.mwSt);
		res *= 100;
		res = Math.round(res);
		res /= 100;
		return Money.of(getPrice().getCurrencyUnit(), res);
	}
}
