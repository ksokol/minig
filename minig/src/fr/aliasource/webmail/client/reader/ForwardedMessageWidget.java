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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.shared.ClientMessage;

/**
 * Display a single mail message
 * 
 * @author tom
 * 
 */
public class ForwardedMessageWidget extends AbstractMessageWidget {

	/**
	 * MessageWidget constructor : without reply widget (for forwarded messages)
	 */
	public ForwardedMessageWidget(View ui, ConversationDisplay convDisp,
			DateFormatter df, ClientMessage cm, RecipientsStyleHandler rsh,
			boolean clickableHeader) {
		this.ui = ui;
		this.rsh = rsh;
		this.cm = cm;

		dp = new VerticalPanel();
		dp.setWidth("100%");

		Label title = new Label(I18N.strings.forwardedMessage() + " - "
				+ cm.getSubject());
		title.setStyleName("header");

		title.addClickHandler(new ClickHandler() {
			private boolean open;

			public void onClick(ClickEvent ev) {
				if (this.open) {
					dp.remove(header);
					dp.remove(content);
				} else {
					dp.add(header);
					dp.add(content);
				}
				this.open = !this.open;
			}
		});

		createHeader(df, rsh, cm, clickableHeader, false);
		createContent(cm, df);

		dp.add(title);
		dp.addStyleName("content");

		setWidth("100%");
		add(dp);
	}

	/**
	 * Forward message content : without reply widget & non-clickable header
	 */
	private Widget createContent(ClientMessage cm, DateFormatter df) {
		content = new VerticalPanel();
		content.setWidth("100%");

		addShowDetailsHandler(header.getShowDetailsLink());

		details = createMessageDetails(cm, df, rsh);
		content.add(details);

		createMessage(cm);

		if (cm.getFwdMessages() != null) {
			for (ClientMessage forwardedMessage: cm.getFwdMessages()) {
				AbstractMessageWidget mw = createForwardedMessageWidget(df, forwardedMessage);
				mw.setStyleName("forwardMessage");
				content.add(mw);
			}
		}

		content.add(createAttachmentsList());

		content.addStyleName("singleMessageBody");
		return content;
	}

}
