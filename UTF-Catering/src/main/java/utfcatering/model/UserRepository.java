package utfcatering.model;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository of all {@link utfcatering.model.User}s
 * @author Dennis
 *
 */
public interface UserRepository extends CrudRepository<User, Long> {
	public User findByUserAccount(UserAccount userAccount);  
	public User findByUserAccount(String string);
	public Iterable <User> findAll();
	
}
