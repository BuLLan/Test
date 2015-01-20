package utfcatering;

import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.Assert.*;

import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Units;
import org.springframework.beans.factory.annotation.Autowired;

import utfcatering.model.Article;
import utfcatering.model.Article.Category;
import utfcatering.model.EntryInThePackage;

public class EntryInThePackageTest {

	EntryInThePackage pack, pack2;
	Article article;
	
	
	@Before
	public void initSystem() {
		
		int count = 10;

		article = new Article("name1", Category.SPEISEN, Money.of(EUR, 2.00),19.0);
		pack = new EntryInThePackage(article, count);
		pack2 = new EntryInThePackage(article, count);
		
	}
	  
	@Test
	public void getCountOfArticleTest(){
		
		assertEquals(pack.getCountOfArticle(), 10);
	
	}
	
	@Test
	public void getIdTest(){
		long a = 3939;
		System.out.println(pack.getId());
		System.out.println(pack2.getId());
		
	}
	
	@Test
	public void getArticleTest(){
		
		assertEquals(pack.getArticle(), article);
		
	}
	

	

	
	
	
	
}
