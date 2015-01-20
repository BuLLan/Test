/** 
 *
 * @author    Igor Misirev
 * @version   0.5
 * @since     28-12-2014
 * 
 */

package utfcatering.controller;

import java.util.ArrayList;

import javax.validation.Valid;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.quantity.RoundingStrategy;
import org.salespointframework.quantity.Units;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import utfcatering.model.Article.Category;
import utfcatering.model.ArticleCatalog;
import utfcatering.model.validation.ProductInsertForm;


@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LagerController {
	
	private final ArticleCatalog articleCatalog;
	private final Inventory<InventoryItem> inventory;
	
	private final MessageSourceAccessor messageSourceAccessor;
	
	InventoryItemIdentifier id;
	
	/**
	 * Constructor
	 * initializes articleCatalog, inventory and messageSource for further use
	 * @param articleCatalog holds all {@link utfcatering.model.Article}s
	 * @param messageSource is to highlight the right item in the navigation
	 */

	@Autowired
	public LagerController(ArticleCatalog articleCatalog,Inventory<InventoryItem> inventory,MessageSource messageSource) {

		Assert.notNull(articleCatalog, "ArticleCatalog must not be null!");
		Assert.notNull(inventory, "Inventory must not be null!");
		
		this.articleCatalog = articleCatalog;
		this.inventory = inventory;
		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
		
	}	
	
	/**
	 * called to show a view of the Stock sorted by category
	 * @return "lager"
	 */
	
	
	@RequestMapping({"/lager"})
	public String lager(ModelMap modelMap) {
		
		modelMap.addAttribute("catalog", inventory.findAll());
		Iterable<InventoryItem> catalogSpeisen = new ArrayList<InventoryItem>();
		Iterable<InventoryItem> catalogGetraenke = new ArrayList<InventoryItem>();
		Iterable<InventoryItem> catalogLeihwaren = new ArrayList<InventoryItem>();

		for(InventoryItem ii : inventory.findAll()){
			if( ii.getProduct().getCategories().iterator().next().equals(Category.SPEISEN.toString())){
				((ArrayList<InventoryItem>)catalogSpeisen).add(ii);
			}
			
			if( ii.getProduct().getCategories().iterator().next().equals(Category.GETRAENKE.toString())){
				((ArrayList<InventoryItem>)catalogGetraenke).add(ii);
			}
			
			if( ii.getProduct().getCategories().iterator().next().equals(Category.LEIHWAREN.toString())){
				((ArrayList<InventoryItem>)catalogLeihwaren).add(ii);
			}
		}
		modelMap.addAttribute("catalogSpeisen", catalogSpeisen);
		modelMap.addAttribute("catalogGetraenke", catalogGetraenke);
		modelMap.addAttribute("catalogLeihwaren", catalogLeihwaren);
		modelMap.addAttribute("articles", this.articleCatalog.findAll());
		modelMap.addAttribute("lagerS", messageSourceAccessor.getMessage("class.selected"));
		return "lager";
	}
	
	/**
	 * called to add a {@link org.salespointframework.catalog.Product} to the stock
	 * @param productInsertForm is a form for the Admin to add a new {@link org.salespointframework.catalog.Product}
	 * @param result is the result if the productInsertForm is filled valid
	 * @return "lager"
	 */
	
	@RequestMapping(value = "/insertNew", method = RequestMethod.GET)
	public String insertNew(ModelMap modelMap, 
			@ModelAttribute("produktInsertForm")@Valid ProductInsertForm produktInsertForm,
			BindingResult result){

		
		if (result.hasErrors()) {
			return this.lager(modelMap.addAttribute("error", "Beim Hinzufügen eines Artikels müssen alle Felder ausgefüllt werden!!!"));
		}
		
		if(!this.articleCatalog.exists(produktInsertForm.getId())){
			return this.lager(modelMap.addAttribute("error", "Es gibt kein Article im Articlecatalog!"));
		}
		
		Product article = this.articleCatalog.findOne(produktInsertForm.getId()).get().createProductFromArticle();
		
		try{
			Integer.parseInt(produktInsertForm.getAmount());
		}catch(NumberFormatException ex){
			return this.lager(modelMap.addAttribute("error", "Eingegebene Anzahl hat falsches Format!"));
		}
		
		for(InventoryItem item : this.inventory.findAll()){
			if(item.getProduct().getName().equals(article.getName())){
				
				item.increaseQuantity(new Quantity(Integer.parseInt(produktInsertForm.getAmount()), Units.METRIC, RoundingStrategy.ROUND_ONE));
				int res = item.getQuantity().getAmount().intValueExact();
				if(res < 0 ){
					res = Math.abs(res);
					//String s = String.valueOf(res);
					item.increaseQuantity(new Quantity(res, Units.METRIC, RoundingStrategy.ROUND_ONE));
					
					inventory.save(item);
					return "redirect:lager";
				}
				else inventory.save(item);
				return "redirect:lager";
			}
		}

		InventoryItem inventoryItem = new InventoryItem(article, 
				new Quantity(Integer.parseInt(produktInsertForm.getAmount()), Units.METRIC, RoundingStrategy.ROUND_ONE));
		inventory.save(inventoryItem);
				
		return "redirect:lager"; 
	}
	
	/**
	 * called to delete a {@link org.salespointframework.catalog.Product} from the stock
	 * @param pid is the ID of the {@link org.salespointframework.catalog.Product}
	 * @return "lager"
	 */
	
	@RequestMapping(value = "/delete/{pid}", method = RequestMethod.DELETE)
	public String articleRemove(@PathVariable InventoryItemIdentifier pid ) {
		
		if(!inventory.exists(pid)){
			return "redirect:/lager";
		}
		
		inventory.delete(pid);
		
		return "redirect:/lager";
	}
	
}
