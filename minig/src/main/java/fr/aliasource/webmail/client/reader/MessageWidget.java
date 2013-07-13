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

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.reader.invitation.InvitationPanel;
import fr.aliasource.webmail.client.shared.ClientMessage;

/**
 * Display a single mail message
 * 
 * @author tom
 * 
 */
public class MessageWidget extends AbstractMessageWidget {

	private DateFormatter df;

	/**
	 * MessageWidget constructor : with reply widget
	 */
	public MessageWidget(View ui, ConversationDisplay convDisp,
			DateFormatter df, ClientMessage cm, boolean showQuickReply,
			RecipientsStyleHandler rsh) {
		this.ui = ui;
		this.rsh = rsh;
		this.cm = cm;
		this.convDisp = convDisp;
		this.df = df;

		this.replyMode = false;
		this.dp = new VerticalPanel();
		dp.add(createHeader(df, rsh, cm, true, showQuickReply));

		if (cm.isLoaded()) {
			createContent(cm, showQuickReply, df);
		}

		setWidth("100%");
		add(dp);
		dp.setWidth("100%");
		lastMessage = false;

		GWT.log("cm.isLoaded: " + cm.isLoaded());
	}
	
	protected void createContent() {
		createContent(cm, false, df);
	}


	private Widget createContent(ClientMessage cm, boolean showQuickReply,
			DateFormatter df) {
		content = new VerticalPanel();
		content.setWidth("100%");

		addShowDetailsHandler(header.getShowDetailsLink());

		details = createMessageDetails(cm, df, rsh);
		content.add(details);
		GWT.log("cm.hasInvitation => " + cm.getHasInvitation(), null);
		if (cm.getHasInvitation()) {
			invitation = new InvitationPanel(ui, cm);
			content.add(invitation);
		}

		GWT.log("cm.dispositionNotification => " + cm.getDispositionNotification(), null);
		if (!cm.getDispositionNotification().isEmpty()) {
			dispositionNotification = new DispositionNotificationPanel(ui, cm);
			content.add(dispositionNotification);
		}
		
		createMessage(cm);

		if (cm.getFwdMessages() != null) {
			Iterator<ClientMessage> it = cm.getFwdMessages().iterator();
			while (it.hasNext()) {
				ClientMessage fwdCm = it.next();
				AbstractMessageWidget mw = createForwardedMessageWidget(df, fwdCm);
				mw.setStyleName("forwardMessage");
				content.add(mw);
			}
		}

		content.add(createAttachmentsList());

		createQuickReply(content, showQuickReply);
		content.addStyleName("singleMessageBody");
		return content;
	}

	private void createQuickReply(VerticalPanel dp, boolean textArea) {
		DockPanel replyZone = new DockPanel();
		ReplyLinkListener rll = new ReplyLinkListener(this, replyZone, dp);

		HorizontalPanel actions = new HorizontalPanel();
		Anchor reply = new Anchor(I18N.strings.reply());
		reply.addStyleName("replyLink");
		reply.addClickHandler(rll);
		actions.add(reply);
		Anchor replyAll = new Anchor(I18N.strings.replyToAll());
		reply.addStyleName("replyToAllLink");
		replyAll.addClickHandler(new ReplyAllLinkListener(this, replyZone, dp));
		actions.add(replyAll);
		Anchor forward = new Anchor(I18N.strings.forward());
		reply.addStyleName("forwardLink");
		forward.addClickHandler(new ForwardLinkListener(this, replyZone, dp));
		actions.add(forward);
		actions.setSpacing(4);
		replyZone.add(actions, DockPanel.NORTH);

		if (textArea) {
			TextArea ta = new TextArea();
			ta.setWidth("40em");
			ta.setVisibleLines(3);
			ta.addClickHandler(rll);
			replyZone.add(ta, DockPanel.CENTER);
			replyZone.setSpacing(3);
		}
		replyZone.setWidth("100%");
		replyZone.addStyleName("replyZone");
		dp.add(replyZone);

	}

}
