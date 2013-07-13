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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Notification displayed on when a message is sent.
 * 
 * @author tom
 * 
 */
public class SentMailNotification extends HorizontalPanel {

	private Label text;
	private View ui;

	public SentMailNotification(View ui) {
		super();
		this.ui = ui;
		text = new Label(I18N.strings.messageSent());
		add(text);
	}

	/**
	 * Informs the notification that the storage in the 'sent' folder was
	 * successful.
	 */
	public void setStored(final ConversationId conversationId) {
		text.setText(I18N.strings.messageSent());
		add(new HTML("&nbsp;"));
		Anchor viewSent = new Anchor(I18N.strings.viewSent());
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent ev) {
				ui.setCurrentFolder(new Folder(WebmailController.get()
						.getSetting(GetSettings.SENT_FOLDER), I18N.strings
						.sent()));
				ui.log("showConv("
						+ WebmailController.get().getSelector().getCurrent()
								.getName() + ", " + conversationId + ")");
				ui.getSidebar().setCurrentDefaultLinkStyle(
						ui.getSidebar().defaultLinks.get(WebmailController
								.get().getSelector().getCurrent().getName()
								.toLowerCase()));
				ui.showConversation(new Folder(WebmailController.get()
						.getSetting(GetSettings.SENT_FOLDER)), conversationId,
						1);
			}
		};
		viewSent.addClickHandler(cl);
		add(viewSent);
	}

}
