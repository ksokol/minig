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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.reader.invitation.InvitationPanel;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.EmailAddress;

/**
 * Display a single mail message
 * 
 * @author tom
 * 
 */
public abstract class AbstractMessageWidget extends VerticalPanel {

	protected VerticalPanel dp;
	protected VerticalPanel content;
	protected MessageHeader header;
	protected MessageActions ma;
	protected FlexTable details;
	protected InvitationPanel invitation;
	protected DispositionNotificationPanel dispositionNotification;
	protected boolean shown;

	protected boolean replyMode;
	protected View ui;
	protected ClientMessage cm;
	protected RecipientsStyleHandler rsh;
	protected boolean lastMessage;

	protected ConversationDisplay convDisp;

	public void setOpen(final boolean b) {
		if (lastMessage) {
			return;
		}
		if (!replyMode) {
			if (cm.isLoaded()) {
				setOpenStatus(b);
			} else if (b) {
				AsyncCallback<ClientMessage> callback = new AsyncCallback<ClientMessage>() {

					@Override
					public void onFailure(Throwable arg0) {
						GWT.log("error fetching one unloaded clientMessage", arg0);
					}

					@Override
					public void onSuccess(ClientMessage loaded) {
						mergeMessage(loaded);
						createContent();
						setOpenStatus(b);
					}
				};
				AjaxCall.sca.loadMessage(cm, callback);
			}
		}
	}
	
	protected void createContent() {
		
	}
	
	private void mergeMessage(ClientMessage loaded) {
		cm.setBody(loaded.getBody());
		cm.setAttachements(loaded.getAttachments());
		cm.setFwdMessage(loaded.getFwdMessages());
		cm.setSubject(loaded.getSubject());
		cm.setDispositionNotification(loaded.getDispositionNotification());
		cm.setLoaded(true);
	}

	private void setOpenStatus(boolean b) {
		if (b && dp.getWidgetIndex(content) < 0) {
			header.getShowDetailsLink().setVisible(true);
			dp.add(content);
		} else if (!b) {
			header.getShowDetailsLink().setVisible(false);
			if (content != null) {
				dp.remove(content);
			}
		}
	}

	public boolean isReplyMode() {
		return replyMode;
	}

	public void setReplyMode(boolean replyMode) {
		this.replyMode = replyMode;
	}

	public View getUi() {
		return ui;
	}

	public ClientMessage getMessage() {
		return cm;
	}

	public boolean isOpen() {
		return dp.getWidgetIndex(content) > 0;
	}

	public boolean isLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(boolean b) {
		this.lastMessage = b;
	}

	protected AbstractMessageWidget createForwardedMessageWidget(DateFormatter df,
			ClientMessage forwarded) {
		return new ForwardedMessageWidget(ui, convDisp, df, forwarded, rsh,
				false);
	}

	public void destroy() {
		cm = null;
		convDisp = null;
		header.destroy();
		if (ma != null) {
			ma.destroy();
		}
	}

	protected VerticalPanel showQuotedText(String body) {
		VerticalPanel newBody = new VerticalPanel();
		newBody.addStyleName("messageText");

		if (body == null) {
			return newBody;
		}

		if (body.contains("<table") || body.contains("<div")
				|| body.contains("<blockquote") || body.contains("<ul")) {
			newBody.add(new HTML(body));
			return newBody;
		}

		body = body.replace("<br>\n", "\n");
		body = body.replace("<BR>\n", "\n");
		body = body.replace("<br/>\n", "\n");
		body = body.replace("<BR/>\n", "\n");

		body = body.replace("<br>", "\n");
		body = body.replace("<BR>", "\n");
		body = body.replace("<br/>", "\n");
		body = body.replace("<BR/>", "\n");

		String[] lines = body.split("\n");
		StringBuilder quoted = new StringBuilder();
		StringBuilder text = new StringBuilder();

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("&gt;")) {
				quoted.append(lines[i]).append("<br/>");
				final DisclosurePanel quotedText = new DisclosurePanel();
				final Label quotedHeader = new Label("- "
						+ I18N.strings.showQuotedText() + " -");
				if (i + 1 < lines.length && !lines[i + 1].startsWith("&gt;")) {

					quotedHeader.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent ev) {
							if (!quotedText.isOpen()) {
								quotedHeader.setText("- "
										+ I18N.strings.hideQuotedText() + " -");
							} else {
								quotedHeader.setText("- "
										+ I18N.strings.showQuotedText() + " -");
							}
						}
					});

					quotedText.setHeader(quotedHeader);
					quotedText.setStyleName("quotedText");
					quotedText.add(new HTML(quoted.toString()));
					newBody.add(quotedText);
					quoted.delete(0, quoted.length());
				} else if (i + 1 == lines.length) {
					quotedText.setHeader(quotedHeader);
					quotedText.setStyleName("quotedText");
					quotedText.add(new HTML(quoted.toString()));
					newBody.add(quotedText);
					quoted.delete(0, quoted.length());
				}

			} else {
				text.append(lines[i]).append("<br/>");

				if (i + 1 < lines.length && lines[i + 1].startsWith("&gt;")) {
					newBody.add(new HTML(text.toString()));
					text.delete(0, text.length());
				} else {
					if (text.length() > 0) {
						newBody.add(new HTML(text.toString()));
						text.delete(0, text.length());
					}
				}
			}
		}

		return newBody;
	}

	protected void addRecips(RecipientsStyleHandler rsh, StringBuilder html,
			List<EmailAddress> al) {
		GWT.log("AddRecips(address size: " + al.size() + ")", null);
		for (int j = 0; j < al.size(); j++) {
			EmailAddress a = al.get(j);
			if (j > 0) {
				html.append("<br>");
			}
			html.append("<span class=\"noWrap ");
			html.append(rsh.getStyle(a));
			html.append("\">");
			String lbl = a.getEmail();
			if (!a.getDisplay().trim().isEmpty()) {
				lbl = a.getDisplay() + "&nbsp;&lt;" + a.getEmail() + "&gt;";
			}
			html.append(lbl);
			GWT.log("append(lbl: " + lbl + ")", null);
			html.append("</span>");
		}
	}

	protected void addShowDetailsHandler(Anchor hl) {
		hl.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent ev) {
				shown = !shown;
				updateLink(shown);
				details.setVisible(shown);
			}

		});
	}

	protected void updateLink(boolean shown) {
		if (shown) {
			header.getShowDetailsLink().setText(I18N.strings.hideDetail());
		} else {
			header.getShowDetailsLink().setText(I18N.strings.showDetail());
		}
	}

	protected Anchor createClippedAnchor() {
		Anchor download = new Anchor(I18N.strings.clippedMessage(), false,
				"downloadEml?emlId=" + cm.getUid() + "&folderName="
						+ cm.getFolderName());
		download.getElement().setAttribute("target", "_blank");
		return download;
	}

	protected Widget createAttachmentsList() {
		final VerticalPanel vp = new VerticalPanel();
		AsyncCallback<AttachmentMetadata[]> meta = new AsyncCallback<AttachmentMetadata[]>() {
			public void onFailure(Throwable caught) {
				ui.log("failure loading attachments", caught);
			}

			public void onSuccess(AttachmentMetadata[] metas) {
				for (int i = 0; i < metas.length; i++) {
					AttachmentMetadata am = metas[i];
					vp.add(new AttachmentDisplay(am, cm.getAttachments()[i]));
				}
			}
		};

		if (cm.getAttachments().length > 0) {
			AjaxCall.atMgr.getAttachementMetadata(cm.getAttachments(), meta);
		}
		return vp;
	}

	protected void createMessage(ClientMessage cm) {
		String html = cm.getBody().getPartialCleanHtml();

		if (html == null && cm.getBody().getHtml() != null) {
			html = cm.getBody().getHtml();
		} else if (html == null && !cm.getHasInvitation()) {
			GWT.log("no html version of the body", null);
			html = "<p>no html version of the body found in message.</p>";
		}
		if (html != null) {
			if (html.contains("<img") && !cm.getBody().isTruncated()) {
				ma = new MessageActions(cm);
				content.add(ma);
			} else {
				VerticalPanel vp = showQuotedText(html);
				if (cm.getBody().isTruncated()) {
					vp.add(createClippedAnchor());
				}
				content.add(vp);
			}

		}
	}

	protected FlexTable createMessageDetails(ClientMessage cm,
			DateFormatter df, RecipientsStyleHandler rsh) {
		shown = false;

		Map<String, Widget> md = new LinkedHashMap<String, Widget>();

		Label from = new Label(cm.getSender().getDisplay() + " <"
				+ cm.getSender().getEmail() + ">");
		// sender color
		if (rsh.getStyle(cm.getSender()) != null) {
			from.addStyleName(rsh.getStyle(cm.getSender()));
		}
		from.addStyleName("messageSenderLabel");
		from.addStyleName("bold");
		md.put(I18N.strings.from(), from);

		StringBuilder html = null;
		html = new StringBuilder(150);
		addRecips(rsh, html, cm.getTo());
		md.put(I18N.strings.to(), new HTML(html.toString()));

		if (cm.getCc() != null && !cm.getCc().isEmpty()) {
			html = new StringBuilder(150);
			addRecips(rsh, html, cm.getCc());
			md.put(I18N.strings.cc(), new HTML(html.toString()));
		}
		if (cm.getBcc() != null && !cm.getBcc().isEmpty()) {
			html = new StringBuilder(150);
			addRecips(rsh, html, cm.getBcc());
			md.put(I18N.strings.bcc(), new HTML(html.toString()));
		}

		Label date = new Label(df.formatDetails(cm.getDate()));
		md.put(I18N.strings.date(), date);

		Label subject = new Label(cm.getSubject());
		md.put(I18N.strings.subject(), subject);

		String mailer = cm.getMailer();
		if (mailer != null && mailer.length() > 0) {
			Label mailBy = new Label(mailer);
			md.put(I18N.strings.mailby(), mailBy);
		}

		Set<String> keys = md.keySet();
		Iterator<String> it = keys.iterator();

		FlexTable ft = new FlexTable();
		ft.setStyleName("messageDetails");

		int i = 0;
		while (it.hasNext()) {
			String key = (String) (it.next());
			Widget value = md.get(key);
			ft.setText(i, 0, key);
			ft.setWidget(i, 1, value);
			ft.getCellFormatter().setStyleName(i, 0, "keys");
			i++;
		}

		ft.setVisible(shown);

		return ft;
	}

	protected Widget createHeader(DateFormatter df, RecipientsStyleHandler rsh,
			ClientMessage cm, boolean isClickable, boolean menuAvailable) {
		header = new MessageHeader(ui, df, rsh, cm, convDisp, this,
				isClickable, menuAvailable);
		return header;
	}

}
