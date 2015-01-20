package utfcatering.controller;

import javax.validation.Valid;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import utfcatering.model.User;
import utfcatering.model.UserRepository;
import utfcatering.model.validation.RegistrationFormExternal;

/**
 *
 * @author Dennis
 *
 */
@Controller
public class ExternRegisterController {

	private final UserAccountManager userAccountManager;  
	private final UserRepository userRepository;
	
	/**
	 * initializing userAccountManager and userRepository for further use
	 * @param userAccountManager
	 * @param userRepository
	 */
	@Autowired
	public ExternRegisterController(UserAccountManager userAccountManager, UserRepository userRepository) {
		this.userAccountManager = userAccountManager;
		this.userRepository = userRepository;
	}
	
	/**
	 * 
	 * @param modelMap holds a formular to be filled in the next View
	 * @return "ExternalRegisterNew"
	 */
	@RequestMapping({"ExternalRegisterNew", "externRegisterNew"})
    public String index(ModelMap modelMap) {
		modelMap.addAttribute("registrationForm", new RegistrationFormExternal());
		modelMap.addAttribute("usernameexist", null);
		return "ExternalRegisterNew";
    }
	
	/**
	 * called after filling the external register formular
	 * @param registrationForm holds all the details of the new User
	 * @param result validation result
	 * @param modelMap variables for the next View
	 * @return "ExternalRegisterNew" if the validation failed, else "login"
	 */
	@RequestMapping("//ExternalRegisterForm")
	public String registerNewExternal(@ModelAttribute("registrationForm") @Valid RegistrationFormExternal registrationForm, BindingResult result, ModelMap modelMap) {

		if(result.hasErrors()) {
			return "ExternalRegisterNew";
		}
		
		UserAccountIdentifier userAccountIdentifier = new UserAccountIdentifier(registrationForm.getUsername());
		
		if (userAccountManager.contains(userAccountIdentifier)==true)
		{
			RegistrationFormExternal r = new RegistrationFormExternal();
			r.setAddress(registrationForm.getAddress());
			r.setFirstname(registrationForm.getFirstname());
			r.setLastname(registrationForm.getLastname());
			r.setPassword(registrationForm.getPassword());
			r.setUsername(registrationForm.getUsername());
			modelMap.addAttribute("registrationForm", r);
			modelMap.addAttribute("usernameexist", "Der angegeben Benutzername ist bereits vergeben.");
			return "ExternalRegisterNew";
		}
		
		UserAccount userAccount = userAccountManager.create(registrationForm.getUsername(), registrationForm.getPassword(), new Role("ROLE_CLIENT"));
		userAccount.setFirstname(registrationForm.getFirstname());
		userAccount.setLastname(registrationForm.getLastname());
		
		userAccountManager.save(userAccount);
		User user;
			user = new User(userAccount, registrationForm.getUsername(), registrationForm.getAddress());
		
		userRepository.save(user);
		



		return "login";
	}
}