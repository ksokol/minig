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

package fr.aliasource.webmail.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.addressbook.AddressBook;
import fr.aliasource.webmail.client.calendar.CalendarPanel;
import fr.aliasource.webmail.client.composer.MailComposer;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.rpc.UseCachedData;
import fr.aliasource.webmail.client.settings.SettingsPanel;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Webmail main ui
 * 
 * the main tab panel, the folders list and search field are here
 * 
 * @author tom
 * 
 */
public class View extends DockPanel implements IFolderSelectionListener {

	private TabPanel tp;
	private ConversationPanel conversationPanel;
	private MailComposer composer;
	private AddressBook bookPanel;
	private SettingsPanel settingsPanel;
	private CalendarPanel calendarPanel;
	private AjaxCall caller;
	private String login;
	private String domain;
	private Heading heading;
	private Toolbar toolbar;
	private HorizontalPanel statusPanel;
	private SideBar sidebar;
	private String query;
	private Folder currentFolder;
	private int currentTab;
	private Map<Folder, Integer> foldersUnreadCount;
	private WebmailDragController dragController;
	private ISpinner spinner;

	public static final int CONVERSATIONS = 0;
	public static final int COMPOSER = 1;
	public static final int ADDRESSBOOK = 2;
	public static final int SETTINGS = 3;
	public static final int CALENDAR = 4;
	public static final int CREATE_EVENT = 5;

	/**
	 * Create a new webmail panel.
	 * 
	 * @param caller
	 * @param settings
	 * @param password
	 */
	public View(Map<String, String> settings, boolean inOBM) {
		this.currentTab = -1;
		this.login = settings.get(GetSettings.CURRENT_LOGIN);
		if (login == null || login.length() == 0) {
			login = "john.doe";
		}
		this.domain = settings.get(GetSettings.CURRENT_DOMAIN);

		dragController = new WebmailDragController();

		add(constructHeadingAndToolbar(inOBM), DockPanel.NORTH);
		add(createTabPanel(), DockPanel.CENTER);

		statusPanel = new HorizontalPanel();
		statusPanel.setStyleName("statusPanel");
		add(statusPanel, DockPanel.NORTH);
		setCellHeight(statusPanel, "1.4em");

		sidebar = new SideBar(this);
		add(sidebar, DockPanel.WEST);
		setCellWidth(tp.getDeckPanel(), "100%");

		callListConversations(new Folder("inbox", I18N.strings.inbox()), 1);
	}

	/**
	 * called by {@link WebmailController}
	 */
	public void startTimers() {
		WebmailController.get().getSelector().startUnreadTimer();
		WebmailController.get().getSelector().refreshUnreadCounts();

		foldersUnreadCount = new HashMap<Folder, Integer>();

		// Event listener
		sinkEvents(Event.ONCLICK);
	}

	public String displayName(Folder f) {
		return displayName(f.getName());
	}

	public String displayName(String fName) {
		return WebmailController.get().displayName(fName);
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			Element el = event.getEventTarget().cast();
			if (el != null) {
				if ("a".equalsIgnoreCase(el.getTagName())) {
					String href = el.getAttribute("href");
					if (href.startsWith("mailto:")) {
						selectTab(COMPOSER);
						composer.mailto(href.replace("mailto:", ""));
					}
				}
			}
			break;
		}
	}

	private Widget createTabPanel() {
		tp = new TabPanel();
		conversationPanel = new ConversationPanel(this);
		tp.add(conversationPanel, "Conversations");

		composer = new MailComposer(View.this);
		tp.add(composer, "Mail Composer");

		bookPanel = new AddressBook(View.this);
		tp.add(bookPanel, "Address Book");

		settingsPanel = new SettingsPanel(View.this);
		tp.add(settingsPanel, "Settings");
		
		calendarPanel = new CalendarPanel();
		tp.add(calendarPanel, "Calendar");

		tp.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				currentTab = event.getSelectedItem();
				if (currentTab == COMPOSER) {
					setWindowTitle(I18N.strings.compose());
				} else if (currentTab == ADDRESSBOOK) {
					setWindowTitle(I18N.strings.contacts());
				} else if (currentTab == SETTINGS) {
					setWindowTitle(I18N.strings.settings());
				} else if (currentTab == CALENDAR) {
					setWindowTitle("Calendar");
				}
			}
		});

		selectTab(0);
		DeckPanel ret = tp.getDeckPanel();
		return ret;
	}

	private Widget constructHeadingAndToolbar(boolean inObm) {
		DockPanel cf = new DockPanel();

		if (!inObm) {
			heading = new Heading(this);
			this.spinner = heading;
			cf.add(heading, DockPanel.NORTH);
		} else {
			this.spinner = new OBMSpinner();
		}

		toolbar = new Toolbar(this);
		cf.add(toolbar, DockPanel.NORTH);

		cf.setWidth("100%");
		return cf;
	}

	public void fetchConversations(String folderName, int page) {
		GWT.log("fetch conv " + folderName + " page " + page, null);
		if (folderName.startsWith("search:")) {
			callSearch(folderName.substring("search:".length()), page);
		} else {
			callListConversations(new Folder(folderName), page);
		}
	}

	private void callListConversations(final Folder ff, int page) {
		getSpinner().startSpinning();

		GWT
				.log("webmail ajaxCall for folder " + ff.getName() + " page "
						+ page, null);
		AjaxCall.listConversations.list(ff, page,
				ConversationListPanel.PAGE_LENGTH, getListConvCallback(page));
	}

	private void callSearch(final String query, int page) {
		log("callSearch " + query);
		getSpinner().startSpinning();

		AjaxCall.search.search(query, page, ConversationListPanel.PAGE_LENGTH,
				getListConvCallback(page));
	}

	private AsyncCallback<ConversationList> getListConvCallback(final int page) {
		return new AsyncCallback<ConversationList>() {
			public void onSuccess(ConversationList convs) {
				getSpinner().stopSpinning();
				if (convs != null) {
					log("getList call successfull : found "
							+ convs.getFullLength() + " conversations.");
					conversationPanel.showConversations(convs, page);
				} else {
					log("problem: Received null conversationsList");
				}
			}

			public void onFailure(Throwable caught) {
				getSpinner().stopSpinning();
				if (UseCachedData.MSG.equals(caught.getMessage())) {
					GWT.log("no need to refresh, backend said nothing changed",
							null);
				} else {
					GWT.log("failure, msg: " + caught.getMessage(), caught);
				}
			}
		};
	}

	public void log(String s) {
		GWT.log(s, null);
	}

	public void log(String s, Throwable t) {
		GWT.log(s, t);
	}

	public AjaxCall getCaller() {
		return caller;
	}

	public String getUserName() {
		StringBuilder builder = new StringBuilder();
		builder.append(login);
		if (domain != null && domain.length() > 0) {
			builder.append("@");
			builder.append(domain);
		}
		return builder.toString();
	}

	public ISpinner getSpinner() {
		return spinner;
	}

	public void selectTab(int tab) {
		tp.selectTab(tab);
	}

	/**
	 * @return the toolbar widget holding the search form & the folder selector
	 */
	public Toolbar getToolbar() {
		return toolbar;
	}

	/**
	 * Redirect with js to url
	 * 
	 * @param url
	 */
	private native void redirect(String url) /*-{
												$wnd.location = url;
												}-*/;

	/**
	 * Executes the logout procedure (remove wandering timers, shows login ui or
	 * redirect to it)
	 */
	public void logout() {
		WebmailController.get().getSelector().stopUnreadTimer();
		conversationPanel.clearTimers();
		RootPanel rp = RootPanel.get("webmail_root");
		if ("true".equals(WebmailController.get().getSetting(
				GetSettings.AJAX_LOGIN))) {
			rp.clear();
			rp.add(new Label("bye bye"));
		} else {
			String logoutUrl = WebmailController.get().getSetting(
					GetSettings.LOGOUT_URL);
			if (logoutUrl == null) {
				logoutUrl = "http://www.obm.org/";
			}
			redirect(logoutUrl);
		}
	}

	/**
	 * Show a simple text notification to the user for 3 seconds
	 * 
	 * @param s
	 */
	public void notifyUser(String s) {
		GWT.log("notifyUser(" + s + ")", null);
		HorizontalPanel w = new HorizontalPanel();
		w.add(new Label(s));
		w.setSpacing(3);
		notifyUser(w, 3);
	}

	/**
	 * Pop a notification widget for the given time
	 * 
	 * @param w
	 *            the widget shown to the user as a notification
	 * @param seconds
	 *            how long the widget will remain visible
	 */
	public void notifyUser(final Widget w, int seconds) {
		Timer t = new Timer() {
			public void run() {
				statusPanel.remove(w);
			}
		};
		statusPanel.clear();
		w.setStyleName("notificationMessage");
		statusPanel.add(w);
		statusPanel.setCellHorizontalAlignment(w, VerticalPanel.ALIGN_CENTER);
		t.schedule(seconds * 1000);
	}

	/**
	 * Force the given notification to disappear
	 * 
	 * @param w
	 */
	public void clearNotification(Widget w) {
		statusPanel.remove(w);
	}

	/**
	 * Returns the tab panel. Users are supposed to use this to add a listener
	 * on the tab panel
	 * 
	 * @return
	 */
	public TabPanel getTabPanel() {
		return tp;
	}

	public void showConversation(Folder folder, ConversationId conversationId, int page) {
		selectTab(CONVERSATIONS);
		conversationPanel.showConversation(folder, conversationId, page);
	}

	public void showComposer(Folder folder, ConversationId conversationId) {
		conversationPanel.showComposer(folder, conversationId);
	}

	public void folderSelected(Folder f) {
		selectTab(CONVERSATIONS);
		fetchConversations(f.getName(), 1);
		setWindowTitle(f);
		currentFolder = f;
	}

	public void foldersChanged(Folder[] folders) {
	}

	public void unreadCountChanged(CloudyFolder f) {
		foldersUnreadCount.put(f, f.getUnreadCount());
		if (currentFolder == null) {
			currentFolder = WebmailController.get().getSelector().getCurrent();
		}
		if (f.equals(currentFolder) && currentTab == CONVERSATIONS) {
			setWindowTitle(f);
		}
	}

	public MailComposer getComposer() {
		return this.composer;
	}

	public SideBar getSidebar() {
		return sidebar;
	}

	private void setWindowTitle(String s) {
		String title = s + " - " + I18N.strings.appName();
		Window.setTitle(title);
	}

	private void setWindowTitle(Folder f) {
		String displayName = displayName(f);
		if (foldersUnreadCount.containsKey(f) && foldersUnreadCount.get(f) > 0) {
			setWindowTitle(displayName + " (" + foldersUnreadCount.get(f) + ")");
		} else {
			setWindowTitle(displayName);
		}
	}

	public void showFolderSettings() {
		selectTab(View.SETTINGS);
		settingsPanel.showFolderSettings();
	}

	public void showFilterSettings() {
		selectTab(View.SETTINGS);
		settingsPanel.showFilterSettings();
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public boolean confirmFolderAction(int nbConversations, Folder folderName) {
		return Window.confirm(I18N.strings.confirmFolderAction(Integer
				.toString(nbConversations), displayName(folderName)));
	}

	public void showGlobalSettings() {
		selectTab(View.SETTINGS);
		settingsPanel.showGlobalSettings();

	}

	public WebmailDragController getDragController() {
		return dragController;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentFolder(Folder currentFolder) {
		this.currentFolder = currentFolder;
	}

	public AddressBook getAddressBook() {
		return bookPanel;
	}

	public void tartiflette() {
		WebmailController.get().getSelector().stopUnreadTimer();
		TartifletteWarning t = new TartifletteWarning(this);
		t.showWarning();
	}

}
