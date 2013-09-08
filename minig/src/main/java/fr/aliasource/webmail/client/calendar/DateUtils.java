package fr.aliasource.webmail.client.calendar;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils {

	private static int DAY_MS = 60*60*24*1000;

	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfWeek(Date d) {
		Date ret = new Date(d.getTime() - ((6-d.getDay()%6)) * DAY_MS);
		return ret;
	}
	
	
	@SuppressWarnings("deprecation")
	public static Date getLastDayOfWeek(Date d) {
		Date ret = new Date(d.getTime() + ((d.getDay()%6)) * DAY_MS);
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOMonth(Date d) {
      Date fdow = (Date) d.clone();
      fdow.setDate(1);
      return fdow;
	}
	
	public static String getFormattedDate(Date d) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("EEE, MMM dd");
		return dtf.format(d);
	}
	
	public static String getFormattedDateTime() {
		return "";
	}
	
}
