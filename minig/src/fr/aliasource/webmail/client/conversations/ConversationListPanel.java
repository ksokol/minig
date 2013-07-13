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

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.rpc.UseCachedData;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.QuotaInfo;

/**
 * The list of conversations. The actions at the top are implemented in
 * {@link ConversationListActionsPanel}. The grid with the conversations is a
 * {@link DataGrid}.
 * 
 * @author tom
 * 
 */
public class ConversationListPanel extends DockPanel implements
		IFolderSelectionListener {

	public static final int PAGE_LENGTH = 25;

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
	private Anchor emptyFolderLink;
	private HorizontalPanel selectAllPanel;
	private boolean allSelected;
	private Folder purgeableFolder;

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
		if (purgeableFolder != null && emptyFolderPanel == null
				&& selectAllPanel == null && currentData.cListFullLength > 0) {
			emptyFolderPanel = new HorizontalPanel();
			emptyFolderPanel.setStyleName("emptyFolderPanel");
			emptyFolderPanel.setWidth("100%");

			emptyFolderLink = new Anchor(I18N.strings.emptyFolder(ui
					.displayName(purgeableFolder)));
			if (purgeableFolder.getName()
					.equals(
							WebmailController.get().getSetting(
									GetSettings.SPAM_FOLDER))) {
				emptyFolderLink.setText(I18N.strings.emptySpam());
			}
			emptyFolderLink.addClickHandler(getEmptyFolderListener());
			emptyFolderPanel.add(emptyFolderLink);

			northActions.add(emptyFolderPanel);
		}
	}

	public void destroyEmptyFolderPanel() {
		if (emptyFolderPanel != null) {
			emptyFolderPanel.removeFromParent();
			emptyFolderPanel = null;
		}
	}

	private ClickHandler getEmptyFolderListener() {
		return new ClickHandler() {
			public void onClick(ClickEvent sender) {
				final Folder folder = WebmailController.get().getSelector()
						.getCurrent();
				if (ui.confirmFolderAction(currentData.cListFullLength, folder)) {
					AsyncCallback<Void> ac = new AsyncCallback<Void>() {
						public void onFailure(Throwable caught) {
							ui.log("Failed to purge " + folder);
						}

						public void onSuccess(Void result) {
							ui.getSpinner().stopSpinning();
							ui
									.notifyUser(I18N.strings
											.allMessagesDeleted(folder
													.getDisplayName()));
							showPage(currentPage);
						}
					};
					ui.getSpinner().startSpinning();
					AjaxCall.store.purgeFolder(folder, ac);
				}

			}
		};
	}

	private void createSelectAllPanel() {
		if (selectAllPanel == null) {
			destroyEmptyFolderPanel();
			selectAllPanel = new HorizontalPanel();
			selectAllPanel.setStyleName("selectAllPanel");
			selectAllPanel.setWidth("100%");

			HorizontalPanel hp = new HorizontalPanel();
			Label selectAllInfo = new Label(I18N.strings
					.allPageConversationsSelected()
					+ " ");
			Anchor selectAllLink = new Anchor(I18N.strings
					.selectAllConversations(Integer
							.toString(currentData.cListFullLength), ui
							.displayName(WebmailController.get().getSelector()
									.getCurrent())));
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
				Label selectAllInfo = new Label(I18N.strings
						.allFolderConversationsSelected(Integer
								.toString(currentData.cListFullLength), ui
								.displayName(WebmailController.get()
										.getSelector().getCurrent())));
				Anchor clearSelection = new Anchor(I18N.strings
						.clearSelection());
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

	private void updateGrid(ConversationList cList, int page) {

		if (cList == null) {
			GWT.log("null conversation list", null);
			return;
		}

		currentPage = page;
		if (currentPage == 1 && !timerStarted) {
			if (timer == null) {
				recreateTimer();
			}
			timerStarted = true;
			timer.scheduleRepeating(20 * 1000);
		}
		if (timerStarted && currentPage != 1) {
			timerStarted = false;
			timer.cancel();
		}

		int folderSize = cList.getFullLength();
		int lastPageNumber = lastPage(folderSize);

		convToolbarNorth.updateButtonStates(folderSize, lastPageNumber,
				currentPage);
		convToolbarSouth.updateButtonStates(folderSize, lastPageNumber,
				currentPage);

		currentData.updateGrid(cList);

		updateCountLabels(page, folderSize, cList.getData().length);

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
		String newLabel = first + " - " + Math.max(0, (first + curPageLen - 1))
				+ " " + I18N.strings.convCountof() + " " + folderSize;
		convToolbarNorth.setCountLabel(newLabel);
		convToolbarSouth.setCountLabel(newLabel);
	}

	private int lastPage(float dataLen) {
		lastPage = (int) Math.ceil(dataLen / PAGE_LENGTH);
		return lastPage;
	}

	void showPage(final int page) {
		ui.getSpinner().startSpinning();
		AsyncCallback<ConversationList> callback = new AsyncCallback<ConversationList>() {
			public void onSuccess(ConversationList convs) {
				ui.getSpinner().stopSpinning();
				updateGrid(convs, page);
			}

			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				if (UseCachedData.MSG.equals(caught.getMessage())) {
					ui.log("no need to refresh, backend said nothing changed");
				} else {
					GWT.log("nextFailure, msg: " + caught.getMessage(), caught);
				}
			}
		};

		ui.log("showing page " + page + "...");
		String folderName = WebmailController.get().getSelector().getCurrent()
				.getName();
		if (folderName.startsWith("search:")) {
			AjaxCall.search.search(folderName.substring("search:".length()),
					page, PAGE_LENGTH, callback);
		} else {
			GWT.log("ui.selector.current: "
					+ WebmailController.get().getSelector().getCurrent()
							.getName() + " p: " + page, null);
			AjaxCall.listConversations.list(WebmailController.get()
					.getSelector().getCurrent(), page, PAGE_LENGTH, callback);
		}

	}

	public void showConversations(ConversationList convs, int page) {
		currentData.clear();
		updateGrid(convs, page);
	}

	public void showConversation(Folder sourceFolder, ConversationId convId) {
		destroyEmptyFolderPanel();
		destroySelectAllPanel();
		timer.cancel();
		timerStarted = false;
		conversationPanel.showConversation(sourceFolder, convId, currentPage);
	}

	public void showComposer(Folder sourceFolder, ConversationId convId) {
		timer.cancel();
		timerStarted = false;
		conversationPanel.showComposer(sourceFolder, convId);
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

	public void selectRead() {
		currentData.selectRead();
		destroySelectAllPanel();
	}

	public void selectUnread() {
		currentData.selectUnread();
		destroySelectAllPanel();
	}

	public void selectSome(Set<ConversationId> selectedIds) {
		currentData.selectSome(selectedIds);
	}

	public void deleteConversation() {
		currentData.deleteConversation();
	}

	public void moveConversation(Folder current, Folder f, boolean move,
			TailCall onSuccess) {
		currentData.moveConversation(current, f, move, onSuccess);
	}

	public void folderSelected(Folder f) {
		final String mailBox = f.getName();
		String trashFolder = WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER);
		String spamFolder = WebmailController.get().getSetting(
				GetSettings.SPAM_FOLDER);
		if (f.getName().equals(trashFolder) || f.getName().equals(spamFolder)) {
			purgeableFolder = f;
		} else {
			purgeableFolder = null;
		}
		destroyEmptyFolderPanel();
		destroySelectAllPanel();
		ui.setQuery(null);

		// Update quota bars
		if (mailBox.startsWith("search:")) {
			convToolbarNorth.updateQuotaBar(mailBox, new QuotaInfo());
			convToolbarSouth.updateQuotaBar(mailBox, new QuotaInfo());
		} else {
			AsyncCallback<QuotaInfo> callback = new AsyncCallback<QuotaInfo>() {
				public void onSuccess(QuotaInfo result) {
					convToolbarNorth.updateQuotaBar(mailBox, result);
					convToolbarSouth.updateQuotaBar(mailBox, result);
					ui.log("/GetQuota call on  " + mailBox + " successfull ");
				}

				public void onFailure(Throwable caught) {
					convToolbarNorth.updateQuotaBar(mailBox, new QuotaInfo());
					convToolbarSouth.updateQuotaBar(mailBox, new QuotaInfo());
					ui.log("/GetQuota failure (" + caught.getMessage() + ")");
				}
			};
			AjaxCall.quota.getQuota(mailBox, callback);
		}
	}

	public void foldersChanged(Folder[] folders) {
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
	}

	public DataGrid getCurrentData() {
		return currentData;
	}

	public boolean isAllSelected() {
		return allSelected;
	}

}
