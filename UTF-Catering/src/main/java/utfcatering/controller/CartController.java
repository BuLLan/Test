package utfcatering.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.quantity.RoundingStrategy;
import org.salespointframework.quantity.Units;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import utfcatering.model.Article;
import utfcatering.model.CateringOrder;
import utfcatering.model.CateringOrderLine;
import utfcatering.model.CateringOrderRepositiry;
import utfcatering.model.WebCalendar;
 
/**
 * controlls the Cart actions
 * @author Kevin
 *
 */

@PreAuthorize("isAuthenticated()")
@Controller
public class CartController {

	private final UserAccountManager userAccountManager;
	private WebCalendar calendar = WebCalendar.getInstance();
	private final CateringOrderRepositiry cateringOrderRepositiry;
	
	/**
	 * Create new {@link CartController} with given {@link OrderManager}
	 * 
	 * @param orderManager must not be {@literal null}.
	 */
	
	@Autowired
	public CartController(UserAccountManager userAccountManager, CateringOrderRepositiry cateringOrderRepositiry){ 
		 
		Assert.notNull(userAccountManager, "userAccountManager must not be null");
		this.userAccountManager = userAccountManager;
		this.cateringOrderRepositiry = cateringOrderRepositiry;
	}
	

	/**
	 * Adds an {@link Article}
	 * @param article Article to be added
	 * @param packages Package to be added
	 * @param quantityarticle Quantity of articles
	 * @param quantitypackage Quantity of packages (should be 1)
	 * @param cart Instance of Cart
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/cart", method = RequestMethod.POST)
	public String addLoanArticle(@RequestParam("pid") Product product, 
			@RequestParam("number") int quantity, 
			ModelMap modelMap, HttpSession session){
		
		if(quantity <= 0){
			quantity = 1;
		}
		
		Cart cart = getCart(session);
		cart.addOrUpdateItem(product, new Quantity(quantity, Units.METRIC, RoundingStrategy.ROUND_ONE));
		
		
		return "redirect:/warenKorb";
	}
	
	/**
	 * @param cart is the Cart for the {@link utfcatering.model.Client}
	 * @return "cart"
	 */
	
	@ModelAttribute("cart")
	private Cart getCart(HttpSession session) {

		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}

		return cart;
	}
	
	/**
	 * calls a view of the current content of the cart
	 */
	
	
	@RequestMapping(value = "/warenKorb", method = RequestMethod.GET)
	public String warenKorb(ModelMap model, HttpSession session) {
		Cart cart = getCart(session);
		if(cart == null){
			model.addAttribute("priceWithMwSt", 0.0);
			model.addAttribute("mwSt", 0.0);
			return "warenKorb";
		}
		if(cart.isEmpty()){
			model.addAttribute("priceWithMwSt", 0.0);
			model.addAttribute("mwSt", 0.0);
			return "warenKorb";
		}
		double priceWithMwSt = cart.getPrice().getAmount().doubleValue(),
				mwSt = priceWithMwSt*19/100;
		mwSt *= 100;
		mwSt = Math.ceil(mwSt);
		mwSt /= 100;
		
		priceWithMwSt += mwSt;
		
	
		
		model.addAttribute("priceWithMwSt", priceWithMwSt);
		model.addAttribute("mwSt", mwSt);
		return "warenKorb";
	}
	
	/**
	 * Checks out the current state of the {@link Cart}. Using a method parameter of type {@code Optional<UserAccount>}
	 * annotated with {@link LoggedIn} you can access the {@link UserAccount} of the currently logged in user.
	 * 
	 * @param session must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return "warenKorb"
	 */
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkout(HttpSession session, @LoggedIn Optional<UserAccount> userAccount,
			 @RequestParam("date") String date, @RequestParam("starttime") String starttime,
			 @RequestParam("endtime") String endtime, @RequestParam("adresse") String adresse,
			 @RequestParam("event") String event, ModelMap modelMap) {

		if(date == null || starttime == null || endtime == null || adresse == null || event == null){
			return "redirect:/warenKorb";
		}
		
		if(!userAccount.isPresent()){
			modelMap.addAttribute("error", "UserAccountError!");
			return warenKorb(modelMap, session);
		}
		
		if(date.isEmpty() || starttime.isEmpty() || endtime.isEmpty() || adresse.isEmpty() || 
				event.isEmpty()){
			modelMap.addAttribute("error", "Alle Felder müssen ausgefüllt sein!");
			return warenKorb(modelMap, session);
		}
	
		LocalDate day = null;
		try{
			day = LocalDate.parse(date);
		}
		catch(DateTimeParseException ex){
			modelMap.addAttribute("error", "Datumformat ist falsch!");
			return warenKorb(modelMap, session);
		}
		
		
		if(!day.isAfter(LocalDate.now()) || adresse.isEmpty()){
			return "redirect:/warenKorb";
		}
		
		LocalDateTime start = LocalDateTime.of(day, LocalTime.of(Integer.parseInt(starttime), 00));
		LocalDateTime end = LocalDateTime.of(day, LocalTime.of(Integer.parseInt(endtime), 00));
		if(!start.isBefore(end)){
			modelMap.addAttribute("error", "Zeit ist falsch!");
			return warenKorb(modelMap, session);
		}
		end = end.minusMinutes(1);
		
		LocalDateTime heute = LocalDateTime.now();
				
		
		if(!this.calendar.canOrderBeAdded(start, end)){
			modelMap.addAttribute("error", "Zu dieser Zeit ist schon eine Veranstaltung geplant!");
			return warenKorb(modelMap, session);
		}
		
		if(start.isBefore(heute.plusDays(3))){
			modelMap.addAttribute("error", "Eine Veranstaltung muss min. 4 Tage im voraus geplant werden!");
			return warenKorb(modelMap, session);
		}
		Cart cart = getCart(session);
		
		if(cart.isEmpty()){
			modelMap.addAttribute("error", "Warenkorb ist leer!");
			return warenKorb(modelMap, session);
		}
		
		if(userAccount.isPresent()){
			if(userAccountManager.contains(userAccount.get().getIdentifier())){
				CateringOrder order = new CateringOrder(userAccount.get(), adresse, event, start, end);
				for(CartItem item : cart){
					Product ff = item.getProduct();
					order.add(new CateringOrderLine(ff, item.getQuantity()));
				}
				order.calculateMwSt();
				order.calculateEmployeePrice();
				this.cateringOrderRepositiry.save(order);
				cart.clear();
				calendar.addOrderWithDate(start, end, order);
			}
		}

		
		return "redirect:/warenKorb";
	}
	
	/**
	 * called to delete a {@link org.salespointframework.catalog.Product} 
	 * @param pid is the ID of the {@link org.salespointframework.catalog.Product} 
	 * @return "warenKorb"
	 */

	@RequestMapping(value = "/deleteUnit/{pid}", method = RequestMethod.DELETE)
	public String cartUnitDelete(@ModelAttribute Cart cart, @PathVariable String pid, ModelMap modelMap ) {

		cart.removeItem(pid);
		return "redirect:/warenKorb";
	}
	
	/**
	 * called to change the amount of a {@link org.salespointframework.catalog.Product} in the cart
	 * @param pid is the ID of the {@link org.salespointframework.catalog.Product} 
	 * @param newAmount is the new Amount of the {@link org.salespointframework.catalog.Product} in the cart 
	 * @return "warenKorb"
	 */
	
	@RequestMapping(value = "/changeAmount/{pid}", method = RequestMethod.POST)
	public String changeAmount(@ModelAttribute Cart cart, @PathVariable String pid, 
			ModelMap modelMap, @RequestParam("number") String newAmount) {
	
		if(newAmount.isEmpty()){
			return "redirect:/warenKorb";
		}
		
		int amount = Integer.parseInt(newAmount);
		Product product = cart.getItem(pid).get().getProduct();
		cart.removeItem(pid);
		
		cart.addOrUpdateItem(product, new Quantity(amount, Units.METRIC, RoundingStrategy.ROUND_ONE));
		
		return "redirect:/warenKorb";
	}
}
