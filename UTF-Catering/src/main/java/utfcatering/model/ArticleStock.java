/** 
 * Stock with articles which can be added, get and removed. Also total amount. 
 * 
 * TODO not yet finished!
 * 
 * @author    Anne-Liesse Fiutak
 * @author Igor Misirev
 * @version   0.1
 * @since     06-12-2014
 * 
 */

package utfcatering.model;

import java.util.HashMap;
import java.util.Map;



public class ArticleStock {
	
	private Map<Article,Integer> stock;
	
	/**
	 * Constructor
	 * @param stock represents the Stock
	 */
	
	public ArticleStock(){
		stock = new HashMap<Article,Integer>();
		
	}

	/**
	 * Adds articles to the article Stock
	 * @param product that is added
	 * @param amount the amount of articles
	 */
	
	public void addArticles(Article product, int amount){
		stock.put(product,amount);
	}
	
	/**
	 * 
	 * @param product the article
	 * @return amount of the articles in the stock
	 */
	
	public int getAmount(Article product){
		return stock.get(product);
	}
	
	/**
	 * @return The map of articles and their amount in the article stock
	 */
	
	public Map<Article,Integer> getArticles(){
		return stock;
	}
	
	/**
	 * removes an article from the map
	 * @param product the article
	 */
	
	public void removeArticles(Article product){
	/**
	 * Entfernt ein ELement des Produktes aus dem Lager
	 * @param product Produkt, von dem ein Element abgezogen werden soll
	 */
		stock.replace(product,getAmount(product)-1);
	}

	/**
	 * removes the amount of articles from the article stock
	 * @param product the article
	 * @param amount is the amount of articles to remove
	 */
	public void removeArticles(Article product, int amount){

		stock.replace(product,getAmount(product)-amount);	
	}
	
	
}
