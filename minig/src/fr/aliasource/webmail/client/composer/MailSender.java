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

package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;

/**
 * Handles the mail sending process in the composer
 * 
 * @author tom
 * 
 */
public class MailSender {

	private View ui;
	private MailComposer mc;

	public MailSender(View ui, MailComposer mc) {
		this.ui = ui;
		this.mc = mc;
	}

	public void sendMessage(final ClientMessage cm, ReplyInfo ri,
			final SendParameters sp, final TailCall tc) {
		if (!isValidMessage(cm)) {
			return;
		}

		AsyncCallback<SendResponse> ac = new AsyncCallback<SendResponse>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("sendMessage failure", caught);
			}

			public void onSuccess(SendResponse result) {
				ui.getSpinner().stopSpinning();
				if (result.isOk()) {
					mc.clearComposer();
					tc.run();
					ui.selectTab(View.CONVERSATIONS);

					SentMailNotification smn = new SentMailNotification(ui);
					ui.notifyUser(smn, 20);
					storeSent(smn, cm, sp);
					String folderName = WebmailController.get().getSelector()
							.getCurrent().getName();
					ui.setCurrentFolder(new Folder(folderName));
					ui.getSidebar().setCurrentDefaultLinkStyle(
							ui.getSidebar().defaultLinks.get(folderName));
					ui.fetchConversations(folderName, 1);
				} else {
					ui.notifyUser(new Label(I18N.strings.smtpError(result
							.getReason())), 15);
				}
			}
		};

		ui.getSpinner().startSpinning();
		AjaxCall.send.sendMessage(cm, ri, sp, ac);
	}

	private boolean e(String s) {
		return s == null || s.trim().length() == 0;
	}

	private boolean isValidMessage(ClientMessage cm) {
		if (e(cm.getSubject())) {
			ui.notifyUser(I18N.strings.emptySubject());
			return false;
		}
		if (cm.getTo() == null || cm.getTo().isEmpty()) {
			ui.notifyUser(I18N.strings.emptyRecipient());
			return false;
		}

		return true;
	}

	private void storeSent(final SentMailNotification smn,
			final ClientMessage cm, SendParameters sp) {
		if (cm == null) {
			GWT.log("NULL message for store sent !!!!!!!!!!!!!",
					new NullPointerException());
			return;
		}
		AsyncCallback<ConversationId> ac = new AsyncCallback<ConversationId>() {
			public void onFailure(Throwable caught) {
				ui.log("storeSentMessage failure");
			}

			public void onSuccess(ConversationId convId) {
				smn.setStored(convId);
			}
		};
		AjaxCall.store.storeSentMessage(cm, sp, ac);
	}

}
