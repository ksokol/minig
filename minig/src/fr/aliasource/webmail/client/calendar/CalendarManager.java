package fr.aliasource.webmail.client.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CalendarManager {
	
	private HashMap<Integer, HashMap<Integer, ArrayList<CalendarEvent>>> dayCol;
	private int toRedraw;
	
	public CalendarManager() {
		dayCol = new HashMap<Integer, HashMap<Integer, ArrayList<CalendarEvent>>>();
	}
	
	public void register(CalendarEvent e) {
		toRedraw = e.getDay();
		if (!dayCol.containsKey(toRedraw)) {
			dayCol.put(toRedraw, new HashMap<Integer, ArrayList<CalendarEvent>>());
		}
		int start = e.getHour();
		int end = start + e.getDuration();
		for(int i=start;i<end;i++) {
			if (!dayCol.get(toRedraw).containsKey(i)){
				dayCol.get(toRedraw).put(i, new ArrayList<CalendarEvent>());
			}
			dayCol.get(toRedraw).get(i).add(e);
		}
	}
	
	public void unregister() {
	}
	
	// TODO
	public void redraw() {
		HashMap<Integer, ArrayList<CalendarEvent>> cells = dayCol.get(toRedraw);
		ArrayList<CalendarEvent> evts;
		CalendarEvent evt;
		for (Iterator<Integer> i = cells.keySet().iterator() ; i.hasNext() ; ){
			evts = cells.get(i.next());
			for(int j=0;j<evts.size();j++) {
				evt = evts.get(j);
				evt.draw(evts.size());
			}
			
		}
	}
	
}
