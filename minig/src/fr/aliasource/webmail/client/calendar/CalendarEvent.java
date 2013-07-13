package fr.aliasource.webmail.client.calendar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CalendarEvent extends SimplePanel {
	
	private int day;
	private int hour;
	private int duration;
	private String title;
	
	public CalendarEvent(int day, int hour, int duration, String title) {
		add(new Label(title));
		setStyleName("event");
		this.duration = duration;
		this.hour = hour;
		this.day = day;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getDay() {
		return day;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void draw(int size) {
		Element e = DOM.getElementById("day"+this.day);
		this.getElement().setAttribute("style", "top:"+this.hour*21+"px;height:"+this.duration*21+"px;width:"+100/size+"%;");
		e.appendChild(this.getElement());
		
	}
}
