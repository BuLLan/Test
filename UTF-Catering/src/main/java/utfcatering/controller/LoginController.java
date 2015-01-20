package utfcatering.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Returns the login page
 * @author Dennis
 *
 */
@Controller
public class LoginController {
	
	/**
	 * Returns the login page
	 * @return "login"
	 */
	
	@RequestMapping({"/login"}) 
    public String manageLogin() {
		return "login";
    }

	/**
	 * Returns the login page after logging out
	 * @return "login"
	 */
	
	@RequestMapping({"/logout"})
    public String manageLogout() {
		return "welcome";
    }
	
	/**
	 * Returns the following page after login
	 * @return "klappt"
	 */
	
	@RequestMapping({"/klappt"})

    public String klappt() {
		return "klappt";
    }
}
