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

package fr.aliasource.webmail.client.conversations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IDestroyable;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.reader.AnsweredWidget;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * The grid widgets with the list of conversations
 * 
 * @author tom
 * 
 */
public class DataGrid extends Grid {

	private ConversationListPanel clp;
	private Conversation[] curConvData;
	private View ui;

	public int cListFullLength;

	private List<IConversationSelectionChangedListener> selectionListeners;

	private Set<ConversationId> selectedIds;
	private Set<Folder> usedFolders;
	private List<HandlerRegistration> regs;

	public DataGrid(ConversationListPanel clp, View ui) {
		super(1, 9);
		this.regs = new LinkedList<HandlerRegistration>();
		this.clp = clp;
		this.ui = ui;
		setStyleName("conversationTable");
		styleRow(0);

		usedFolders = new HashSet<Folder>();
		selectionListeners = new ArrayList<IConversationSelectionChangedListener>();
		selectedIds = new HashSet<ConversationId>();
		addSelectionChangedListener(ui.getDragController());
		ui.getDragController().setGrid(this);
	}

	private void styleRow(int row) {
		CellFormatter cf = getCellFormatter();
		int i = 0;
		cf.addStyleName(row, i++, "convGrip");
		cf.addStyleName(row, i++, "convCb");
		cf.addStyleName(row, i++, "convStar");
		cf.addStyleName(row, i++, "convStar");
		cf.addStyleName(row, i++, "convRecip");
		cf.addStyleName(row, i++, "convSize");
		cf.addStyleName(row, i++, "conversationAndPreviewCol");
		cf.addStyleName(row, i++, "convAttach");
		cf.addStyleName(row, i++, "convDate");
	}

	public void updateGrid(ConversationList cList) {
		DateFormatter dtf = new DateFormatter(new Date());
		curConvData = cList.getData();
		cListFullLength = cList.getFullLength();

		usedFolders.clear();
		for (Conversation c : curConvData) {
			usedFolders.add(new Folder(c.getSourceFolder()));
		}

		int rc = curConvData.length;
		destroyCurrentWidgets();
		if (rc == 0) {
			showEmptyList();
		} else {
			if (getRowCount() != rc) {
				resizeRows(rc);
			}
			for (int i = 0; i < rc; i++) {
				try {
					fillRow(dtf, curConvData[i], i);
					styleRow(i);
				} catch (Throwable t) {
					GWT.log("error drawing row", t);
				}
			}
		}
	}

	private void destroyCurrentWidgets() {
		for (HandlerRegistration hr : regs) {
			hr.removeHandler();
		}
		regs.clear();

		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					if (w instanceof IDestroyable) {
						((IDestroyable) w).destroy();
					}
					w.removeFromParent();
				}
			}
		}
	}

	private void showEmptyList() {
		clear();
		resizeRows(0);
		resizeRows(1);
		// clear does not handle column set with "setText"
		setHTML(0, 2, "&nbsp;");
		setWidget(0, 3, new Label(I18N.strings.noAvailableConversations(),
				false));
		clp.destroySelectAllPanel();
		clp.destroyEmptyFolderPanel();
	}

	private void fillRow(DateFormatter dtf, Conversation data, int i) {
		int col = 0;

		setWidget(i, col++, new GripImage(data));

		CheckBox selector = new CheckBox();
		if (selectedIds.contains(data.getId())) {
			selector.setValue(true);
		}
		regs.add(selector.addClickHandler(getCheckListener(data.getId(),
				selector)));
		setWidget(i, col++, selector);

		setWidget(i, col++, new StarWidget(data.isStarred(), data.getId()));

		if (data.isAnswered()) {
			setWidget(i, col++, new AnsweredWidget());
		} else {
			setWidget(i, col++, new HTML("&nbsp"));
		}

		ClickHandler cl = newShowConversationListener(data, i);

		ParticipantsWidget pw = new ParticipantsWidget(ui, data, cl);
		regs.add(pw.getRegistration());
		setWidget(i, col++, pw);

		HTML count = null;
		if (data.getMessageCount() > 1) {
			count = new HTML("(" + data.getMessageCount() + ")");
		} else {
			count = new HTML("&nbsp;");
		}
		regs.add(count.addClickHandler(cl));
		setWidget(i, col++, count);

		Widget convWidget = null;
		try {
			convWidget = getConversationWidget(data, cl);
		} catch (Throwable t) {
			GWT.log("crash on email", t);
			HTML h = new HTML("[" + data.getTitle() + "]");
			regs.add(h.addClickHandler(cl));
			convWidget = h;
		}

		setWidget(i, col++, convWidget);
		if (data.hasInvitation()) {
			setWidget(i, col++, new Image("minig/images/invitation.gif"));
		} else {
			if (data.hasAttachements()) {
				setWidget(i, col++, new Image("minig/images/paperclip.gif"));
			} else if (data.getSourceFolder().equals("#chat")) {
				setWidget(i, col++, new Image("minig/images/chat.gif"));
			} else {
				setHTML(i, col++, "&nbsp;");
			}
		}
		Label dlbl = new Label(dateAsText(dtf, data.getDate()), false);
		DateFormatter df = new DateFormatter(new Date(data.getDate()));
		dlbl.setTitle(df.formatDetails(new Date(data.getDate())));
		if (data.isUnread()) {
			dlbl.addStyleName("bold");
		}
		setWidget(i, col++, dlbl);
	}

	private String dateAsText(DateFormatter dtf, long date) {
		Date d = new Date(date);
		return dtf.formatSmall(d);
	}

	private Widget getConversationWidget(Conversation conversation,
			ClickHandler cl) {
		ConversationWidget ret = new ConversationWidget(usedFolders,
				conversation);
		regs.add(ret.addClickHandler(cl));
		return ret;
	}

	private ClickHandler newShowConversationListener(final Conversation conv,
			final int row) {
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (sender.getNativeEvent().getCtrlKey()) {
					switchSelected(row);
					return;
				}

				if (conv.getSourceFolder().equals(
						WebmailController.get().getSetting(
								GetSettings.DRAFTS_FOLDER))
						|| conv.getSourceFolder().equals(
								WebmailController.get().getSetting(
										GetSettings.TEMPLATES_FOLDER))) {
					ui.log("showDraft(" + conv.getSourceFolder() + ", "
							+ conv.getId() + ")");
					clp.showComposer(new Folder(conv.getSourceFolder()),
							conv.getId());
				} else {
					ui.log("showConversation(" + conv.getSourceFolder() + ", "
							+ conv.getId() + ")");
					clp.showConversation(new Folder(conv.getSourceFolder()),
							conv.getId());
				}
			}
		};
		return cl;
	}

	public ClickHandler getCheckListener(final ConversationId convId, final CheckBox cb) {
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (cb.getValue()) {
					selectedIds.add(convId);
				} else {
					selectedIds.remove(convId);
					clp.destroySelectAllPanel();
				}
				notifySelectionListeners();
			}
		};
		return cl;
	}

	public void selectAllConversations() {
		notifySelectAllListeners();
	}

	public void selectAll() {
		for (int i = 0; i < curConvData.length; i++) {
			setSelected(i, true);
		}
		notifySelectionListeners();
	}

	public void selectNone() {
		for (int i = 0; i < curConvData.length; i++) {
			setSelected(i, false);
		}
		notifySelectionListeners();
	}

	public void selectRead() {
		for (int i = 0; i < curConvData.length; i++) {
			setSelected(i, !curConvData[i].isUnread());
		}
		notifySelectionListeners();
	}

	public void selectUnread() {
		for (int i = 0; i < curConvData.length; i++) {
			setSelected(i, curConvData[i].isUnread());
		}
		notifySelectionListeners();
	}

	public void clear() {
		selectedIds.clear();
		super.clear();
		notifySelectionListeners();
	}

	public void addSelectionChangedListener(
			IConversationSelectionChangedListener cscl) {
		selectionListeners.add(cscl);
	}

	private void notifySelectionListeners() {
		for (IConversationSelectionChangedListener cscl : selectionListeners) {
			cscl.selectionChanged(selectedIds);
		}
	}

	private void notifySelectAllListeners() {
		for (IConversationSelectionChangedListener cscl : selectionListeners) {
			cscl.selectionChanged("ALL");
		}
	}

	private void setSelected(int row, boolean selected) {
		CheckBox cb = (CheckBox) getWidget(row, 1);
		cb.setValue(selected);
		Conversation conv = curConvData[row];
		if (selected) {
			selectedIds.add(conv.getId());
		} else {
			selectedIds.remove(conv.getId());
		}
	}

	private void switchSelected(int row) {
		CheckBox cb = (CheckBox) getWidget(row, 1);
		setSelected(row, !cb.getValue());
		notifySelectionListeners();
	}

	public void clearAllSelected() {
		Set<ConversationId> temp = new HashSet<ConversationId>();
		for (Conversation conv : curConvData) {
			temp.add(conv.getId());
		}
		selectedIds.retainAll(temp);
		notifySelectionListeners();
	}

	public void deleteConversation() {
		final List<ConversationId> convIds = new ArrayList<ConversationId>(selectedIds);
		Folder sel = WebmailController.get().getSelector().getCurrent();
		String trash = WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER);
		String spam = WebmailController.get().getSetting(
				GetSettings.SPAM_FOLDER);
		if (trash.equals(sel.getName()) || spam.equals(sel.getName())) {
			// Delete conversation forever
			if (clp.isAllSelected()) {
				if (ui.confirmFolderAction(
						clp.getCurrentData().cListFullLength, sel)) {
					Folder folder = sel;
					ui.getSpinner().startSpinning();
					AjaxCall.store.purgeFolder(folder,
							purgeFolderCallback(folder));
				}
			} else {
				ui.getSpinner().startSpinning();
				AjaxCall.store.deleteConversation(convIds,
						deleteForeverCallback(convIds));
			}
		} else {
			// Move to trash
			if (clp.isAllSelected()) {
				if (ui.confirmFolderAction(
						clp.getCurrentData().cListFullLength, sel)) {
					String query = ui.getQuery();
					if (query == null) {
						query = "in:" + sel.getName();
					}
					ui.getSpinner().startSpinning();
					AjaxCall.store.trashConversation(query,
							moveToTrashCallback());
				}
			} else {
				ui.getSpinner().startSpinning();
				AjaxCall.store.trashConversation(new Folder[0], convIds,
						moveToTrashCallback());
			}
		}
	}

	private AsyncCallback<Void> deleteForeverCallback(final List<ConversationId> convIds) {
		return new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				ui.log("Failed to delete conversation.");
				ui.getSpinner().stopSpinning();
			}

			public void onSuccess(Void result) {
				String message = I18N.strings.conversationDeletedForever();
				if (!convIds.isEmpty()) {
					message = convIds.size()
							+ I18N.strings.conversationsDeletedForever(Integer
									.toString(convIds.size()));
				}
				ui.notifyUser(message);
				clp.selectNone();
				clp.showPage(clp.getCurrentPage());
				ui.getSpinner().stopSpinning();
			}
		};
	}

	private AsyncCallback<List<ConversationId>> moveToTrashCallback() {
		return new AsyncCallback<List<ConversationId>>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("Error trashing conversations", caught);
			}

			public void onSuccess(List<ConversationId> newIds) {
				clp.selectNone();
				clp.showPage(clp.getCurrentPage());
				ui.notifyUser(new UndoMoveWidget(ui, WebmailController.get()
						.getSelector().getCurrent().getName(), newIds), 20);
				ui.getSpinner().stopSpinning();
			}
		};
	}

	private AsyncCallback<Void> purgeFolderCallback(final Folder f) {
		final String folder = f.getDisplayName();
		return new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("Failed to purge " + folder);
			}

			public void onSuccess(Void result) {
				ui.getSpinner().stopSpinning();
				ui.notifyUser(I18N.strings.allMessagesDeleted(folder));
				clp.showPage(clp.getCurrentPage());
				clp.destroySelectAllPanel();
			}
		};
	}

	public void moveConversation(Folder current, Folder f, boolean move,
			TailCall onSuccess) {
		if (clp.isAllSelected()) {
			if (ui.confirmFolderAction(clp.getCurrentData().cListFullLength,
					WebmailController.get().getSelector().getCurrent())) {
				String query = ui.getQuery();
				if (query == null) {
					query = "in:\""
							+ WebmailController.get().getSelector()
									.getCurrent().getName() + "\"";
				}
				ui.getSpinner().startSpinning();
				AjaxCall.store.moveConversation(query, f, move,
						moveConversationCallback(ui, current, f, move, null));
			}
		} else {
			moveSomeConversations(current, f, selectedIds, move, onSuccess);
		}
	}

	public void moveSomeConversations(Folder current, Folder f,
			Set<ConversationId> ids, boolean move, TailCall onSuccess) {
		ui.getSpinner().startSpinning();
		AjaxCall.store.moveConversation(new Folder[] { current }, f, 
				new ArrayList<ConversationId>(ids), move,
				moveConversationCallback(ui, current, f, move, onSuccess));
	}

	private AsyncCallback<List<ConversationId>> moveConversationCallback(final View ui,
			final Folder current, final Folder dest, final boolean move,
			final TailCall onSuccess) {
		return new AsyncCallback<List<ConversationId>>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.notifyUser(I18N.strings.errorMovingConv());
				GWT.log(caught.getMessage(), caught);
			}

			public void onSuccess(List<ConversationId> result) {
				ui.getSpinner().stopSpinning();
				clp.selectNone();
				clp.showPage(clp.getCurrentPage());
				WebmailController.get().getSelector().refreshUnreadCounts();
				if (move) {
					Widget w = new UndoMoveWidget(ui, current.getName(),
							dest.getName(), result);
					ui.notifyUser(w, 20);
				} else {
					ui.notifyUser(I18N.strings.mailCopyDone(WebmailController
							.get().displayName(dest.getName())));
				}
				if (onSuccess != null) {
					onSuccess.run();
				}
			}
		};
	}

	public void selectSome(Set<ConversationId> sel) {
		HashSet<ConversationId> tmp = new HashSet<ConversationId>(2 * sel.size());
		tmp.addAll(sel);
		selectNone();
		selectedIds = tmp;
	}
}