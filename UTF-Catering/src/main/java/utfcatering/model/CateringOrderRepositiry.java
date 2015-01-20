package utfcatering.model;

/**

 * 
 * A Repository of all {@link utfcatering.model.CateringOrder}s
 * 
 */

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

public interface CateringOrderRepositiry extends CrudRepository<CateringOrder, Long>{
	
	Iterable<CateringOrder> findByOrderStatus(OrderStatus status);
	CateringOrder findByOrderIdentifier(OrderIdentifier id);
	Iterable<CateringOrder> findByUserAccount(UserAccount status);
}
