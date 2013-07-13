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

package fr.aliasource.webmail.server.proxy.client.http;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.WordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.SendParameters;
import fr.aliasource.webmail.server.HTMLToPlainConverter;

public abstract class AbstractMessageMethod extends AbstractClientMethod {

	protected AbstractMessageMethod(HttpClient hc, String backendUrl,
			String action) {
		super(hc, backendUrl, action);
	}

	protected String[] parseConversationIds(Element root) {
		String[][] ids = DOMUtils.getAttributes(root, "c",
				new String[] { "id" });
		String[] ret = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			ret[i] = ids[i][0];
		}
		return ret;
	}

	protected ClientMessage parseMessage(Calendar cal, Element me) {

		String subject = DOMUtils.getElementText(me, "subject");
		cal.setTimeInMillis(Long.parseLong(me.getAttribute("date")));
		Date d = cal.getTime();
		String[][] sender = DOMUtils.getAttributes(me, "from", new String[] {
				"addr", "displayName" });
		EmailAddress from = new EmailAddress(sender[0][1], sender[0][0]);

		List<EmailAddress> to = new LinkedList<EmailAddress>();
		Element recip = DOMUtils.getUniqueElement(me, "to");
		loadRecipient(to, recip);

		List<EmailAddress> cc = new LinkedList<EmailAddress>();
		recip = DOMUtils.getUniqueElement(me, "cc");
		loadRecipient(cc, recip);

		List<EmailAddress> bcc = new LinkedList<EmailAddress>();
		recip = DOMUtils.getUniqueElement(me, "bcc");
		loadRecipient(bcc, recip);

		List<EmailAddress> dispositionNotification = new LinkedList<EmailAddress>();
		recip = DOMUtils.getUniqueElement(me, "disposition-notification");
		if (recip != null) {
			loadRecipient(dispositionNotification, recip);
		}
		
		Element attachements = DOMUtils.getUniqueElement(me, "attachements");

		String[][] aIds = DOMUtils.getAttributes(attachements, "a",
				new String[] { "id" });
		int aCount = aIds.length;
		String[] attachs = new String[aCount];
		for (int i = 0; i < aCount; i++) {
			attachs[i] = aIds[i][0];
		}
		Body body = parseBody(me);

		ClientMessage cm = new ClientMessage(from, to, subject, body, attachs, d, null, null);

		if (body.isEmpty()) {
			cm.setLoaded(false);
		}

		cm.setUid(new MessageId(Long.valueOf(me.getAttribute("uid"))));
		cm.setRead("true".equals(me.getAttribute("read")));
		cm.setStarred("true".equals(me.getAttribute("starred")));
		cm.setAnswered("true".equals(me.getAttribute("answered")));
		cm.setHighPriority("true".equals(me.getAttribute("hp")));
		cm.setCc(cc);
		cm.setBcc(bcc);
		cm.setDispositionNotification(dispositionNotification);

		return cm;
	}

	private void loadRecipient(List<EmailAddress> sa, Element recip) {
		String[][] ra = DOMUtils.getAttributes(recip, "r", new String[] {
				"addr", "displayName" });
		for (int i = 0; i < ra.length; i++) {
			sa.add(new EmailAddress(ra[i][1], ra[i][0]));
		}
	}

	protected Body parseBody(Element me) {
		Body b = new Body();

		NodeList nlist = me.getChildNodes();

		for (int i = 0; i < nlist.getLength(); i++) {
			if ("body".equals(nlist.item(i).getNodeName())) {
				Element body = (Element) nlist.item(i);
				String type = body.getAttribute("type");
				if ("text/plain".equals(type)) {
					b.setPlain(DOMUtils.getElementText(body));
				} else if ("text/html".equals(type)) {
					b.setHtml(DOMUtils.getElementText(body));
				} else if ("text/cleanHtml".equals(type)) {
					b.setCleanHtml(DOMUtils.getElementText(body));
				} else if ("text/partialCleanHtml".equals(type)) {
					b.setPartialCleanHtml(DOMUtils.getElementText(body));
				} else if ("text/truncatedPlain".equals(type)) {
					b.setPlain(DOMUtils.getElementText(body));
					b.setHtml(DOMUtils.getElementText(body));
					b.setTruncated(true);
				}
			}
		}

		return b;
	}

	protected Map<String, String> createMapParamsFromSendParameters(SendParameters sp) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("hp", "" + sp.isHighPriority());
		params.put("disposition-notification", "" + sp.isAskForDispositionNotification());
		params.put("encrypt", "" + sp.isEncrypt());
		params.put("sign", "" + sp.isSign());
		return params;
	}
	
	protected Document getMessageAsXML(ClientMessage m, SendParameters sp)
			throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = DOMUtils.createDoc(
				"http://obm.aliasource.fr/xsd/messages", "messages");
		Element root = doc.getDocumentElement();
		Element me = DOMUtils.createElement(root, "m");
		Date d = m.getDate();
		if (d == null) {
			d = new Date();
		}
		me.setAttribute("date", "" + d.getTime());

		DOMUtils.createElementAndText(me, "subject", m.getSubject());

		if (m.getMessageId() != null) {
			DOMUtils.createElementAndText(me, "messageId", m.getMessageId().getConversationId());
		}
		Element from = DOMUtils.createElement(me, "from");
		from.setAttribute("addr", m.getSender().getEmail());
		from.setAttribute("displayName", m.getSender().getDisplay());

		Element recipients = DOMUtils.createElement(me, "to");
		List<EmailAddress> recip = m.getTo();
		addRecips(recipients, recip);
		recipients = DOMUtils.createElement(me, "cc");
		recip = m.getCc();
		addRecips(recipients, recip);
		recipients = DOMUtils.createElement(me, "bcc");
		recip = m.getBcc();
		addRecips(recipients, recip);

		Element attachements = DOMUtils.createElement(me, "attachements");
		String[] attachIds = m.getAttachments();
		for (String attach : attachIds) {
			Element ae = DOMUtils.createElement(attachements, "a");
			ae.setAttribute("id", attach);
		}

		Element body = DOMUtils.createElement(me, "body");
		body.setAttribute("type", "text/plain");

		if (sp.isSendPlainText() && m.getBody().getHtml() != null) {
			// we do this server side as firefox richtextarea::getText() seems
			// to drop carriage returns.
			body.setTextContent(new HTMLToPlainConverter().convert(m.getBody()
					.getHtml()));
		} else {
			String plain = m.getBody().getPlain();
			StringBuilder sb = new StringBuilder();
			String[] lines = plain.split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].startsWith("> ")) {
					sb.append(lines[i]).append("\n");
				} else {
					sb.append(WordUtils.wrap(lines[i], 80)).append("\n");
				}
			}
			body.setTextContent(sb.toString());
		}

		if (!sp.isSendPlainText()) {
			if (m.getBody().getHtml() != null) {
				body = DOMUtils.createElementAndText(me, "body", m.getBody()
						.getHtml());
				body.setAttribute("type", "text/html");
			}

			if (m.getBody().getCleanHtml() != null) {
				body = DOMUtils.createElementAndText(me, "body", m.getBody()
						.getCleanHtml());
				body.setAttribute("type", "text/cleanHtml");
			}

			if (m.getBody().getPartialCleanHtml() != null) {
				body = DOMUtils.createElementAndText(me, "body", m.getBody()
						.getPartialCleanHtml());
				body.setAttribute("type", "text/partialCleanHtml");
			}
		}

		return doc;
	}

	protected void parseMessageList(Conversation c, Element root,
			List<ClientMessage> cml) {
		NodeList mnl = root.getElementsByTagName("m");
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < mnl.getLength(); i++) {
			Element m = (Element) mnl.item(i);
			ClientMessage cMess = parseMessage(cal, m);
			cMess.setConvId(c.getId());
			
			if (cMess.isLoaded()) {
				Element invitation = DOMUtils.getUniqueElement(
						(Element) mnl.item(i), "invitation");
				if (invitation != null) {
					cMess.setHasInvitation("true".equals(invitation
							.getTextContent()));
				}

				fetchFwdMessage(cMess, cal, m);
			}
			cml.add(cMess);
			cMess.setFolderName(c.getSourceFolder());
		}
	}

	protected void fetchFwdMessage(ClientMessage parentMessage, Calendar cal,
			Element element) {
		NodeList nlist = element.getChildNodes();
		for (int i = 0; i < nlist.getLength(); i++) {
			if ("fwd".equals(nlist.item(i).getNodeName())) {
				ClientMessage fwdMess = parseMessage(cal,
						(Element) nlist.item(i));
				parentMessage.addFwdMessage(fwdMess);
				fetchFwdMessage(fwdMess, cal, (Element) nlist.item(i));
			}
		}
	}

	private void addRecips(Element recipients, List<EmailAddress> recip) {
		for (EmailAddress a : recip) {
			Element p = DOMUtils.createElement(recipients, "r");
			p.setAttribute("displayName", a.getDisplay());
			String ma = a.getEmail();
			p.setAttribute("addr", ma);
		}
	}
}
