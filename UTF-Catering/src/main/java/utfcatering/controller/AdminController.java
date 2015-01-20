package utfcatering.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import utfcatering.model.Employee;
import utfcatering.model.User;
import utfcatering.model.UserRepository;
import utfcatering.model.validation.RegistrationForm;
import utfcatering.model.validation.RegistrationFormExternal;

/**
 * controlls all Admin Views
 * @author Dennis
 *
 */
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	
	
	/**
	 * basic Admin View call
	 * @return basic Admin View
	 */
	@RequestMapping({"AdminIndex"})
    public String index() {
		return "AdminManageUser";
    }
	private final UserAccountManager userAccountManager;
	private final UserRepository userRepository;
	
	/**
	 * initializes userAccountManager and userRepository for further use
	 * @param userAccountManager holds all UserAccounts
	 */
	@Autowired
	public AdminController(UserAccountManager userAccountManager, UserRepository userRepository) {
		this.userAccountManager = userAccountManager;
		this.userRepository = userRepository;
	}
	
	/**
	 * calls an overview of functionalities
	 * @return AdminManageUser View
	 */
	@RequestMapping({"AdminManageUser"})
    public String manageUser() {
		return "AdminManageUser";
    }
	
	/**
	 * is called to add new {@link utfcatering.model.Employee}s 
	 * @param registrationForm holds all {@link utfcatering.model.Employee} details
	 * @param result holds the validation result
	 * @param modelMap holds variables for the next Views
	 * @return if validation failed "AdminAddEmployee", else "AdminManageUser"
	 */
	@RequestMapping("/AdminRegisterNew")
	public String registerNew(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm, BindingResult result, ModelMap modelMap) {

		if(result.hasErrors()) {
			return "AdminAddEmployee";
		}

		UserAccountIdentifier userAccountIdentifier = new UserAccountIdentifier(registrationForm.getUsername());
		System.out.println(userAccountIdentifier.getIdentifier());
		if (userAccountManager.contains(userAccountIdentifier)==true)
		{
			RegistrationForm r = new RegistrationForm();
			r.setAddress(registrationForm.getAddress());
			r.setBirthdate(registrationForm.getBirthdate());
			r.setFirstname(registrationForm.getFirstname());
			r.setLastname(registrationForm.getLastname());
			r.setPassword(registrationForm.getPassword());
			r.setRole(registrationForm.getRole());
			r.setSalary(registrationForm.getSalary());
			r.setUsername(registrationForm.getUsername());
			modelMap.addAttribute("registrationForm", r);
			modelMap.addAttribute("usernameexist", "Der angegeben Benutzername ist bereits vergeben.");
			return "AdminAddEmployee";
		}
		UserAccount userAccount = userAccountManager.create(registrationForm.getUsername(), registrationForm.getPassword(), new Role(registrationForm.getRole()));
		userAccount.setFirstname(registrationForm.getFirstname());
		userAccount.setLastname(registrationForm.getLastname()); 
		
		userAccountManager.save(userAccount);
		Employee employee;
		employee = new Employee(userAccount, registrationForm.getUsername(), registrationForm.getAddress(), registrationForm.getBirthdate(), Double.parseDouble(registrationForm.getSalary()));
		
		userRepository.save(employee);
		

		System.out.println(employee.getClass().getName());

		FillMapEmployees(modelMap);
		return "AdminEmployeesList";
	}
	
	/**
	 * calls a View to add {@link utfcatering.model.Employee}s
	 * @param modelMap holds a RegistrationForm to be filled in the next View
	 * @return "AdminAddEmployee"
	 */
	@RequestMapping("/AdminRegister")
	public String register(ModelMap modelMap) {

		modelMap.addAttribute("registrationForm", new RegistrationForm());
		modelMap.addAttribute("usernameexist", null);
		return "AdminAddEmployee";
	}
	
	/**
	 * called for an Overview of all {@link utfcatering.model.Employee}s
	 * @param modelMap holds a List of all {@link utfcatering.model.Employee}s
	 * @return "AdminEmployeesList"
	 */
	@RequestMapping("/AdminEmployees")
	public String employees(ModelMap modelMap) 
	{

		FillMapEmployees(modelMap);
		return "AdminEmployeesList";
	}
	
	/**
	 * called to edit details of an {@link utfcatering.model.Employee}
	 * @param modelMap holds variables for further Views
	 * @param ui identifier for the {@link utfcatering.model.Employee} to be edited
	 * @return "AdminEmployeeEdit"
	 */

	
	@RequestMapping(value = "/AdminEditEmployees/{user}", method = RequestMethod.GET)
	public String editemployees(ModelMap modelMap,@PathVariable User user) 
	{	
		Employee employee = (Employee) userRepository.findByUserAccount(user.getUserAccount());
		System.out.println(employee.getSalary().toString());
		RegistrationForm r = new RegistrationForm();
		r.setAddress(employee.getAddress());
		r.setBirthdate(employee.getBirthdate());
		r.setFirstname(employee.getUserAccount().getFirstname()); 
		r.setLastname(employee.getUserAccount().getLastname());
		r.setPassword(employee.getUserAccount().getPassword().toString());
		for (Role role : employee.getUserAccount().getRoles())
		{
			r.setRole(role.getName());
			break;
		}
		r.setSalary(employee.getSalary().toString());
		r.setUsername(employee.getUsername());
		modelMap.addAttribute("registrationForm", r);
		return "AdminEmployeeEdit";
	}
	
	/**
	 * @param modelMap holds an formular to be filled in the next View
	 * @param ui identifier of the {@link utfcatering.model.User} to be edited
	 * @return "AdminExternalEdit"
	 */
	@RequestMapping(value = "/AdminEditExternals/{user}", method = RequestMethod.GET)
	public String editexternals(ModelMap modelMap,@PathVariable User user) 
	{
		User client = userRepository.findByUserAccount(user.getUserAccount());
		
		RegistrationFormExternal r = new RegistrationFormExternal();
		r.setAddress(client.getAddress());
		r.setFirstname(client.getUserAccount().getFirstname());
		r.setLastname(client.getUserAccount().getLastname());
		r.setPassword(client.getUserAccount().getPassword().toString());
		r.setUsername(client.getUsername());
		modelMap.addAttribute("registrationForm", r);
		
		return "AdminExternalEdit";
	}
	
	/**
	 * called to save the Registration of an {@link utfcatering.model.Employee}
	 * @param empLocId id of the {@link utfcatering.model.Location} for the {@link utfcatering.model.Employee}
	 * @param registrationForm holds all details of the registration
	 * @param result validation result
	 * @param modelMap variables for further Views
	 */
	
	@RequestMapping("/AdminRegisterSave")
	public String registerSave(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm, BindingResult result, ModelMap modelMap ) {

		if(result.hasErrors()) {
			return "AdminEmployeeEdit";
		}
		
		UserAccountIdentifier userAccountIdentifier = new UserAccountIdentifier(registrationForm.getUsername());
		for(UserAccount ua : userAccountManager.findAll()){
			if(ua.getIdentifier().equals(userAccountIdentifier)==true){
				Employee u = (Employee) userRepository.findByUserAccount(ua);
				u.getUserAccount().setFirstname(registrationForm.getFirstname());
				u.getUserAccount().setLastname(registrationForm.getLastname());
				for (Role role : u.getUserAccount().getRoles())
					{
					u.getUserAccount().remove(role);
					}
				u.getUserAccount().add(new Role(registrationForm.getRole()));
				u.setAddress(registrationForm.getAddress());
				u.setBirthdate(registrationForm.getBirthdate());
				u.setSalary(Double.parseDouble(registrationForm.getSalary()));
				userRepository.save(u);
	
			}
				
		}
		FillMapEmployees(modelMap);
		
		return "AdminEmployeesList";
	}
	
	/**
	 * called to save the Registration of an external {@link utfcatering.model.User}
	 * @param registrationForm holds all the registration details
	 * @param result validation result
	 * @param modelMap variables for further Views
	 * @return "AdminExternalEdit" if the validation failed, else "AdminExternalUserList"
	 */
	@RequestMapping("/AdminRegisterExternalSave")
	public String registerExternalSave(@ModelAttribute("registrationForm") @Valid RegistrationFormExternal registrationForm, BindingResult result, ModelMap modelMap) {

		if(result.hasErrors()) {
			return "AdminExternalEdit";
		}

		UserAccountIdentifier userAccountIdentifier = new UserAccountIdentifier(registrationForm.getUsername());
		for(UserAccount ua : userAccountManager.findAll()){
			if(ua.getIdentifier().equals(userAccountIdentifier)==true){
				User u = userRepository.findByUserAccount(ua);
				u.getUserAccount().setFirstname(registrationForm.getFirstname());
				u.getUserAccount().setLastname(registrationForm.getLastname());
				u.setAddress(registrationForm.getAddress());
				userRepository.save(u);
	
			}
				
		}
		FillMapExternal(modelMap);
		
		return "AdminExternalUserList";
	}
	
	/**
	 * called to disable {@link utfcatering.model.Employee}s 
	 * @param modelMap List of all {@link utfcatering.model.Employee}s for the next View
	 * @return "AdminEmployeesList"
	 */
	
	@RequestMapping(value = "/AdminDisableEmployees/{user}", method = RequestMethod.GET)
	public String disableemployees(ModelMap modelMap, @PathVariable User user,  @LoggedIn Optional<UserAccount> userAccount) 
	{		
		Role adminRole = new Role("ROLE_ADMIN");
		if(userAccount.get().hasRole(adminRole)){
			int counter = 0;
			for(UserAccount ua : userAccountManager.findAll()){
				if(ua.hasRole(adminRole) && ua.isEnabled()){counter++;}
			}
			if(counter > 1){
				if(userAccount.get().equals(user.getUserAccount().getIdentifier())){
					userAccountManager.disable(user.getUserAccount().getIdentifier());
					userRepository.delete(userRepository.findByUserAccount(user.getUserAccount()));
					return "logout";
				}
				else{
					userAccountManager.disable(user.getUserAccount().getIdentifier());
				}
			}
		}
		else{
			userAccountManager.disable(user.getUserAccount().getIdentifier());
			userRepository.delete(userRepository.findByUserAccount(user.getUserAccount()));
		}
		
		FillMapEmployees(modelMap);
		return "AdminEmployeesList";
	}
	
	/**
	 * called to disable external {@link utfcatering.model.User}s 
	 * @param modelMap List of all external {@link utfcatering.model.User}s for the next View
	 * @return "AdminExternalUserList"
	 */
	@RequestMapping(value = "/AdminDisableExternal/{user}", method = RequestMethod.GET)
	public String disableexternal(ModelMap modelMap, @PathVariable User user) {
		userAccountManager.disable(user.getUserAccount().getIdentifier());
		userRepository.delete(userRepository.findByUserAccount(user.getUserAccount()));
		
		FillMapExternal(modelMap);
		return "AdminExternalUserList";
	}
	
	/**
	 * calls a View to add external {@link utfcatering.model.User}s 
	 * @param modelMap holds a formular to be filled in the next View
	 * @return "AdminAddExternal"
	 */
	@RequestMapping("/AdminRegisterExternal")
	public String registerExternal(ModelMap modelMap) {

		modelMap.addAttribute("registrationForm", new RegistrationFormExternal());
		modelMap.addAttribute("usernameexist", null);
		return "AdminAddExternal";
	}
	
	/**
	 * called after sending the external registration formular
	 * @param registrationForm holds all the details of the external {@link utfcatering.model.User}
	 * @param result validation result
	 * @param modelMap variables for further Views
	 * @return "AdminAddExternal" if validation failed, else "AdminManageUser"
	 */
	@RequestMapping("/AdminRegisterNewExternal")
	public String registerNewExternal(@ModelAttribute("registrationForm") @Valid RegistrationFormExternal registrationForm, BindingResult result, ModelMap modelMap) {

		if(result.hasErrors()) {
			return "AdminAddExternal";
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
			return "AdminAddExternal";
		}
		
		UserAccount userAccount = userAccountManager.create(registrationForm.getUsername(), registrationForm.getPassword(), new Role("ROLE_CLIENT"));
		userAccount.setFirstname(registrationForm.getFirstname());
		userAccount.setLastname(registrationForm.getLastname());
		
		userAccountManager.save(userAccount);
		User user;
			user = new User(userAccount, registrationForm.getUsername(), registrationForm.getAddress());
		
		userRepository.save(user);

		FillMapExternal(modelMap);
		return "AdminExternalUserList";
	}
	
	/**
	 * writes all  {@link utfcatering.model.Employee}s into the modelMap
	 * @param modelMap modelMap to hold all {@link utfcatering.model.Employee}s
	 */
	public void FillMapEmployees(ModelMap modelMap)
	{		
		List<User> list = new ArrayList<User>();
		Iterable<User> userList = userRepository.findAll();
		for (User user : userList)
		{
			for (Role role : user.getUserAccount().getRoles())
			{
				if (role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_EMPLOYEE"))
					list.add(user);
				break;
			}			
		}		
		modelMap.addAttribute("employeesList", list);
	}
	
	/**
	 * writes all external {@link utfcatering.model.User}s into the modelMap
	 * @param modelMap modelMap to hold all external {@link utfcatering.model.User}s
	 */
	public void FillMapExternal(ModelMap modelMap)
	{
		List<User> list = new ArrayList<User>();
		Iterable<User> userList = userRepository.findAll();
		for (User user : userList)
		{
			for (Role role : user.getUserAccount().getRoles())
			{
				if(user.getUserAccount().isEnabled()==true)
				if (role.getName().equals("ROLE_CLIENT"))
					list.add(user);
				break;
			}			
		}
		modelMap.addAttribute("externalList", list);
	}

	/**
	 * calls a overview View which displays all external {@link utfcatering.model.User}s
	 * @param modelMap modelMap to hold all the {@link utfcatering.model.User}
	 * @return "AdminExternalUserList"
	 */
	@RequestMapping("/AdminExternals")
	public String externals(ModelMap modelMap) 
	{
		FillMapExternal(modelMap);
		return "AdminExternalUserList";
	}
}