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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;

public class AbstractConversationMethod extends AbstractClientMethod {

	protected String token;

	protected AbstractConversationMethod(HttpClient hc, String token,
			String backendUrl, String action) {
		super(hc, backendUrl, action);
		this.token = token;
	}

	protected ConversationReference parseConversation(Calendar cal, Element cr) {
		String title = DOMUtils.getElementText(cr, "title");

		Element ps = DOMUtils.getUniqueElement(cr, "participants");
		Set<EmailAddress> participants = new HashSet<EmailAddress>();
		String[][] psArray = DOMUtils.getAttributes(ps, "p", new String[] {
				"addr", "displayName" });
		for (int j = 0; j < psArray.length; j++) {
			participants.add(new EmailAddress(psArray[j][1], psArray[j][0]));
		}

		Element mids = DOMUtils.getUniqueElement(cr, "mids");
		String[][] mArray = DOMUtils.getAttributes(mids, "m",
				new String[] { "id" });
		List<MessageId> messageIds = new ArrayList<MessageId>(mArray.length);
		for (int j = 0; j < mArray.length; j++) {
			messageIds.add(new MessageId(Long.valueOf(mArray[j][0])));
		}

		ConversationReference c = new ConversationReference();
		c.setParticipants(participants);
		c.setMessageIds(messageIds);
		c.setTitle(title);
		c.setRead("true".equals(cr.getAttribute("read")));
		c.setHasAttachements("true".equals(cr.getAttribute("attach")));
		c.setHasInvitation("true".equals(cr.getAttribute("invitation")));
		c.setStarred("true".equals(cr.getAttribute("star")));
		c.setAnswered("true".equals(cr.getAttribute("answer")));
		c.setHighPriority("true".equals(cr.getAttribute("hp")));
		cal.setTimeInMillis(Long.parseLong(cr.getAttribute("lastMessageDate")));
		c.setLastMessageDate(cal.getTime());
		c.setSourceFolderName(cr.getAttribute("folder"));
		c.setId(new ConversationId(cr.getAttribute("id")));

		if (cr.hasAttribute("prev")) {
			c.setPrev(new ConversationId(cr.getAttribute("prev")));
		}
		if (cr.hasAttribute("next")) {
			c.setNext(new ConversationId(cr.getAttribute("next")));
		}

		parseMetadata(c, cr);
		return c;
	}

	private void parseMetadata(ConversationReference c, Element cr) {
		NodeList metaL = cr.getElementsByTagName("meta");
		Map<String, String> metas = new HashMap<String, String>();
		for (int i = metaL.getLength() - 1; i >= 0; i--) {
			Element m = (Element) metaL.item(i);
			metas.put(m.getAttribute("type"), DOMUtils.getElementText(m));
		}

		String preview = metas.get("preview");
		if (preview != null) {
			c.setPreview(preview);
		} else {
			c.setPreview("no preview available");
		}
		if (metas.containsKey("html")) {
			c.setHtml(metas.get("html"));
		}
	}
}
