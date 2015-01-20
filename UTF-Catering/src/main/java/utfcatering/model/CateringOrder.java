package utfcatering.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;

@Entity
public class CateringOrder extends Order{

	private int neededHours;
	
	private Money employeePrice;
	
	private int wert;
	
	private String adresse;
	
	private String name;
	
	private LocalDateTime start, end;
	
	private boolean confirmed;
	
	private Money priceWithMwSt;
	
	@Deprecated
	protected CateringOrder(){}

	/**
	 * Constructor
	 * @param userAccount is the userAccount that orders
	 * @param adresse is the address to supply
	 * @param name is the name of the event
	 * @param start is the starttime of the event
	 * @param end is the endtime of the event
	 * @param confirmed is the state of an order
	 */
	
	public CateringOrder(UserAccount userAccount, String adresse, String name, 
			LocalDateTime start, LocalDateTime end) {
		super(userAccount, Cash.CASH);
		this.name = name;
		this.adresse = adresse;
		this.start = start;
		this.end = end;
		this.confirmed = false;
		this.priceWithMwSt = this.calculateMwSt();
	}
	
	/**
	 * 
	 * @return priceForMwSt, the VAT-price
	 */
	
	public Money getPriceWithMwSt() {
		return priceWithMwSt;
	}
	
	public Money getEmployeePrice() {
		return employeePrice;
	}
	
	public int getNeededHours() {
		return neededHours;
	}
	
	
	public int getWert() {
		return wert;
	}
	
	/**
	 * returns the state of order
	 * @return confirmed
	 */

	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * sets the state of order
	 */
	
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * returns the start of an event
	 * @return start
	 */
	
	public LocalDateTime getStart() {
		return start;
	}
	
	/**
	 * returns the end of an event
	 * @return end
	 */

	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * returns the name of an event
	 * @return name
	 */
	
	public String getName() {
		return name;
	}
	
	/**
	 * sets name of an event
	 */

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the address to supply
	 * @return adresse
	 */
	
	public String getAdresse() {
		return adresse;
	}
	
	/**
	 * sets the address to supply
	 */

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	
	/**
	 * calculates the VAT
	 * @return res
	 */
	
	public Money calculateMwSt(){
		Money res = Money.of(CurrencyUnit.EUR, 0.0);
		
		for(OrderLine entry : this.getOrderLines()){
			//res = res.plus(((CateringOrderLine)entry).getPriceForMwSt());
			res = res.plus(entry.getPrice());
		}
		double pr = res.getAmount().doubleValue();
		pr += pr*19/100;
		pr *= 100;
		pr = Math.ceil(pr);
		pr /= 100;
		this.priceWithMwSt = Money.of(CurrencyUnit.EUR, pr);
		return priceWithMwSt;
	}
	
	public Money calculateEmployeePrice(){
		Money kosten = Money.of(CurrencyUnit.EUR, 10.0);
		wert = 0;

		this.neededHours = end.getHour()-start.getHour()+1;
		kosten = kosten.multipliedBy(neededHours);
		
		
		for(OrderLine entry : this.getOrderLines()){
			wert += (((CateringOrderLine)entry).getNeededEmp());
		}
		
		if (wert == 0){
			wert += 1;
		}
		kosten = kosten.multipliedBy(wert);
		
		
		
		this.employeePrice = kosten;
		return kosten;
	}
}
