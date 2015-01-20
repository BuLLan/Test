/** 
 *
 * @author    Igor Misirev, Dennis Körte
 * @version   0.8
 * @since     28-12-2014
 * 
 */

package utfcatering.controller;

import org.apache.commons.io.FileUtils;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.joda.money.Money;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import utfcatering.model.Article.Category;
import utfcatering.model.ArticleCatalog;
import utfcatering.model.CateringOrder;
import utfcatering.model.CateringOrderRepositiry;
import utfcatering.model.PackageRepository;
import utfcatering.model.Package;
import utfcatering.model.Article;
import utfcatering.model.WebCalendar;

@Controller

/**
 * basic Shop View call
 * @return basic Shop View with all Options
 * @param servletContext is needed for the image-upload
 */

public class ShopController implements ServletContextAware {
	
	private ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
	this.servletContext = servletContext;
	 
	}

	/** 
	 * @param packageRepository is the Repository for all {@link utfcatering.model.Package}s
	 * @param orderManager is to Manage all {@link utfcatering.model.Orders}s
	 * @param calendar is a new Instance of {@link utfcatering.model.WebCalendar}
	 * @param cateringOrderRepositiry is the Repository for all {@link utfcatering.model.CateringOrder}s,
	 * {@link utfcatering.model.CateringOrder} inherits from {@link utfcatering.model.Order}
	 * @return if validation failed "AdminAddEmployee", else "AdminManageUser"
	 */
	
	private final PackageRepository packageRepository;
	private final ArticleCatalog articleCatalog;
	private final Inventory<InventoryItem> inventory;
	private final OrderManager<CateringOrder> orderManager;
	private final CateringOrderRepositiry cateringOrderRepositiry;
	private WebCalendar calendar = WebCalendar.getInstance();
	private Package newPack = null;
	private String  old = null;

	@Autowired
	public ShopController(OrderManager<CateringOrder> orderManager, PackageRepository packageRepository,
			ArticleCatalog articleCatalog, Inventory<InventoryItem> inventory,
			CateringOrderRepositiry cateringOrderRepositiry) {

		Assert.notNull(orderManager, "orderManager must not be null!");
		Assert.notNull(packageRepository, "packageRepository must not be null!");
		Assert.notNull(articleCatalog, "articleCatalog must not be null!");
		Assert.notNull(inventory, "inventory must not be null!");
		Assert.notNull(cateringOrderRepositiry, "cateringOrderRepositiry must not be null!");
		
		
		this.articleCatalog = articleCatalog;
		this.packageRepository = packageRepository;
		this.inventory = inventory;
		this.orderManager = orderManager;
		this.cateringOrderRepositiry = cateringOrderRepositiry;
	}
	
	/**
	 * is called to show all {@link utfcatering.model.Package}s
	 * @return "shop"
	 */
	@RequestMapping(value = "/shop", method = RequestMethod.GET)
	public String shop(ModelMap modelMap){
		modelMap.addAttribute("packages", packageRepository.findAll());
		return "shop";
	}
	
	/**
	 * is called to show all {@link utfcatering.model.Article}s
	 * @return "speisen"
	 */
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public String all(ModelMap modelMap){
		modelMap.addAttribute("category", "Speisen");
		modelMap.addAttribute("packages", articleCatalog.findAll());
		return "speisen";
	}
	
	/**
	 * is called to show all {@link utfcatering.model.Article}s with the Category "Food"
	 * @return "speisen"
	 */
	
	@RequestMapping(value = "/speisen", method = RequestMethod.GET)
	public String speisen(ModelMap modelMap){
		modelMap.addAttribute("category", "Speisen");
		modelMap.addAttribute("packages", articleCatalog.findByCategory(Category.SPEISEN));
		return "speisen";
	}
	
	/**
	 * is called to show all {@link utfcatering.model.Article}s with the Category "Drinks"
	 * @return "speisen"
	 */
	
	@RequestMapping(value = "/getraenke", method = RequestMethod.GET)
	public String getraenke(ModelMap modelMap){
		modelMap.addAttribute("category", "Getraenke");
		modelMap.addAttribute("packages", articleCatalog.findByCategory(Category.GETRAENKE));
		return "speisen";
	}
	
	/**
	 * is called to show all {@link utfcatering.model.Article}s with the Category "Loan Article"
	 * @return "speisen"
	 */
	
	@RequestMapping(value = "/leihwaren", method = RequestMethod.GET)
	public String leihwaren(ModelMap modelMap){
		modelMap.addAttribute("category", "Leiwaren");
		modelMap.addAttribute("packages", articleCatalog.findByCategory(Category.LEIHWAREN));
		return "speisen";
	}
	
	/**
	 * is called to add new {@link utfcatering.model.Article}s 
	 * @param modelMap holds variables for the next Views
	 * @param mwst is the betterment tax for the new {@link utfcatering.model.Article}
	 * "error" is to show Errors, if something is not in a valid form, or anything is not filled
	 * @return "speisen", if all is valid and the new {@link utfcatering.model.Article} does not already exist
	 * @param price is the Price without the the betterment tax
	 * @param priceWithMwSt is the total price for the new {@link utfcatering.model.Article}
	 */

	
	@RequestMapping(value = "/hinzufuegen", method = RequestMethod.POST)
	public String hinzufuegen(ModelMap modelMap,
			@RequestParam("category") String category,
			@RequestParam("name") String name,
			@RequestParam("preis") String preis,
			@RequestParam("mwst") String mwst,
			@RequestParam("image") MultipartFile image){

		if(category == null || name == null || preis == null || 
				mwst == null || image == null){			
			if(category != null){
				if(category.equals("Getraenke"))
					return "redirect:/getraenke";
				if(category.equals("Leiwaren"))
					return "redirect:/leihwaren";
			}
			return "redirect:/speisen";
		}
		
		
		if(category.isEmpty() || name.isEmpty() || 
				preis.isEmpty() ||mwst.isEmpty()){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Alle Felder müssen ausgefüllt werden!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Alle Felder müssen ausgefüllt werden!"));
			return this.speisen(modelMap.addAttribute("error", "Alle Felder müssen ausgefüllt werden!"));
		}
		
		
		if(this.articleCatalog.findByName(name).iterator().hasNext()){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Produkt existiert schon!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Produkt existiert schon!"));
			return this.speisen(modelMap.addAttribute("error", "Produkt existiert schon!"));
			
		}
		
		
		
		
		double mwSt;
		double price;
		double priceWithMwSt;
		
		try{
			mwSt = Double.parseDouble(mwst);
			price = Double.parseDouble(preis);
		}catch(NumberFormatException ex){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Eingabe für Preis oder MwSt ist falsch!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Eingabe für Preis oder MwSt ist falsch!"));
			return this.speisen(modelMap.addAttribute("error", "Eingabe für Preis oder MwSt ist falsch!"));
		}
		
		if(mwSt <= 0 || price <= 0){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Preis oder MwSt darf nicht negativ sein!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Preis oder MwSt darf nicht negativ sein!"));
			return this.speisen(modelMap.addAttribute("error", "Preis oder MwSt darf nicht negativ sein!"));
		}
		
		if(price >= 10000){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Preis darf nicht mehr als 9999.99 € sein!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Preis darf nicht mehr als 9999.99 € sein!"));
			return this.speisen(modelMap.addAttribute("error", "Preis darf nicht mehr als 9999.99 € sein!"));
		}
		
		if(mwSt > 100 ){
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "MwSt darf nicht mehr als 100% sein!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "MwSt darf nicht mehr als 100% sein!"));
			return this.speisen(modelMap.addAttribute("error", "MwSt darf nicht mehr als 100% sein!"));
		}
		
		priceWithMwSt = price + (price*mwSt/100);
		priceWithMwSt *= 100;
		priceWithMwSt = Math.round(priceWithMwSt);
		priceWithMwSt /= 100;
		
		price *= 100;
		price = Math.ceil(price);
		price /= 100;
		
		
		String pries = "EUR " + String.valueOf(price);
		
		if (!image.isEmpty()) {
			try {
				validateImage(image);
				} 
			catch (RuntimeException re) {
				return "redirect:/speisen";
				}
			try {
				saveImage(name + ".jpg", image);
			} 
			catch (IOException e) {
				return "redirect:/speisen";
			}
		}
		
		Article a1;
		Category cat = Category.SPEISEN;
		modelMap.addAttribute("category", "Speisen");
		
		if(category.equals("Leiwaren")){
			cat = Category.LEIHWAREN;
			modelMap.addAttribute("category", "Leiwaren");
		}
		if(category.equals("Getraenke")){
			cat = Category.GETRAENKE;
			modelMap.addAttribute("category", "Getraenke");
		}
		
		
		try{
			a1 = new Article(name, cat, Money.parse(pries), mwSt);
		}catch(IllegalArgumentException e){
			System.out.println("IllegalArgumentException!!");
			if(category.equals("Getraenke"))
				return this.getraenke(modelMap.addAttribute("error", "Einigen Eingaben sind falsch!"));
			if(category.equals("Leiwaren"))
				return this.leihwaren(modelMap.addAttribute("error", "Einigen Eingaben sind falsch!"));
			return this.speisen(modelMap.addAttribute("error", "Einigen Eingaben sind falsch!"));
		}
		this.articleCatalog.save(a1);
		
		if(category.equals("Leiwaren")){
			return "redirect:/leihwaren";
		}
		
		if(category.equals("Getraenke")){
			return "redirect:/getraenke";
		}
		return "redirect:/speisen";
	}
	
	/**
	 * checks if the MultipartFile is a image/jpeg
	 * @param image is the given MultipartFile
	 */
	
	private void validateImage(MultipartFile image) {
		if (!image.getContentType().equals("image/jpeg")) {
			throw new RuntimeException("Only JPG images are accepted");
		}
	}
	
	/**
	 * converts the MultipartFile to a file and saves it in the path "/resources/img/"
	 * @param file is the converted MultipartFile
	 */
	 
	private void saveImage(String filename, MultipartFile image)
	throws RuntimeException, IOException {
		try {
			File file = new File(servletContext.getRealPath("/resources/img/") + "/"
					+ filename);
			 
			FileUtils.writeByteArrayToFile(file, image.getBytes());
			System.out.println("Go to the location:  " + file.toString()
			+ " on your computer and verify that the image has been stored.");
		} catch (IOException e) {
		throw e;
		}
	}
	
	/**
	 * called to delete a {@link utfcatering.model.Article}
	 * @param pid is the id of the Article
	 * @param res is the category of the {@link utfcatering.model.Article}
	 * @param modelMap variables for further Views
	 * @return "speisen" filtered by @param res 
	 */

	
	@RequestMapping(value = "/deleteArticle/{pid}", method = RequestMethod.DELETE)
	public String deleteArticle(ModelMap modelMap, @PathVariable ProductIdentifier pid, 
			@RequestParam("category") String category) {
		String res = "speisen";
		if(category.equals("Getraenke")){
			res = "getraenke";
		}
		if(category.equals("Leiwaren")){
			res = "leihwaren";
		}
		
		if(category == null || pid == null ){
			return "redirect:/"+res;
		}
		
		if(category.isEmpty()){
			return "redirect:/"+res;
		}
		
		if(!articleCatalog.exists(pid)){
			return "redirect:/"+res;
		}
		
		this.articleCatalog.delete(pid);
		
		modelMap.addAttribute("category", category);
		return "redirect:/"+res;
		
	}
	
	/**
	 * called to edit a {@link utfcatering.model.Package}
	 * @param pid is the id of the Package
	 * @return "edit"
	 */
	
	@RequestMapping(value = "/editArticle/{pid}", method = RequestMethod.GET)
	public String editArticle(ModelMap modelMap, @PathVariable ProductIdentifier pid,
			 @RequestParam("category") String category) {
		
		if(category == null || pid == null ){
			if(category != null){
				if(category.equals("Getraenke"))
					return "redirect:/getraenke";
				if(category.equals("Leiwaren"))
					return "redirect:/leihwaren";
			}
			return "redirect:/speisen";
		}
		
		if(category.isEmpty()){
			if(category.equals("Getraenke"))
				return "redirect:/getraenke";
			if(category.equals("Leiwaren"))
				return "redirect:/leihwaren";
			return "redirect:/speisen";
		}
		
		if(!articleCatalog.exists(pid)){
			if(category.equals("Getraenke"))
				return "redirect:/getraenke";
			if(category.equals("Leiwaren"))
				return "redirect:/leihwaren";
			return "redirect:/speisen";
		}

		modelMap.addAttribute("article", this.articleCatalog.findOne(pid).get());
		modelMap.addAttribute("category", category);
		
		return "/edit";
	}
	
	/**
	 * called to finish the edit of a {@link utfcatering.model.Article}
	 * @param pid is the id of the Article
	 * @param oldfile is the picture from the old Article
	 * @param newfile is a copy of the @param oldfile
	 * @param altName is the old Name  of the {@link utfcatering.model.Article}
	 * @param name is the new Name  of the {@link utfcatering.model.Article}
	 * @param price is the price for the {@link utfcatering.model.Article}
	 * @param e is the IOException
	 * @return "speisen"
	 */
	
	@RequestMapping(value = { "/editArticle" }, method = RequestMethod.GET)
	public String editArticle(ModelMap modelMap, @RequestParam("name") String name, 
			 @RequestParam("pid") ProductIdentifier pid, @RequestParam("price") String price,
			 @RequestParam("category") String category) {
		
		if(category == null || name == null || price == null || pid == null){			
			return "redirect:/edit";
		}
		
		if(!articleCatalog.exists(pid)){
			if(category.equals("Getraenke"))
				return "redirect:/getraenke";
			if(category.equals("Leiwaren"))
				return "redirect:/leihwaren";
			return "redirect:/speisen";
		}
		
		if(category.isEmpty() || name.isEmpty() || price.isEmpty()){
			modelMap.addAttribute("error", "Alle Felder müssen ausgefüllt sein!");
			modelMap.addAttribute("article", this.articleCatalog.findOne(pid).get());
			return editArticle(modelMap, pid, category);
		}
		
		
		Article article = this.articleCatalog.findOne(pid).get();
		String altName = article.getName();
		
		File oldfile = new File(servletContext.getRealPath("/resources/img/") + "/" + altName + ".jpg");
		File newfile = new File(servletContext.getRealPath("/resources/img/") + "/" + name + ".jpg");
		
		try {
			if(!altName.equals(name)){
			FileUtils.copyFile(oldfile, newfile);
			}
			} 
			catch (IOException e) {
				return "redirect:/edit";
			}
		
		article.setName(name);
		try{
			article.setPrice(Money.parse("EUR " + price));
		}catch(Exception ex){
			modelMap.addAttribute("error", "Eingabe für Preis ist falsch!");
			modelMap.addAttribute("article", this.articleCatalog.findOne(pid).get());
			return editArticle(modelMap, pid, category);
		}
		
		
		this.articleCatalog.save(article);
		
		for(InventoryItem item : this.inventory.findAll()){
			if(item.getProduct().getName().equals(altName)){
				item.getProduct().setName(name);
				item.getProduct().setPrice(Money.parse("EUR " + price));
				this.inventory.save(item);
				break;
			}
		}
		
		modelMap.addAttribute("category", category);
		if(category.equals("Leiwaren")){
			return "redirect:/leihwaren";
		}
			
		if(category.equals("Getraenke")){
			return "redirect:/getraenke";
		}
		
		return "redirect:/speisen";
	}
	
	/**
	 * called to edit  a {@link utfcatering.model.Package}
	 * @param id is the id of the {@link utfcatering.model.Package}
	 * @param newPack is the current used Package
	 * @param articles is a list of all Articles in the {@link utfcatering.model.Package}
	 * @return "insertnewpackage"
	 */
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/editPackage", method = RequestMethod.GET)
	public String editPackage(ModelMap modelMap, @RequestParam("id") ProductIdentifier id){
		
		if(id == null){
			return shop(modelMap);
		}
		if(!packageRepository.exists(id)){
			return shop(modelMap);
		}
		this.newPack = this.packageRepository.findOne(id).get();
		modelMap.addAttribute("articles", this.articleCatalog.findAll());
		modelMap.addAttribute("package", this.newPack);
		return "insertnewpackage";
	}
	
	/**
	 * called to insert  a new {@link utfcatering.model.Package}
	 * @param newPack is the current used Package
	 * @param articles is a list of all Articles in the {@link utfcatering.model.Package}
	 * @return "insertnewpackage"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/insertnewpackage", method = RequestMethod.GET)
	public String hinzufuegen(ModelMap modelMap){
		modelMap.addAttribute("articles", this.articleCatalog.findAll());
		modelMap.addAttribute("package", this.newPack);
		return "insertnewpackage";
	}
	
	/**
	 * called to insert  a new {@link utfcatering.model.Package} with differnce @param newPack is null
	 * @param newPack is the current used Package
	 * @param articles is a list of all Articles in the {@link utfcatering.model.Package}
	 * @return "insertnewpackage"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/insertnewpackageNew", method = RequestMethod.GET)
	public String hinzufuegenNew(ModelMap modelMap){
		this.newPack = null;
		return "redirect:insertnewpackage";
	}
	
	/**
	 * called to add {@link utfcatering.model.Article}s to a {@link utfcatering.model.Package}
	 * @param id is the ID of an {@link utfcatering.model.Article} 
	 * @param amount the amount of an {@link utfcatering.model.Article}
	 * @return "insertnewpackage"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/addPackage", method = RequestMethod.POST)
	public String addPackage(ModelMap modelMap, 
							@RequestParam("id") ProductIdentifier id, 
							@RequestParam("amount") String amount){
		int res;
		
		if(id == null || amount == null){
			return hinzufuegen(modelMap);
		}
		
		if(!articleCatalog.exists(id)){
			return hinzufuegen(modelMap);
		}
		if(amount.isEmpty()){
			modelMap.addAttribute("error", "Anzahl darf nich leer sein!");
			return hinzufuegen(modelMap);
		}
		try {
			res = Integer.parseInt(amount);
		} catch (NumberFormatException e) {
			modelMap.addAttribute("error", "Eingegebe Anzahl hat falsches Format!");
			return hinzufuegen(modelMap);
		}
		if (res <= 0){
			modelMap.addAttribute("error", "Eingegebe Anzahl muss grösser als 0 sein!");
			return hinzufuegen(modelMap);
		}
		if(this.newPack == null){
			Map<Article, Integer> newInhalt = new HashMap<Article, Integer>();
			newInhalt.put(this.articleCatalog.findOne(id).get(), 
					Integer.parseInt(amount));
			
			Package pack = new Package("NewPackage", newInhalt);
			this.newPack = pack;
		}
		else{
			this.newPack.addArticle(this.articleCatalog.findOne(id).get().createProductFromArticle(), 
					Integer.parseInt(amount));
		}
		
		modelMap.addAttribute("package", this.newPack);
		
		return "redirect:insertnewpackage";
	}
	
	/**
	 * called to save the new or edited {@link utfcatering.model.Package} 
	 * @param name is the name of the current {@link utfcatering.model.Package} 
	 * @param image is the image for the current {@link utfcatering.model.Package}
	 * @param old is the old Name 
	 * @param oldfile is the old Image
	 * @param newfile is a copy of the old Image with a new Name
	 * @return "shop"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/savePackage", method = RequestMethod.POST)
	public String savePackage(ModelMap model, 
							@RequestParam(value="name", required=false) String name,
							@RequestParam(value="image",required=false ) MultipartFile image){
		if(name == null){
			return hinzufuegen(model);
		}
		if(name.isEmpty()){
			model.addAttribute("error2", "Name darf nich leer sein!");
			return hinzufuegen(model);
		}
		old = this.newPack.getName();
		File oldfile = new File(servletContext.getRealPath("/resources/img/") + "/" + old + ".jpg");
		File newfile = new File(servletContext.getRealPath("/resources/img/") + "/" + name + ".jpg");
		
		
		if (image.getContentType().equals("image/jpeg")) {
			try {
				saveImage(name + ".jpg", image);
			} 
			catch (IOException e) {
				return "redirect:insertnewpackage";
			}
		}
		
		else {
			try {
			if(!old.equals(name)){
			FileUtils.copyFile(oldfile, newfile);
			}
			} 
			catch (IOException e) {
				return "redirect:insertnewpackage";
			}
		
		}
		
		
		this.newPack.setName(name);
		
		this.packageRepository.save(this.newPack);
		this.newPack = null;
		return "redirect:/shop";
	}
	
	/**
	 * called to remove an {@link utfcatering.model.Article} from the {@link utfcatering.model.Package}
	 * @param pid is the ID of the {@link utfcatering.model.Article}
	 * @return "insertnewpackage"
	 */

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/removeArticle/{pid}", method = RequestMethod.DELETE)
	public String removeArticle(@PathVariable Long pid ) {
		
		this.newPack.removeArticle(pid);
		if(this.newPack.getArticles().isEmpty())
			this.newPack = null;
		
		return "redirect:/insertnewpackage";
	}
	
	/**
	 * called to deleta a {@link utfcatering.model.Package}
	 * @param id is the ID of the {@link utfcatering.model.Package}
	 * @return "shop"
	 */
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/deletePackage", method = RequestMethod.GET)
	public String deletePackage(ModelMap modelMap, @RequestParam("id") ProductIdentifier id){
		this.packageRepository.delete(id);
		return "redirect:/shop";
	}
	
	/**
	 * called to show all {@link utfcatering.model.CateringOrder}s
	 * @return "bestellungen"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/bestellungen")
	public String bestellungen(ModelMap modelMap){
		
		modelMap.addAttribute("orders", this.cateringOrderRepositiry.findByOrderStatus(OrderStatus.OPEN));
		modelMap.addAttribute("paidOrders", orderManager.find(OrderStatus.PAID));
	
		return "bestellungen";
	}
	/**
	 * called to show all {@link utfcatering.model.CateringOrder}s from the logged in Client
	 * @param userAccount is the logged in User
	 * @return "bestellungen", if the  @param userAccount is not logged in @return "klappt"
	 */
	
	@PreAuthorize("hasRole('ROLE_CLIENT')")
	@RequestMapping(value = "/bestellungenClient")
	public String bestellungenClirnt(ModelMap modelMap,@LoggedIn Optional<UserAccount> userAccount){
		
		if(!userAccount.isPresent())
			return "klappt";
		
		modelMap.addAttribute("orders", this.cateringOrderRepositiry.findByUserAccount(userAccount.get()));
	
		return "bestellungen";
	}
	
	/**
	 * confirmed a {@link utfcatering.model.CateringOrder}s from the logged in Client
	 * @param pid is the Order-ID
	 * @param order is the current {@link utfcatering.model.CateringOrder} 
	 * @return "bestellungen"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/confirm/{pid}", method = RequestMethod.POST)
	public String confirm(@PathVariable OrderIdentifier pid ) {
		
		CateringOrder order = this.cateringOrderRepositiry.findByOrderIdentifier(pid);
		
		order.setConfirmed(true);
		CateringOrder o = this.calendar.getOrderByTime(order.getStart()).get();
		o.setConfirmed(true);
		this.cateringOrderRepositiry.save(order);
		
		
		return "redirect:/bestellungen";
	}
	
	/**
	 * called to refuse a {@link utfcatering.model.CateringOrder} 
	 * @param pid is the Order-ID
	 * @param order is the current {@link utfcatering.model.CateringOrder} 
	 * @return "bestellungen"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/refuse/{pid}", method = RequestMethod.DELETE)
	public String refuse(@PathVariable OrderIdentifier pid ) {
		
		CateringOrder order = this.cateringOrderRepositiry.findByOrderIdentifier(pid);
		
		
		this.cateringOrderRepositiry.delete(order);
		
		
		return "redirect:/bestellungen";
	}
	
	/**
	 * called to set a {@link utfcatering.model.CateringOrder} paid 
	 * @param pid is the Order-ID
	 * @param order is the current {@link utfcatering.model.CateringOrder} 
	 * @return "bestellungen"
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/pay/{pid}", method = RequestMethod.POST)
	public String pay(@PathVariable OrderIdentifier pid) {
		
		CateringOrder order = this.cateringOrderRepositiry.findByOrderIdentifier(pid);

		
		this.orderManager.payOrder(order);
		this.orderManager.add(order);
		
		return "redirect:/bestellungen";
	}
	
	/**
	 * called to get a bill for the paid {@link utfcatering.model.CateringOrder} 
	 * @param pid is the Order-ID
	 * @param order is the current {@link utfcatering.model.CateringOrder} 
	 * @return "rechnung"
	 */
	
	@PreAuthorize("hasRole('ROLE_CLIENT')")
	@RequestMapping(value = "/rechnung/{pid}", method = RequestMethod.POST)
	public String rechnung(@PathVariable OrderIdentifier pid, ModelMap model) {
		
		CateringOrder order = this.cateringOrderRepositiry.findByOrderIdentifier(pid);

		model.addAttribute("order", order);
		
		double priceWithMwSt = order.getTotalPrice().getAmount().doubleValue(),
				mwSt = priceWithMwSt*19/100;
		mwSt *= 100;
		mwSt = Math.ceil(mwSt);
		mwSt /= 100;
		
		priceWithMwSt += mwSt;
		
		model.addAttribute("priceWithMwSt", priceWithMwSt);
		model.addAttribute("mwSt", mwSt);
		
		return "rechnung";
	}
	
}
