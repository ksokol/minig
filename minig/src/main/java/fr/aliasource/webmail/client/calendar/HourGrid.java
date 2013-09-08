package fr.aliasource.webmail.client.calendar;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class HourGrid extends AbsolutePanel {
	
	public HourGrid(boolean displayHour) {
		getElement().setAttribute("style", "position:absolute;");
		SimplePanel ap;
		for(int i=0;i<48;i++) {
			ap = new SimplePanel();
			if ((i%2)==0) {
				ap.setStyleName("hour");
				if (displayHour) {
					ap.add(new Label(i/2+"h"));
				}
			} else {
				ap.setStyleName("halfHour");
			}
			ap.setHeight("20px");
			if (displayHour) {
				ap.getElement().setAttribute("id", "time_"+1800*i);
			}
			add(ap);
		}
		if (displayHour) {
			setWidth("50px");
		} else {
			setWidth("100%");
		}
	}
}