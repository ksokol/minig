/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package fr.aliasource.webmail.client.calendar;

import java.util.Date;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Calendar
 * 
 * @author david
 * 
 */
public class CalendarPanel extends VerticalPanel {
	
	private Toolbar toolbar;
	private DeckPanel container;
	private TabPanel tp;
	private int selectedTab;
	private Date currentDate;
	private CalendarManager cm;
	
	public static int TAB_DAY = 0;
	public static int TAB_WEEK = 1;
	public static int TAB_MONTH = 2;

	public CalendarPanel() {
		
		currentDate = new Date();
		cm = new CalendarManager();
		toolbar = new Toolbar(this, cm);
		add(toolbar);
		
		addStyleName("whiteBackground");
		setWidth("100%");
		setHeight(Window.getClientHeight() - 130 + "px");
				
		tp =  new TabPanel();
		tp.add(new Label("day"), "day");
		tp.add(new Multiday(7), "week");
		tp.add(new Label("month"), "month");
		setTab(TAB_WEEK);
		selectedTab = TAB_WEEK;
		toolbar.setDisplayedDate();
		
		container = tp.getDeckPanel();
		container.setStyleName("whiteBackground");
		add(container);
	}

	public void setTab(int tab) {
		tp.selectTab(tab);
		selectedTab = tab;
	}
	
	public int getSeletedTab() {
		return selectedTab;
	}
	
	public Date getCurrentDate() {
		return currentDate;
	}
}
