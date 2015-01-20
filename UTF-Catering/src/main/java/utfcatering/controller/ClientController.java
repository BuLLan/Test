package utfcatering.controller;


import java.util.Optional;

import org.salespointframework.useraccount.web.LoggedIn;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import utfcatering.model.User;
import utfcatering.model.UserRepository;



/**
 * Controller for Clients
 * @author Dennis
 *
 */
@Controller
@PreAuthorize("hasRole('ROLE_CLIENT')")
public class ClientController {
	
	private final UserRepository userRepository;
	private final UserAccountManager userAccountManager;
	
	/**
	 * Constructor
	 * @param userRepository
	 */
	@Autowired
	public ClientController(UserRepository userRepository, UserAccountManager userAccountManager){
		this.userRepository=userRepository;
		this.userAccountManager=userAccountManager;
		
	}
	
	/**
	 * Shows all information of the account
	 * @param modelMap (given by Spring)
	 * @param userAccount the UserAccount that is currently logged in
	 * @return "MyAccount"
	 */
	@RequestMapping("/MyAccount")
	public String myAccount(ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount){	
		User user = userRepository.findByUserAccount(userAccount.get());
		modelMap.addAttribute("user", user);		
		return "MyAccount";
	}
	
	/**
	 * Client can change his address
	 * @param address the new address
	 * @param userAccount the UserAccount that is currently logged in
	 * @return "redirect:/MyAccount"
	 */
	
	@RequestMapping(value="/changeMyAdress",method=RequestMethod.POST)
	public String changeMyAdress(@RequestParam("adress") String address,@LoggedIn Optional<UserAccount> userAccount){
		if(address.equals(""))
			return "redirect:/MyAccount";
		
		User client=userRepository.findByUserAccount(userAccount.get());
		client.setAddress(address);
		userRepository.save(client);
		
		return "redirect:/MyAccount";
	}
	
	/**
	 * called to disable the userAccount that is currently logged in
	 * @param userAccount the UserAccount that is currently logged in
	 * @return "welcome"
	 */
	
	
	@RequestMapping(value = "/disableMyAccount", method = RequestMethod.POST)
	public String disableMyAccount(@LoggedIn Optional<UserAccount> userAccount) {
		userAccountManager.disable(userAccount.get().getIdentifier());
		userRepository.delete(userRepository.findByUserAccount(userAccount.get()));
		
		return "welcome";
	}
	
}
