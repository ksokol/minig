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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.composer.MailComposer;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.settings.SettingsPanel;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

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
    private SettingsPanel settingsPanel;
    private HorizontalPanel statusPanel;
    private SideBar sidebar;
    private IFolder currentFolder;
    private int currentTab;
    private Spinner spinner;

    public static final int CONVERSATIONS = 0;
    public static final int COMPOSER = 1;
    public static final int SETTINGS = 2;

    /**
     * Create a new webmail panel.
     * 
     * @param caller
     * @param settings
     * @param password
     */
    public View() {
        this.currentTab = -1;

        spinner = new Spinner();

        // add(constructHeadingAndToolbar(), DockPanel.NORTH);
        add(createTabPanel(), DockPanel.CENTER);

        statusPanel = new HorizontalPanel();
        statusPanel.setStyleName("statusPanel");
        add(statusPanel, DockPanel.NORTH);
        setCellHeight(statusPanel, "1.4em");

        sidebar = new SideBar(this);
        add(sidebar, DockPanel.WEST);
        setCellWidth(tp.getDeckPanel(), "100%");

        fetchMessages("INBOX", 1);
    }

    /**
     * called by {@link WebmailController}
     */
    public void startTimers() {
        // Event listener
        sinkEvents(Event.ONCLICK);
    }

    public String displayName(IFolder f) {
        return displayName(f.getId());
    }

    public String displayName(String fName) {
        return WebmailController.get().displayName(fName);
    }

    public void onBrowserEvent(Event event) {
        switch(DOM.eventGetType(event)) {
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

        settingsPanel = new SettingsPanel(View.this);
        tp.add(settingsPanel, "Settings");

        tp.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                currentTab = event.getSelectedItem();
                if (currentTab == COMPOSER) {
                    setWindowTitle(I18N.strings.compose());
                } else if (currentTab == SETTINGS) {
                    setWindowTitle(I18N.strings.settings());
                }
            }
        });

        selectTab(0);
        DeckPanel ret = tp.getDeckPanel();
        return ret;
    }

    public void fetchMessages(String folderName, final int page) {
        getSpinner().startSpinning();

        Ajax<IClientMessageList> builder = AjaxFactory.fetchMessages(folderName, ConversationListPanel.PAGE_LENGTH, page);

        try {
            builder.send(new AjaxCallback<IClientMessageList>() {
                @Override
                public void onError(Request request, Throwable exception) {
                    if (exception != null) {
                        WebmailController.get().getView().notifyUser(exception.getMessage());
                    } else {
                        WebmailController.get().getView().notifyUser("something went wrong");
                    }
                }

                @Override
                public void onSuccess(IClientMessageList object) {
                    conversationPanel.showConversations(object, page);
                    getSpinner().stopSpinning();
                }
            });
        } catch (RequestException e) {
            WebmailController.get().getView().notifyUser(e.getMessage());
        }
    }

    public void log(String s) {
        GWT.log(s, null);
    }

    public void log(String s, Throwable t) {
        GWT.log(s, t);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void selectTab(int tab) {
        tp.selectTab(tab);
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

    public void showConversation(String conversationId, int page) {
        selectTab(CONVERSATIONS);
        conversationPanel.showConversation(conversationId, page);
    }

    public void showComposer(String messageId) {
        conversationPanel.showComposer(messageId);
    }

    public void folderSelected(IFolder f) {
        selectTab(CONVERSATIONS);
        fetchMessages(f.getId(), 1);
        setWindowTitle(f);
        currentFolder = f;
    }

    public void foldersChanged(List<IFolder> folders) {
        // TODO war leer
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

    private void setWindowTitle(IFolder f) {
        String displayName = displayName(f);
        setWindowTitle(displayName);
    }

    public void showFolderSettings() {
        selectTab(View.SETTINGS);
        settingsPanel.showFolderSettings();
    }

    public boolean confirmFolderAction(int nbConversations, IFolder folderName) {
        return Window.confirm(I18N.strings.confirmFolderAction(Integer.toString(nbConversations), displayName(folderName)));
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentFolder(IFolder currentFolder) {
        this.currentFolder = currentFolder;
    }

}
