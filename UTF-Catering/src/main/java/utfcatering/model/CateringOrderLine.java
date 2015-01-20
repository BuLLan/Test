package utfcatering.model;

import java.util.regex.Pattern;

import org.joda.money.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.OrderLine;
import org.salespointframework.quantity.Quantity;

public class CateringOrderLine extends OrderLine{
	
	private Money priceForMwSt;
	
	private int neededEmp;

	/**
	 * Constructor
	 * @param product is the article/package that is added to the cart
	 * @param amount is the amount of the added product
	 */
	
	public CateringOrderLine(Product product, Quantity quantity) {
		super(product, quantity);
		
		int cnEmp = 0;
		
		if( !Pattern.matches(".*Article", product.getClass().getName())){
			cnEmp += (int)Math.ceil(getQuantity().getAmount().intValue()/10)+1;
		}
		
		this.neededEmp = cnEmp;
	}
	
	/**
	 * returns the price inclusive VAT of the product
	 * @return priceForMwSt
	 */

	public Money getPriceForMwSt() {
		return priceForMwSt;
	}
	
	public int getNeededEmp() {
		return neededEmp;
	}
	
	

}
