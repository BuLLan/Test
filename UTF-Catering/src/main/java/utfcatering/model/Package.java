/** 		
 *		
 * @author    Igor Misirev & Kevin Gölzner		
 * @version   0.4		
 * @since     28-12-2014		
 * 		
 */		
package utfcatering.model;		
		
import static org.joda.money.CurrencyUnit.EUR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.joda.money.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Units;

import utfcatering.model.Article.Category;


@Entity
public class Package extends Product{			
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch =FetchType.EAGER)
	private List<EntryInThePackage> articles = new ArrayList<EntryInThePackage>();
	
	private Category category;	
	
	@Deprecated
	protected Package(){}	
	
	/**
	 * Constructor
	 * @param name the name of the package
	 * @param Inhalt the content of the package
	 */ 
	
	public Package(String name, Map<Article, Integer> Inhalt) {	
		super(name, Money.of(EUR, 0.0), Units.METRIC);

		Money price = Money.of(EUR, 0.0);
		for(Article a : Inhalt.keySet()){
			this.articles.add(new EntryInThePackage(a.createProductFromArticle(), Inhalt.get(a)));
			price = price.plus(a.getPrice().multipliedBy(Inhalt.get(a)));
		}
		this.setPrice(price);
		this.category = Category.PACKAGE;
		this.addCategory(category.toString());
	}
	
	/**
	 * returns a list of articles that are in the package
	 * @return articles
	 */ 

	public List<EntryInThePackage> getArticles(){
		return articles;
	}
	
	/**
	 * adds a article to a package
	 * @param article is the article to add
	 * @param amount of the article to add
	 */ 
	
	public void addArticle(Product article, Integer amount){
		articles.add(new EntryInThePackage(article, amount));
		this.setPrice(getPrice().plus(article.getPrice().multipliedBy(amount)));
	}

	/**
	 * returns the category of a package
	 * @return category
	 */ 
	
	public Category getCategory() {
		return category;
	}
	
	/**
	 * removes a article from a package
	 * @param removed is the article to remove
	 * @param amount of the article to add
	 */ 
	
	public void removeArticle(Long id){
		EntryInThePackage removed = null;
		for(EntryInThePackage item : this.articles){
			if(item.getId() == id){
				removed = item;
				this.articles.remove(item);
				break;
			}
		}

		if(removed != null){
			this.setPrice(getPrice().minus(removed.getArticle().getPrice().
					multipliedBy(removed.getCountOfArticle())));
		}
	}
	
	/**
	 * calculates the total price (inclusive VAT) for the package
	 * @param resss is the total price
	 */ 
	@Deprecated
	public Money calculateMwSt(){
		double resss = 0.0;
		for(EntryInThePackage entry : this.articles){
			resss += entry.getArticle().getPrice().getAmount().doubleValue() * entry.getCountOfArticle();
		}
		resss *= 100;
		resss = Math.round(resss);
		resss /= 100;
		
		return getPrice().minus(Money.of(getPrice().getCurrencyUnit(), resss));
	}
}		
