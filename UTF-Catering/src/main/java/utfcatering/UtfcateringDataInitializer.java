/** 
 *

 * @author    Dennis Körte, Igor Misirev
 * @version   0.5
 * @since     28-12-2014
 * 
 */

package utfcatering;

import static org.joda.money.CurrencyUnit.EUR;

import java.util.HashMap;

import org.joda.money.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.quantity.RoundingStrategy;
import org.salespointframework.quantity.Units;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import utfcatering.model.Article;
import utfcatering.model.Article.Category;
import utfcatering.model.ArticleCatalog;
import utfcatering.model.Employee;
import utfcatering.model.Package;
import utfcatering.model.PackageRepository;
import utfcatering.model.User;
import utfcatering.model.UserRepository;


/**
 * initializes userAccountManager, userRepository, packageRepository, arcticleCatalog and inventory
 * @param userAccountManager holds all userAccounts
 * @param userRepository is the Repository of all {@link utfcatering.model.User}s
 * @param packageRepository is the Repository of all {@link utfcatering.model.Package}s
 * @param articleCatalog holds all {@link utfcatering.model.Article}s
 * @param articleCatalog holds all {@link org.salespointframework.inventory.InventoryItem}s
 */

@Component
public class UtfcateringDataInitializer implements DataInitializer {
	private final UserAccountManager userAccountManager; 
	private final UserRepository userRepository;
	private final PackageRepository packageRepository;
	private final ArticleCatalog articleCatalog;
	private final Inventory<InventoryItem> inventory;
	
	@Autowired
	public UtfcateringDataInitializer(UserRepository userRepository, 
			UserAccountManager userAccountManager,
			ArticleCatalog articleCatalog,
			Inventory<InventoryItem> inventory,
			PackageRepository packageRepository) {

		Assert.notNull(userRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(articleCatalog, "ArticleCatalog must not be null!");
		Assert.notNull(packageRepository, "PackageRepository must not be null!");
		
		this.userRepository = userRepository;
		this.userAccountManager = userAccountManager;
		this.inventory = inventory;
		this.articleCatalog = articleCatalog;
		this.packageRepository = packageRepository;
	}
	
	@Override
	public void initialize() {
		initializeUsers(userAccountManager,userRepository);
		createArticle(articleCatalog, inventory);
	}
	
	/**
	 * initializes some {@link utfcatering.model.Article}s to fill up the stock at the first launch.
	 */
	
	private void createArticle(ArticleCatalog articleCatalog, Inventory<InventoryItem> inventory){
		//create Food
		Article a1 = new Article("Obstplatte", Category.SPEISEN, Money.of(EUR, 2.00), 19.0);
		Article a2 = new Article("Brötchen", Category.SPEISEN, Money.of(EUR, 0.20), 19.0);
		Article a3 = new Article("halbe belegte Brötchen", Category.SPEISEN, Money.of(EUR, 1.20), 19.0);
		Article a4 = new Article("Bagel", Category.SPEISEN, Money.of(EUR, 2.50), 19.0);
		Article a5 = new Article("Donut", Category.SPEISEN, Money.of(EUR, 1.00), 19.0);
		Article a6 = new Article("Kuchen", Category.SPEISEN, Money.of(EUR, 0.94), 19.0);
		Article a7 = new Article("Aufstrichpaket(Butter, Marmelade, Wurst etc.)", Category.SPEISEN, Money.of(EUR, 1.00), 19.0);
		//create Drinks		
		Article a8 = new Article("Tasse Kaffee", Category.GETRAENKE, Money.of(EUR, 1.00), 19.0);
		Article a9 = new Article("Tasse Tee", Category.GETRAENKE, Money.of(EUR, 0.85), 19.0);
		Article a10 = new Article("Wasserflaschen 0,5l", Category.GETRAENKE, Money.of(EUR, 0.61), 19.0);
		Article a11 = new Article("Orangensaft in L", Category.GETRAENKE, Money.of(EUR, 1.86), 19.0);
		Article a12 = new Article("Apfelsaft in L", Category.GETRAENKE, Money.of(EUR, 1.64), 19.0);
		//create LoanArticle
		Article a13 = new Article("Stehtisch", Category.LEIHWAREN, Money.of(EUR, 10.00), 19.0);
		Article a14 = new Article("Zapfanlage", Category.LEIHWAREN, Money.of(EUR, 15.00), 19.0);
		Article a15 = new Article("Sonnenschirm", Category.LEIHWAREN, Money.of(EUR, 20.00), 19.0);
		
		if (articleCatalog.findAll().iterator().hasNext()){
			return;
		}
		
		articleCatalog.save(a1);
		articleCatalog.save(a2);
		articleCatalog.save(a3);
		articleCatalog.save(a4);
		articleCatalog.save(a5);
		articleCatalog.save(a6);
		articleCatalog.save(a7);
		articleCatalog.save(a8);
		articleCatalog.save(a9);
		articleCatalog.save(a10);
		articleCatalog.save(a11);
		articleCatalog.save(a12);
		articleCatalog.save(a13);
		articleCatalog.save(a14);
		articleCatalog.save(a15);
		
		for(Article article : articleCatalog.findAll()){
			InventoryItem inventoryItem = new InventoryItem(article.createProductFromArticle(),
					new Quantity(100, Units.METRIC, RoundingStrategy.ROUND_ONE));
			inventory.save(inventoryItem);
		}
		
		/**
		 * initializes some {@link utfcatering.model.Package}s to fill up the Shop at the first launch.
		 */
		
		Package Konferenz;
		HashMap<Article, Integer> Inhalt1 = new HashMap<Article, Integer>();
		Inhalt1.put(a10, 2);
		Inhalt1.put(a8, 2);
		Konferenz = new Package("Konferenz", Inhalt1);
		packageRepository.save(Konferenz);
		
		Package Fruehstueck;
		HashMap<Article, Integer> Inhalt2 = new HashMap<Article, Integer>();
		Inhalt2.put(a2, 2);
		Inhalt2.put(a7, 1);
		Fruehstueck = new Package("Frühstück", Inhalt2);
		packageRepository.save(Fruehstueck);
		
		Package Brunch;
		HashMap<Article, Integer> Inhalt3 = new HashMap<Article, Integer>();
		Inhalt3.put(a8, 3);
		Inhalt3.put(a4, 1);
		Inhalt3.put(a5, 2);
		Inhalt3.put(a1, 1);
		Brunch = new Package("Brunch", Inhalt3);
		packageRepository.save(Brunch);
		
		Package Workshop;
		HashMap<Article, Integer> Inhalt4 = new HashMap<Article, Integer>();
		Inhalt4.put(a3, 3);
		Inhalt4.put(a8, 2);
		Inhalt4.put(a10, 1);
		Inhalt4.put(a5, 1);
		Workshop = new Package("Workshop", Inhalt4);
		packageRepository.save(Workshop);
		
		
		
	}
	/**
	 * initializes some {@link utfcatering.model.User}s, one {@link utfcatering.model.Employee} (Admin) and one {@link utfcatering.model.Client}, to fill up the Client- and EmployeeList at the first launch.
	 */
	private void initializeUsers(UserAccountManager userAccountManager,UserRepository userRepository){
			Employee dennis;
			Role adminRole =new Role("ROLE_ADMIN");
			UserAccount acc1=userAccountManager.create("dennis","1234",adminRole);
			acc1.setFirstname("Dennis");
			acc1.setLastname("Koerte");
			double salary1 = 22470.89;
			dennis = new Employee(acc1, "dennis", "Schlueterstrasse 46, 01277 Dresden", "18.11.1991", salary1);
			userAccountManager.save(acc1);
			userRepository.save(dennis);
			
			User kevin;
			Role clientRole =new Role("ROLE_CLIENT");
			UserAccount acc2=userAccountManager.create("kevin","123",clientRole);
			acc2.setFirstname("Kevin");
			acc2.setLastname("Goelzner");
			kevin = new User(acc2, "kevin", "Klopstockstraße 45, 01157 Dresden");
			userAccountManager.save(acc2);
			userRepository.save(kevin);
			
		}
}
