package utfcatering;

import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.Assert.*;

import java.util.Map;

import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import utfcatering.model.Article;
import utfcatering.model.Article.Category;
import utfcatering.model.ArticleStock;


public class LagerTest {

	@Autowired ArticleStock stock;
	
	@Before
	public void initSystem() {
		stock = new ArticleStock();

	}
	  
	@Test 
	//Adding a product and Test methods
	public void productTest(){
		//adding article to stock
		int amountToAdd = 12;
		Article prod = new Article("TestName",Category.SPEISEN,Money.of(EUR, 30.00), 19.0);
		stock.addArticles(prod, amountToAdd);
		
		Map<Article,Integer> stockmap = stock.getArticles();
		int amount = stockmap.get(prod);
		//checking amount
		assertEquals(amount,amountToAdd);
		//checking getAmount method
		assertEquals(stock.getAmount(prod),amountToAdd);
		//checking remove x method
		int amountToRemove=2;
		stock.removeArticles(prod, amountToRemove);
		assertEquals(stock.getAmount(prod),amountToAdd-amountToRemove);
		//checking remove 1
		stock.removeArticles(prod);
		
		stockmap = stock.getArticles();
		assertTrue(9 == stockmap.get(prod));
	}
	
}