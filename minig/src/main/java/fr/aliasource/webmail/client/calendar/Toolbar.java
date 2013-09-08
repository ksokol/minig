package fr.aliasource.webmail.client.calendar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class Toolbar extends HorizontalPanel {
	
	private Label displayedDate;
	
	public Toolbar (final CalendarPanel c, final CalendarManager cm) {
		Button prev = new Button("<<");
		Button day = new Button("day");
		Button week = new Button("week");		
		Button month = new Button("month");		
		Button next = new Button(">>");
		
		day.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				c.setTab(CalendarPanel.TAB_DAY);
				setDisplayedDate();
			}
		});
		
		week.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				c.setTab(CalendarPanel.TAB_WEEK);
				setDisplayedDate();
			}
		});
		
		month.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				c.setTab(CalendarPanel.TAB_MONTH);
				setDisplayedDate();
			}
		});
		
		add(prev);
		add(next);

		add(day);
		add(week);
		add(month);
		
		displayedDate = new Label();
		add(displayedDate);
		setDisplayedDate();

		//
		Button evt = new Button("Add dummy event");
		evt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				int day = (int) Math.floor(Math.random()*7);
				int hour = (int) Math.floor(Math.random()*47);
				int duration = (int) Math.max(1, Math.floor(Math.random()*10));
				if (duration+hour > 47) duration = 48-hour;
				CalendarEvent evt = new CalendarEvent(day, hour, duration,  "dummy");
				cm.register(evt);
				cm.redraw();
			}
		});
		add(evt);

	}
	
	public void setDisplayedDate() {
		displayedDate.setText(" begin - end date");
	}

}
