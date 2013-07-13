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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

public class UndoMoveWidget extends HorizontalPanel {

	private View ui;

	public UndoMoveWidget(View ui, final String origFolder,
			String currentFolder, final List<ConversationId> newIds) {
		this.ui = ui;
		final UndoMoveWidget self = this;
		String undoLabel = "";
		if (currentFolder.equalsIgnoreCase(WebmailController.get().getSetting(
				GetSettings.SPAM_FOLDER))) {
			undoLabel = I18N.strings.conversationMarkedAsSpam();
			if (!newIds.isEmpty()) {
				undoLabel = I18N.strings.conversationsMarkedAsSpam(Integer
						.toString(newIds.size()));
			}
		} else {
			if (newIds.isEmpty()) {
				undoLabel = I18N.strings.permissionDenied();
			} else if (newIds.size() == 1) {
				undoLabel = I18N.strings.conversationMoveTo(ui
						.displayName(currentFolder));
			} else {
				undoLabel = I18N.strings
						.conversationsMoveTo(Integer.toString(newIds.size()),
								ui.displayName(currentFolder));
			}
		}
		add(new Label(undoLabel));
		if (!origFolder.startsWith("search:") && newIds != null
				&& !newIds.isEmpty()) {
			Anchor undoTrash = new Anchor(I18N.strings.undo());
			add(undoTrash);
			undoTrash.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					undoMove(self, new Folder(origFolder), newIds);
				}
			});
		}

		setSpacing(3);
	}

	public UndoMoveWidget(View ui, final String origFolder,
			final List<ConversationId> newIds) {
		this(ui, origFolder, WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER), newIds);
	}

	private void undoMove(final Widget notif, final Folder moveTo,
			List<ConversationId> newIds) {
		AsyncCallback<List<ConversationId>> ac = new AsyncCallback<List<ConversationId>>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("Error moving conversations", caught);
			}

			public void onSuccess(List<ConversationId> result) {
				ui.getSpinner().stopSpinning();
				ui.fetchConversations(moveTo.getName(), 1);
				WebmailController.get().getSelector().refreshUnreadCounts();
			}
		};
		ui.clearNotification(notif);
		ui.getSpinner().startSpinning();
		AjaxCall.store
				.moveConversation(new Folder[0], moveTo, newIds, true, ac);
	}

}
