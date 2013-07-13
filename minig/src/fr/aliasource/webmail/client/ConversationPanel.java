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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.composer.MailComposer;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.reader.ConversationDisplay;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Widget handling the switch between conversation list and reader display
 * 
 * @author tom
 * 
 */
public class ConversationPanel extends VerticalPanel {

	private View ui;
	private ConversationListPanel listPanel;
	private ConversationDisplay convPanel;

	public ConversationPanel(View ui) {
		super();
		this.ui = ui;
		listPanel = new ConversationListPanel(this, ui);
	}

	private void setContent(Widget w) {
		clear();
		add(w);
	}

	public void showConversations(ConversationList convs, int page) {
		setContent(listPanel);
		if (convPanel != null) {
			convPanel.shutdown();
			convPanel = null;
		}
		listPanel.showConversations(convs, page);
	}

	public void showConversation(final Folder sourceFolder, ConversationId convId,
			int page) {
		ui.getSpinner().startSpinning();
		if (convPanel != null) {
			convPanel.shutdown();
		}
		convPanel = new ConversationDisplay(ui, listPanel, page);
		setContent(convPanel);

		AsyncCallback<ConversationContent> ac = new AsyncCallback<ConversationContent>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				GWT.log("failure on showConversation", caught);
			}

			public void onSuccess(ConversationContent cc) {
				ui.getSpinner().stopSpinning();
				convPanel.setConversationContent(cc);
				WebmailController.get().getSelector().refreshUnreadCounts();
			}
		};
		AjaxCall.sca.show(convId, ac);
	}

	public void clearTimers() {
		listPanel.clearTimers();
		if (convPanel != null) {
			convPanel.shutdown();
		}
	}

	public void showComposer(final Folder sourceFolder, final ConversationId convId) {
		if (ui.getCurrentTab() != View.COMPOSER) {
			ui.selectTab(View.COMPOSER);
		}
		AsyncCallback<ConversationContent> ac = new AsyncCallback<ConversationContent>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("failure on showComposer");
			}

			public void onSuccess(ConversationContent cc) {
				ui.log("success on showComposer");
				ui.getSpinner().stopSpinning();
				ClientMessage cm = cc.getMessages()[cc.getMessages().length - 1];
				MailComposer mp = ui.getComposer();
				if (sourceFolder.getName().equals(
						WebmailController.get().getSetting(
								GetSettings.TEMPLATES_FOLDER))) {
					mp.loadDraft(cm, null, false);
				} else if (sourceFolder.getName().equals(
						WebmailController.get().getSetting(
								GetSettings.DRAFTS_FOLDER))) {
					mp.loadDraft(cm, convId, false);
				} else {
					mp.loadDraft(cm, convId);
				}
			}
		};
		AjaxCall.sca.show(convId, ac);
	}

}
