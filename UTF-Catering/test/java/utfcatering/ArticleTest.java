package utfcatering;

import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Units;
import org.springframework.beans.factory.annotation.Autowired;

import utfcatering.model.Article;
import utfcatering.model.ArticleStock;
import utfcatering.model.LoanArticle;
import utfcatering.model.PackageRepository;
import utfcatering.model.Article.Category;
import utfcatering.model.LoanArticle.Availability;
import utfcatering.model.Package;
import utfcatering.model.EntryInThePackage;

public class ArticleTest {

	@Autowired PackageRepository packageRepository;
	@Autowired ArticleStock stock;
	Article art1,art2, art3;
	Product pr;
	
	@Before
	public void initSystem() {
		art1 = new Article("name1", Category.SPEISEN, Money.of(EUR, 2.00),19.0);
		art2 = new Article("name2", Category.GETRAENKE, Money.of(EUR, 3.50), 19.0);
		art3 = new Article("name3", Category.LEIHWAREN, Money.of(EUR, 12.00), 19.0);
		
		stock = new ArticleStock();
		
		pr = new Article("testName", Category.SPEISEN, Money.of(EUR, 5.00), 19.0);
	}
	  
	@Test
	public void ArticleGetCategoryTest(){
		
		//check category
		Category cat1 = Category.GETRAENKE;
		Category cat2 = Category.LEIHWAREN ;
		Category cat3 = Category.SPEISEN;
		
		Category test1;
		Category test2;
		Category test3;
		test1 = art1.getCategory();
		test2 = art2.getCategory();
		test3 = art3.getCategory();
		
		assertEquals(test1, cat3);
		assertEquals(test2, cat1);
		assertEquals(test3, cat2);
	}
	
	@Test
	public void ArticleGetAmountTest(){
		
		int amountToAdd = 10;
		stock.addArticles(art1, amountToAdd);
		
		boolean amount = false;

		if(stock.getAmount(art1)==10.00){
			amount = true;
		}
		
		assertTrue(amount);		
	}
	
	@Test
	public void createProductFromArticleTest(){
		
		String productName = pr.getName();
		Money productPrice = pr.getPrice();
		
		assertEquals(productName,"testName");
		assertEquals(productPrice, Money.of(EUR, 5.00));
		
	}
	
	@Test 
	public void mwstTest(){
		
		double artMwst = art1.getMwSt();
		boolean mwst = false;
		
		if(artMwst == 19.0){
			mwst = true;
		}
		
		assertTrue(mwst);
	}
	
	@Test
	public void calculateMwStTest(){
		
		double mwStArticle = art1.getMwSt();
		
		double preis1Test = art1.getPrice().getAmount().doubleValue();	
		double preis2Test = preis1Test * (mwStArticle/(100 + mwStArticle));
		
		preis2Test *= 100;
		preis2Test = Math.round(preis2Test);
		preis2Test /= 100;

		Money mwstArticle = Money.of(art1.getPrice().getCurrencyUnit(), preis2Test);
		
		assertEquals(mwstArticle, Money.of(EUR, 0.32));
		
	}
	
}