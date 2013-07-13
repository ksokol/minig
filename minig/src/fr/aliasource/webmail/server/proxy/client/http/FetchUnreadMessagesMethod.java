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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;

public class FetchUnreadMessagesMethod extends AbstractMessageMethod {

	private String token;

	FetchUnreadMessagesMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/fetchUnreadMessages.do");
		this.token = token;
	}

	public ConversationContent fetchUnreadMessages(ConversationId convId,
			String defaultTimeZone) {
		Map<String, String> params = paramsFrom(token);
		params.put("convId", convId.getConversationId());

		Document doc = execute(params);
		Conversation c = null;
		ClientMessage[] messages = null;
		if (doc != null) {
			Element root = doc.getDocumentElement();
			c = parseConv(convId, root);
			List<ClientMessage> cml = new LinkedList<ClientMessage>();
			parseMessageList(c, root, cml);
			messages = cml.toArray(new ClientMessage[cml.size()]);
		}

		ConversationContent cc = new ConversationContent(c, messages);

		return cc;
	}


	private Conversation parseConv(ConversationId convId, Element root) {
		Conversation c = new Conversation();
		c.setTitle(DOMUtils.getElementText(root, "title"));
		c.setId(convId);
		c.setUnread("false".equals(root.getAttribute("read")));
		return c;
	}

}
