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

package fr.aliasource.webmail.client.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.WebSafeColors;
import fr.aliasource.webmail.client.XssUtils;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;

/**
 * Widget used to display all the messages in a conversation
 * 
 * @author tom
 * 
 */
public class ConversationDisplay extends DockPanel {

	private List<MessageWidget> messages;
	private ConversationActions southToolbar;
	private ConversationActions northToolbar;
	private ConversationContent conversationContent;
	private HTML title;
	private VerticalPanel vp;
	private View ui;
	private int page;

	public ConversationDisplay(View ui, ConversationListPanel listPanel,
			int page) {
		this.ui = ui;
		this.page = page;
		northToolbar = new ConversationActions(this, ui, listPanel, page, Position.North);
		add(northToolbar, DockPanel.NORTH);

		vp = new VerticalPanel();
		vp.setStyleName("conversationDisplay");

		title = new HTML(I18N.strings.loadingConversation() + "...");
		title.setStyleName("conversationTitle");
		title.setWidth("100%");
		vp.add(title);

		southToolbar = new ConversationActions(this, ui, listPanel, page, Position.South);
		add(southToolbar, DockPanel.SOUTH);
		setWidth("100%");

		vp.setWidth("100%");
		add(vp, DockPanel.CENTER);

	}

	public void setConversationContent(ConversationContent cc) {
		if (conversationContent != null) {
			conversationContent.destroy();
		}

		this.conversationContent = cc;
		vp.clear();
		vp.add(title);
		Folder f = new Folder(cc.getConversation().getSourceFolder());
		title
				.setHTML("<b>"
						+ XssUtils.safeHtml(cc.getTitle())
						+ "</b>&nbsp;<span class=\"convFolderTag\" style=\"background-color:"
						+ WebSafeColors.htmlColor(f)
						+ "; color:"
						+ WebSafeColors.fgColor(f)
						+ ";\" >"
						+ ui
								.displayName(cc.getConversation()
										.getSourceFolder()) + "</span>");
		ClientMessage[] cm = cc.getMessages();
		messages = new ArrayList<MessageWidget>(cm.length + 1);
		DateFormatter df = new DateFormatter(new Date());
		RecipientsStyleHandler rsh = new RecipientsStyleHandler(cc);
		for (int i = 0; i < cm.length; i++) {
			MessageWidget mw = null;
			if (i == cm.length - 1) {
				// show last message expanded with quick reply enabled
				mw = new MessageWidget(ui, this, df, cm[i], true, rsh);
				mw.setOpen(true);
				mw.setLastMessage(true);
			} else {
				mw = new MessageWidget(ui, this, df, cm[i], false, rsh);
				mw.setOpen(!cm[i].isRead());
			}
			mw.setWidth("100%");
			messages.add(mw);
			vp.add(mw);
		}
		southToolbar.notifyConvChanged();
		northToolbar.notifyConvChanged();
	}

	public void shutdown() {
		northToolbar.shutdown();
		southToolbar.shutdown();
		for (MessageWidget mw : messages) {
			mw.destroy();
		}
		messages.clear();
		conversationContent.destroy();
		conversationContent = null;
	}

	public void setExpanded(boolean b) {
		for (MessageWidget mw : messages) {
			mw.setOpen(b);
		}
		northToolbar.updateLinks(b);
		southToolbar.updateLinks(b);
	}

	public ConversationContent getConversationContent() {
		return conversationContent;
	}

	private void updateRemoveMessage(MessageId uid) {
		if (messages.size() == 1) {
			Folder sel = WebmailController.get().getSelector().getCurrent();
			final String selName = sel.getName();
			ui.fetchConversations(selName, page);
		} else {
			MessageWidget mw = null;
			for (Iterator<MessageWidget> it = messages.iterator(); it.hasNext();) {
				mw = it.next();
				if (mw.getMessage().getUid().equals(uid)) {
					vp.remove(mw);
					it.remove();
					break;
				}
			}
			if (mw != null && mw.isLastMessage()) {
				MessageWidget to = messages.get(messages.size() - 1);
				to.setOpen(true);
				to.setLastMessage(true);
			}
		}
	}

	public void deleteMessage(ConversationId convId, MessageId uid) {
		Folder sel = WebmailController.get().getSelector().getCurrent();
		String trash = WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER);
		ui.getSpinner().startSpinning();
		if (trash.equals(sel.getName())) {
			// Delete message forever
			AjaxCall.store.deleteMessage(convId, uid, deleteCallback(uid));
		} else {
			// Move to trash
			AjaxCall.store.trashMessage(convId, uid, moveToTrashCallback(uid));
		}
	}

	private AsyncCallback<Void> deleteCallback(final MessageId puid) {
		return new AsyncCallback<Void>() {

			private MessageId uid = puid;

			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("Error trashing conversations", caught);
			}

			public void onSuccess(Void result) {
				ui.notifyUser(I18N.strings.messageDeletedForever());
				updateRemoveMessage(uid);
				ui.getSpinner().stopSpinning();
			}
		};
	}

	private AsyncCallback<ConversationId> moveToTrashCallback(final MessageId puid) {
		return new AsyncCallback<ConversationId>() {
			private MessageId uid = puid;

			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("Error trashing conversations", caught);
			}

			public void onSuccess(ConversationId convId) {
				ui
						.notifyUser(I18N.strings.messageDeleted(I18N.strings
								.trash()));
				updateRemoveMessage(uid);
				ui.getSpinner().stopSpinning();
			}

		};
	}
}
