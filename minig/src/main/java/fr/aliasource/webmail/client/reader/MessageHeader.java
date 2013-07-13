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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.conversations.StarWidget;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.EmailAddress;

/**
 * Renders the rounded header on top of messages in conversation reader.
 * 
 * @author tom
 * 
 */
public class MessageHeader extends FlexTable {

	private Anchor showDetails;

	public MessageHeader(View ui, DateFormatter dtf,
			RecipientsStyleHandler rsh, ClientMessage cm,
			ConversationDisplay convDisplay, final AbstractMessageWidget mw,
			boolean withMenu, boolean collapsable) {

		addStyleName("singleMessageHeader");
		setWidth("100%");

		int col = 0;

		GWT.log("withMenu: " + withMenu + " collapsable: " + collapsable, null);

		if (withMenu) {
			if (cm.isHighPriority()) {
				Widget hp = createHighPriority();
				setWidget(0, col++, hp);
			}

			Widget star = createStar(ui, cm);
			setWidget(0, col++, star);

			if (cm.isAnswered()) {
				Widget answer = createAnswer();
				setWidget(0, col++, answer);
			}
		}

		Widget recipients = createRecipientsWidget(rsh, cm, mw, withMenu);
		setWidget(0, col++, recipients);
		CellFormatter cf = getCellFormatter();
		cf.addStyleName(0, col - 1, "recipientsCol");
		recipients.addStyleName("recipients");

		showDetails = new Anchor(I18N.strings.showDetail());
		setWidget(0, col++, showDetails);
		showDetails.addStyleName("recipientsDetails");

		Label date = new Label(dtf.formatPretty(cm.getDate()));
		date.addStyleName("noWrap");
		setWidget(0, col++, date);
		date.addStyleName("recipientsDate");

		if (withMenu) {
			MessageMenu menu = new MessageMenu(cm, convDisplay);
			setWidget(0, col++, menu);
		}
	}

	private Widget createHighPriority() {
		return null;
	}

	private Widget createAnswer() {
		return new AnsweredWidget();
	}

	private Widget createStar(View ui, ClientMessage cm) {
		StarWidget sw = new StarWidget(cm.isStarred(), cm.getConvId());
		return sw;
	}

	private Widget createRecipientsWidget(RecipientsStyleHandler rsh,
			final ClientMessage cm, final AbstractMessageWidget mw,
			boolean isClickable) {
		StringBuilder html = new StringBuilder(150);
		html.append("<span class=\"bold noWrap ");
		html.append(rsh.getStyle(cm.getSender()));
		html.append("\">");
		html.append(cm.getSender().getDisplay());
		html.append("</span>&nbsp;" + I18N.strings.to().toLowerCase()
				+ "&nbsp;");

		int col = addRecips(rsh, html, cm.getTo(), 0);
		if (cm.getCc() != null) {
			col = addRecips(rsh, html, cm.getCc(), col);
		}
		if (cm.getBcc() != null) {
			col = addRecips(rsh, html, cm.getBcc(), col);
		}

		HTML h = new HTML(html.toString());
		if (isClickable) {
			h.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
						mw.setOpen(!mw.isOpen());
				}
			});
		}
		return h;
	}

	private int addRecips(RecipientsStyleHandler rsh, StringBuilder html,
			List<EmailAddress> al, int col) {
		int c = col;
		for (int i = 0; i < al.size(); i++) {

			EmailAddress a = al.get(i);
			if (c++ > 0) {
				html.append("&nbsp;");
			}
			html.append("<span class=\"noWrap ");
			html.append(rsh.getStyle(a));
			html.append("\">");
			String lbl = a.getDisplay();
			if (lbl == null || lbl.trim().length() == 0) {
				lbl = a.getEmail();
			}
			html.append(lbl);
			html.append("</span>");

			i++;
		}
		return c;
	}

	public Anchor getShowDetailsLink() {
		return showDetails;
	}

	public void destroy() {
	}

}
