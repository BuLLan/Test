package utfcatering.controller;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import utfcatering.model.UserRepository;

@Controller
/**
 * The WelcomeController just returns the welcome.html
 * @author Salespoint
 *
 */
public class WelcomeController { 
	
	private final UserRepository userRepository;
	
	/**
	 * Constructor: Creates a new WelcomeController
	 * @param userRepository the currently used userRepostiory
	 */
	
	@Autowired
	
	public WelcomeController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	/**
	 * Returns the welcome.html
	 * @return "welcome"
	 */
	
	@RequestMapping({"/", "index"})
	
    public String index(ModelMap modelMap, @LoggedIn UserAccount userAccount) {
		return "welcome";
    }

}