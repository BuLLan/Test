/** 
 *
 * @author    Igor Misirev
 * @version   0.5
 * @since     28-12-2014
 * 
 */

package utfcatering.controller;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import utfcatering.model.CateringOrder;
import utfcatering.model.WebCalendar;


@Controller
public class WebCalendarController {
	
	private final MessageSourceAccessor messageSourceAccessor;
	private WebCalendar calendar = WebCalendar.getInstance();
	
	@Autowired
	public WebCalendarController(MessageSource messageSource) {

		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
		
	}	
	
	
	/**
	 * shows the previous week 
	 * @param modelMap session-attribute
	 * @param date1 is the date of the shown Monday
	 * @param aktuellesDatum is the current date
	 * @return "calendar"
	 */
	@RequestMapping(value = "/previousWeek/{pid}")
    public String previousWeek(Model modelMap, @PathVariable("pid") String date1) {
		if(date1 == null){
			return calendar(modelMap);
		}
		if (date1.isEmpty()){
			return calendar(modelMap);
		}
		LocalDate aktuellesDatum;
		try{
		aktuellesDatum = LocalDate.parse(date1).minusWeeks(1);
		}catch (DateTimeParseException ex){
			return calendar(modelMap);
		}
		int dayOfWeek = aktuellesDatum.getDayOfWeek().getValue();
		if(dayOfWeek != 1){
			return calendar(modelMap);
		}
		modelMap = this.fillTheModel(modelMap, aktuellesDatum);
		
		return "calendar";
    }
	
	/**
	 * shows the next week 
	 * @param modelMap session-attribute
	 * @param date1 is the date of the shown Monday
	 * @param aktuellesDatum is the current date
	 * @return "calendar"
	 */
	@RequestMapping(value = "/nextWeek/{pid}")
    public String nextWeek(Model modelMap, @PathVariable("pid") String date1) {
		if(date1 == null){
			return calendar(modelMap);
		}
		if (date1.isEmpty()){
			return calendar(modelMap);
		}
		LocalDate aktuellesDatum;
		try{
		aktuellesDatum = LocalDate.parse(date1).plusWeeks(1);
		}catch (DateTimeParseException ex){
			return calendar(modelMap);
		}
		int dayOfWeek = aktuellesDatum.getDayOfWeek().getValue();
		if(dayOfWeek != 1){
			return calendar(modelMap);
		}
		modelMap = this.fillTheModel(modelMap, aktuellesDatum);
		
		return "calendar";
    }
	
	/**
	 * fills the model with data to show the calendar
	 * 
	 * @param model session-attribute
	 * @param dayMo date of monday 
	 * @param daySo date of sunday
	 * @param weelDate is the list to fill
	 * @return model filled with Data
	 */
	private Model fillTheModel(Model model, LocalDate dayMo){
		
		List<Date> weekDates = new ArrayList<Date>();
		
		LocalDate localDatePlus = dayMo;
		for(int aktuell = localDatePlus.getDayOfWeek().getValue(); aktuell <= 7; aktuell++){
			
			//Convert java.time.LocalDate to java.util.Date
			Instant instant = localDatePlus.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(instant);
			
			//Liste füllen mit Date
			weekDates.add(aktuell-1, date);
			
			localDatePlus = localDatePlus.plusDays(1);
		}
		//----------------------------------------------
		LocalDate daySo = dayMo.plusDays(6);
		int year = daySo.getYear();
		
		//----------------------------------------------
		
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int weekNumber = dayMo.get(weekFields.weekOfWeekBasedYear());
		
		LocalDate firstDayOfWeek = dayMo;
		
		
		Instant instant = Instant.ofEpochMilli(weekDates.get(0).getTime());
		LocalDate datumMo = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
		LocalDateTime time = datumMo.atTime(0, 0);
		

		List<List<TableEntity>> table = new LinkedList<List<TableEntity>>();
		
		for(int i = 0; i < 24; i++){//time
			List<TableEntity> entity = new LinkedList<TableEntity>();
			for(int j = 0; j < 7; j++){//dayOfWeek
				time = datumMo.plusDays(j).atTime(i, 0);
				if(calendar.isFree(time))
					entity.add(j, new TableEntity("",messageSourceAccessor.getMessage("class.notpainted")));//class free
				else{
					if(calendar.getOrderByTime(time).isPresent()){
						CateringOrder order = calendar.getOrderByTime(time).get();
						String name = "";
						if(calendar.isStartDate(time))
							name = order.getName();
						if(order.isConfirmed())
							entity.add(j, new TableEntity(name,messageSourceAccessor.getMessage("class.painted_green")));//class 1-4 Orders
						else
							entity.add(j, new TableEntity(name,messageSourceAccessor.getMessage("class.painted")));//class 1-4 Orders
					}
				}
			}
			table.add(i, entity); 
		}
		
		
		model.addAttribute("year", year);
		model.addAttribute("firstDayOfWeek", firstDayOfWeek);
		model.addAttribute("weekNumber", weekNumber);
		model.addAttribute("week", weekDates);
		model.addAttribute("table", table);
		model.addAttribute("kalenderS", messageSourceAccessor.getMessage("class.selected"));
		
		return model;
	}
	
	
	
	
	/**
	 * called to show the current week as calendar
	 * 
	 * @param modelMap session-attribute
	 * @param aktuellesDatum 
	 * @return "calendar"
	 */
	@RequestMapping({"calendar"})
    public String calendar(Model modelMap) {
		//-----------------------------------------------
		//List<Date> weekDates = new ArrayList<Date>();
		LocalDate aktuellesDatum = LocalDate.now();
		
		for(int aktuell = aktuellesDatum.getDayOfWeek().getValue(); aktuell >1; aktuell--){
			aktuellesDatum = aktuellesDatum.minusDays(1);
		}
		
		modelMap = this.fillTheModel(modelMap, aktuellesDatum);
		
		return "calendar";
    }
	
	/**
	 * this class helps to fill the model correctly
	 * 
	 * @author Igor
	 *
	 */
	private final class TableEntity{
		
		/**
		 * attribute to change the color of calendar-box
		 */
		private String message;
		
		/**
		 * text that is shown in the box
		 */
		private String discription = "";

		public TableEntity(String message, String discription) {
			super();
			this.message = message;
			this.discription = discription;
		}

		/**
		 * @return message the attribute to change color
		 */
		
		public String getMessage() {
			return message;
		}

		/**
		 * sets the attribute to change color
		 */
		
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * @return discription the text in the box
		 */
		
		public String getDiscription() {
			return discription;
		}

		/**
		 * sets the text in the box
		 */
		
		public void setDiscription(String discription) {
			this.discription = discription;
		}
	}
	
}