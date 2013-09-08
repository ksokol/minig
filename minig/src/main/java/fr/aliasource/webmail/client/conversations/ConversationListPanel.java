/*
 *  BEGIN LICENSE BLOCK Version: GPL 2.0
 * 
 * The contents of this file are subject to the GNU General Public License
 * Version 2 or later (the "GPL").
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Initial Developer of the Original Code is MiniG.org project members
 * 
 * END LICENSE BLOCK
 */

package fr.aliasource.webmail.client.conversations;

import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.ConversationPanel;
import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IFolderSelectionListener;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

/**
 * The list of conversations. The actions at the top are implemented in
 * {@link ConversationListActionsPanel}. The grid with the conversations is a
 * {@link DataGrid}.
 * 
 * @author tom
 * 
 */
public class ConversationListPanel extends DockPanel implements IFolderSelectionListener {

    public static final int PAGE_LENGTH = 20;

    private ConversationPanel conversationPanel;
    private int currentPage;
    private int lastPage;
    private DataGrid currentData;
    private View ui;
    private Timer timer;
    private boolean timerStarted;
    private VerticalPanel northActions;
    private ConversationListActionsPanel convToolbarNorth;
    private ConversationListActionsPanel convToolbarSouth;
    private HorizontalPanel emptyFolderPanel;
    private HorizontalPanel selectAllPanel;
    private boolean allSelected;
    private String purgeableFolder;

    public ConversationListPanel(ConversationPanel cp, View wm) {
        this.ui = wm;
        this.conversationPanel = cp;

        northActions = new VerticalPanel();

        convToolbarNorth = new ConversationListActionsPanel(this, wm, Position.North);
        convToolbarNorth.setWidth("100%");

        northActions.add(convToolbarNorth);
        northActions.setWidth("100%");

        add(northActions, DockPanel.NORTH);

        convToolbarSouth = new ConversationListActionsPanel(this, wm, Position.South);
        add(convToolbarSouth, DockPanel.SOUTH);
        convToolbarSouth.setWidth("100%");
        SimplePanel spData = new SimplePanel();
        spData.addStyleName("dataGrid");
        currentData = new DataGrid(this, ui);
        spData.add(currentData);
        add(spData, DockPanel.NORTH);

        currentData.addSelectionChangedListener(convToolbarNorth);
        currentData.addSelectionChangedListener(convToolbarSouth);

        setWidth("100%");

        recreateTimer();
        WebmailController.get().getSelector().addListener(this);
    }

    private void recreateTimer() {
        timer = new Timer() {
            public void run() {
                ui.log("reload from timer at " + System.currentTimeMillis());
                showPage(currentPage);
            }

        };
        timerStarted = false;
    }

    private void createEmptyFolderPanel() {
        if (purgeableFolder != null && emptyFolderPanel == null && selectAllPanel == null && currentData.cListFullLength > 0) {
            emptyFolderPanel = new HorizontalPanel();
            emptyFolderPanel.setStyleName("emptyFolderPanel");
            emptyFolderPanel.setWidth("100%");

            throw new UnsupportedOperationException();
            // if
            // (purgeableFolder.getName().equals(WebmailController.get().getSetting(GetSettings.SPAM_FOLDER)))
            // {
            // emptyFolderLink.setText(I18N.strings.emptySpam());
            // }
            // emptyFolderLink.addClickHandler(getEmptyFolderListener());
            // emptyFolderPanel.add(emptyFolderLink);
            //
            // northActions.add(emptyFolderPanel);
        }
    }

    public void destroyEmptyFolderPanel() {
        if (emptyFolderPanel != null) {
            emptyFolderPanel.removeFromParent();
            emptyFolderPanel = null;
        }
    }

    private void createSelectAllPanel() {
        if (selectAllPanel == null) {
            destroyEmptyFolderPanel();
            selectAllPanel = new HorizontalPanel();
            selectAllPanel.setStyleName("selectAllPanel");
            selectAllPanel.setWidth("100%");

            HorizontalPanel hp = new HorizontalPanel();
            Label selectAllInfo = new Label(I18N.strings.allPageConversationsSelected() + " ");
            Anchor selectAllLink = new Anchor(I18N.strings.selectAllConversations(Integer.toString(currentData.cListFullLength),
                    ui.displayName(WebmailController.get().getSelector().getCurrent())));
            selectAllLink.addClickHandler(getSelectAllListener());

            hp.add(selectAllInfo);
            hp.add(selectAllLink);

            selectAllPanel.add(hp);
            northActions.add(selectAllPanel);
        }
    }

    public void destroySelectAllPanel() {
        allSelected = false;
        if (selectAllPanel != null) {
            this.currentData.clearAllSelected();
            selectAllPanel.removeFromParent();
            selectAllPanel = null;
            createEmptyFolderPanel();
        }
    }

    private ClickHandler getSelectAllListener() {
        return new ClickHandler() {
            public void onClick(ClickEvent ev) {
                selectAllConversations();
                selectAllPanel.clear();
                selectAllPanel.setStyleName("clearSelectAllPanel");

                HorizontalPanel hp = new HorizontalPanel();
                Label selectAllInfo = new Label(I18N.strings.allFolderConversationsSelected(Integer.toString(currentData.cListFullLength),
                        ui.displayName(WebmailController.get().getSelector().getCurrent())));
                Anchor clearSelection = new Anchor(I18N.strings.clearSelection());
                clearSelection.addClickHandler(getClearSelectionListener());

                hp.add(selectAllInfo);
                hp.add(clearSelection);

                selectAllPanel.add(hp);

                allSelected = true;
                selectAll();
            }
        };
    }

    private ClickHandler getClearSelectionListener() {
        return new ClickHandler() {
            public void onClick(ClickEvent ev) {
                selectNone();
                ui.log("Clear selection");
            }
        };
    }

    private void updateGrid(IClientMessageList cList, int page) {

        if (cList == null) {
            return;
        }

        currentPage = page;
        if (currentPage == 1 && !timerStarted) {
            if (timer == null) {
                recreateTimer();
            }
            timerStarted = true;
            timer.scheduleRepeating(60 * 1000);
        }
        if (timerStarted && currentPage != 1) {
            timerStarted = false;
            timer.cancel();
        }

        int folderSize = cList.getFullLength();
        int lastPageNumber = lastPage(folderSize);

        convToolbarNorth.updateButtonStates(folderSize, lastPageNumber, currentPage);
        convToolbarSouth.updateButtonStates(folderSize, lastPageNumber, currentPage);

        currentData.updateGrid(cList);

        updateCountLabels(page, folderSize, cList.getMailList().size());

        if (currentData.cListFullLength > 0 && purgeableFolder != null) {
            createEmptyFolderPanel();
        }

        if (currentData.cListFullLength > PAGE_LENGTH && selectAllPanel != null) {
            createSelectAllPanel();
        }
        if (allSelected) {
            selectAll();
        }
    }

    private void updateCountLabels(int page, int folderSize, int curPageLen) {
        int first = ((page - 1) * PAGE_LENGTH) + 1;
        if (page == 1 && curPageLen == 0) {
            first = 0;
        }
        String newLabel = first + " - " + Math.max(0, (first + curPageLen - 1)) + " " + I18N.strings.convCountof() + " " + folderSize;
        convToolbarNorth.setCountLabel(newLabel);
        convToolbarSouth.setCountLabel(newLabel);
    }

    private int lastPage(float dataLen) {
        lastPage = (int) Math.ceil(dataLen / PAGE_LENGTH);
        return lastPage;
    }

    void showPage(final int page) {
        String folder = WebmailController.get().getSelector().getCurrent().getId();

        Ajax<IClientMessageList> builder = AjaxFactory.fetchMessages(folder, ConversationListPanel.PAGE_LENGTH, page);

        try {
            builder.send(new AjaxCallback<IClientMessageList>() {
                @Override
                public void onError(Request request, Throwable exception) {
                    // FIXME
                    System.out.println("onError");
                }

                //
                // @Override
                // public void onResponseReceived(Request request, Response
                // response) {
                // if (200 == response.getStatusCode()) {
                // AutoBean<IClientMessageList> bean =
                // AutoBeanCodex.decode(factory, IClientMessageList.class,
                // response.getText());
                //
                //
                // } else {
                // // FIXME
                // System.out.println("nok: " + response.getStatusCode());
                // ui.getSpinner().stopSpinning();
                // }
                // }

                @Override
                public void onSuccess(IClientMessageList object) {
                    // TODO Auto-generated method stub
                    updateGrid(object, page);
                    ui.getSpinner().stopSpinning();
                }

            });
        } catch (RequestException e) {
            e.printStackTrace();
        }

        // AjaxCall.listConversations
        // .list(WebmailController.get().getSelector().getCurrent(), page,
        // PAGE_LENGTH, callback);

    }

    public void showConversations(IClientMessageList convs, int page) {
        currentData.clear();
        updateGrid(convs, page);
    }

    public void showConversation(String convId) {
        destroyEmptyFolderPanel();
        destroySelectAllPanel();
        timer.cancel();
        timerStarted = false;
        conversationPanel.showConversation(convId, currentPage);
    }

    public void showComposer(String messageId) {
        timer.cancel();
        timerStarted = false;
        conversationPanel.showComposer(messageId);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void clearTimers() {
        if (timer != null && timerStarted) {
            timer.cancel();
        }
    }

    public void selectAllConversations() {
        currentData.selectAllConversations();
    }

    public void selectAll() {
        currentData.selectAll();
        if (currentData.cListFullLength > PAGE_LENGTH) {
            createSelectAllPanel();
        }
    }

    public void selectNone() {
        currentData.selectNone();
        destroySelectAllPanel();
    }

    public void selectSome(Set<String> selectedIds) {
        currentData.selectSome(selectedIds);
    }

    public void deleteConversation() {
        currentData.deleteConversation();
    }

    public void moveConversation(String current, String f, boolean move, TailCall onSuccess) {
        currentData.moveConversation(current, f, move, onSuccess);
    }

    public void folderSelected(IFolder f) {

        // TODO
        // final String mailBox = f;
        // String trashFolder = null; //
        // WebmailController.get().getSetting(GetSettings.TRASH_FOLDER);
        //
        // if (f.equals(trashFolder)) {
        // purgeableFolder = f;
        // } else {
        // purgeableFolder = null;
        // }
        // destroyEmptyFolderPanel();
        // destroySelectAllPanel();
    }

    public void foldersChanged(List<IFolder> folders) {
        // TODO war leer
    }

    public DataGrid getCurrentData() {
        return currentData;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

}
