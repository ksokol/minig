package fr.aliasource.webmail.client.calendar;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class Multiday extends AbsolutePanel {

	final SimplePanel body;
	HourGrid grid;
	
	public Multiday (int days) {
		int cols = days + 1; // hour label
		
		getElement().setAttribute("id", "gridContainer");
		
		SimplePanel header = new SimplePanel();
		header.setStyleName("calendarHeader");
		body = new SimplePanel();
		body.setStyleName("calendarBody");
		
		Grid top = new Grid(2, cols);
		top.setCellPadding(0);
		top.setCellSpacing(0);
		top.setStyleName("calendarTable");
		top.setWidget(0, 0, new Label(""));
		top.getColumnFormatter().setWidth(0, "50px");
		
		FlexTable ft = new FlexTable();
		ft.setCellPadding(0);
		ft.setCellSpacing(0);
		ft.setStyleName("calendarTable");
		ft.setWidget(0, 0, new HourGrid(true));
		grid = new HourGrid(false);
		ft.setWidget(0, 1, grid);
		ft.getColumnFormatter().setWidth(0, "50px");
		SimplePanel sp;
		Label lbl;
		for(int i=0;i<days;i++) {
			lbl = new Label("day"+i);
			lbl.setStyleName("dayLabel");
			top.setWidget(0, i+1, lbl);
			sp = new SimplePanel();
			sp.setHeight("20px");
			sp.setStyleName("allDayCol");
			top.setWidget(1, i+1, sp);
			sp = new DayPanel(i);
			ft.setWidget(1, i+1, sp);
		}
		
		header.add(top);
		body.add(ft);
		
		add(header);
		add(body);
		
		resize(Window.getClientHeight());
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize(event.getHeight());
			}
		});
		
	}
	
	private void resize(int height) {
		body.setHeight(height-200+"px");
	}
	
}