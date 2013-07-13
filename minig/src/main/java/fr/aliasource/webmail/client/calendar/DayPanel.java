package fr.aliasource.webmail.client.calendar;

import com.google.gwt.user.client.ui.SimplePanel;

public class DayPanel extends SimplePanel {

	public DayPanel (int index) {
		getElement().setAttribute("id", "day"+index);
		setHeight("1008px"); // 20*48 + 48
		setStyleName("dayCol");
	}
	
}
