/**
 * 
 * Die Klasse representiert den Kalender, wo man die Bestellungen zum bestimmten Datum als "Map" speichert. 
 * Die Klasse ist nach Pattern "Singleton" gebaut.
 * 
 * @author Igor Misirev
 * @version   0.3
 * @since     28-12-2014
 * 
 */

package utfcatering.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class WebCalendar{

	
	private List<CalenderEntity> myCalender;
	 /**
	  * instance der Klasse Calendar
	  */
	private static WebCalendar instance;
	
	private WebCalendar() {
		this.myCalender = new ArrayList<CalenderEntity>();
	}
	/**
	 * gibt den statischen Objekt der Klasse Calendar zurück 
	 */
	public static WebCalendar getInstance () {
	    if (WebCalendar.instance == null) {
	    	WebCalendar.instance = new WebCalendar ();
	    }
	    return WebCalendar.instance;
	  }
	
	public boolean isFree (LocalDateTime date){
		
		if(date == null)
			return false;
		
		for(CalenderEntity ce : this.myCalender){
			if(!ce.getStartDate().isAfter(date) && !ce.getEndDate().isBefore(date)){
				return false;
			}
		}
		
		return true;
	}	
	
	
	
	/**
	 * Prüft ob das Datum in Map enthaltet ist
	 * 
	 * @param  date  Datum, das geprüft werden soll
	 * @return true, wenn das Datu in Map enthalten ist und false sonst
	 */
	public boolean canOrderBeAdded (LocalDateTime startDate, LocalDateTime endDate){
		
		if(startDate == null || endDate == null)
			return false;
		
		
		
		for(CalenderEntity ce : this.myCalender){
			if(startDate.toLocalDate().isEqual(ce.getStartDate().toLocalDate())){
				if(  !(startDate.toLocalTime().isAfter(ce.getEndDate().toLocalTime()) ||
						endDate.toLocalTime().isBefore(ce.getStartDate().toLocalTime())) ){
					return false;
				}
			}
		}
		
		
		return true;
	}
	/**
	 * checks if the order is in the calendar
	 * 
	 * @param  order  that is to check
	 * @return true, if the order is in the calendar, else false
	 */
	public boolean containsOrder (Order order){
		
		if(order == null)
			return false;
		
		for(CalenderEntity ce : this.myCalender){
			if(ce.getOrder().equals(order)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns the amount of orders for a date
	 * @return count
	 */ 
	
	public int getCountOfOrdersByDate (LocalDateTime date){
		
		if(date == null)
			return -1;
		int count = 0;
		for(CalenderEntity ce : this.myCalender){
			if(ce.getStartDate().getYear() == date.getYear() &&
					ce.getStartDate().getMonthValue() == date.getMonthValue() &&
					ce.getStartDate().getDayOfMonth() == date.getDayOfMonth())
				count++;
		}
		return count;
	}
	
	
	/**
	 * adds a order to a date,
	 * only four orders for one date are allowed
	 * 
	 * @param  date  for the order
	 * @param  order that is to save
	 * @return true if the order is added, elso false
	 */
	public boolean addOrderWithDate(LocalDateTime startDate, LocalDateTime endDate, CateringOrder order) {
		
		if (startDate == null || order == null || endDate == null){
			return false;
		}
		if(getCountOfOrdersByDate(startDate) >= 4 || !canOrderBeAdded(startDate, endDate)){
			return false;
		}
		
		return this.myCalender.add(new CalenderEntity(startDate, endDate, order));
	}
	
	/**
	 * @param  date  das Datum
	 * @return res is a list of all order for a date, or null
	 */
	public Optional<CateringOrder> getOrderByTime(LocalDateTime date){
		
		if(date == null){
			return null;
		}
		
		CateringOrder order = null;
		
		for(CalenderEntity ce : this.myCalender){
			if(!ce.getStartDate().isAfter(date) && !ce.getEndDate().isBefore(date)){
				order = ce.getOrder();
				break;
			}
		}
		
		Optional<CateringOrder> res = Optional.ofNullable(order);
		if(order != null)
			res = Optional.of(order);
		
		return res;
	}
	
	/**
	 * checks if startdate equal to another
	 * @return return true if it is not equal, else false
	 */
	
public boolean isStartDate(LocalDateTime startDate){
		
		if(startDate == null){
			return false;
		}
		for(CalenderEntity ce : this.myCalender){
			if(ce.getStartDate().compareTo(startDate) == 0){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * list of startDate 
	 * 
	 * @param  order 
	 * @return list of dates, or null if the order not exists
	 */
	public List<LocalDateTime> getDateByOrder(Order order){

		if(!containsOrder(order)){
			return null;
		}
		
		List<LocalDateTime> list = new ArrayList<LocalDateTime>();
		
		for(CalenderEntity ce : this.myCalender){
			if(ce.getOrder().equals(order)){
				list.add(ce.getStartDate());
			}
		}
		return list;
	}
	
	/**
	 * removes all orders for the chosen date
	 * 
	 * @param  date is the chosen date
	 * @return list of all orders for the chosen date or null
	 */
	public List<CateringOrder> removeAllOrdersByDate (LocalDateTime date){
		
		List<CateringOrder> list = new ArrayList<CateringOrder>();
		
		if(date == null){
			return list;
		}
		
		for(int i = 0; i < this.myCalender.size(); i++){
			CalenderEntity ce = this.myCalender.get(i);
			if(ce.getStartDate().getYear() == date.getYear() &&
					ce.getStartDate().getMonthValue() == date.getMonthValue() &&
					ce.getStartDate().getDayOfMonth() == date.getDayOfMonth()){
				list.add(ce.getOrder());
				this.myCalender.remove(ce);
				i = -1;
			}
		}
		
		return list;
	}

	/**
	 * removes a concrete order of a chosen date
	 * 
	 * @param  order to remove
	 * @param  date is the chosen date
	 * @return true if the remove was successful, else false
	 */
	public boolean removeOrderByDate(LocalDateTime startDate){

		if(startDate == null)
			return false;

		
		for(CalenderEntity ce : this.myCalender){
			if(ce.getStartDate().compareTo(startDate) == 0){
				return this.myCalender.remove(ce);
			}
		}
		return false;
	}
	

	
	private class CalenderEntity{
		
		private LocalDateTime startDate;
		
		private LocalDateTime endDate;
		
		private CateringOrder order;

		public CalenderEntity(LocalDateTime startDate, LocalDateTime endDate, CateringOrder order) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
			this.order = order;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}

		public CateringOrder getOrder() {
			return order;
		}
	}
	
}