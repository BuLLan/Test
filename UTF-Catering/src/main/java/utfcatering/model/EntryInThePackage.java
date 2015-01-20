/**
 * 
 * @author    Igor Misirev
 * @version   0.2
 * @since     28-12-2014
 * 
 */

package utfcatering.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.salespointframework.catalog.Product;

@Entity
public class EntryInThePackage {

	@Id	@GeneratedValue	long id;
	
	@ManyToOne(cascade = {CascadeType.ALL})
	private Product article;
	
	private int countOfArticle;
	
	
	@Deprecated
	protected EntryInThePackage(){}
	
	/**
	 * Constructor
	 * @param article is Product that is in a Package
	 * @param count is the amount of Product that is in the Package
	 */
	
	public EntryInThePackage(Product article, int count) {
		this.article = article;
		this.countOfArticle = count;
	}
	
	/**
	 * returns the amount of a Product that is the Package
	 * @return countOfArticle
	 */

	public int getCountOfArticle() {
		return countOfArticle;
	}
	
	/**
	 * sets the amount of a Product that is the Package
	 */

	public void setCountOfArticle(int count) {
		this.countOfArticle = count;
	}
	
	/**
	 * returns the ID of a Product that is the Package
	 * @return id
	 */

	public long getId() {
		return id;
	}

	/**
	 * returns the Product that is the Package
	 * @return article
	 */
	
	public Product getArticle() {
		return article;
	}

	/**
	 * sets the Product that is the Package
	 */
	
	public void setArticle(Product article) {
		this.article = article;
	}

	
}
