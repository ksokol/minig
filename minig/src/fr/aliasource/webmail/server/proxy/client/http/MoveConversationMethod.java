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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Stores a message on the server in the given folder
 * 
 * @author tom
 * 
 */
public class MoveConversationMethod extends AbstractMessageMethod {

	private String token;

	MoveConversationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/moveConversation.do");
		this.token = token;
	}

	private String join(List<ConversationId> l, char sep) {
		StringBuilder ret = new StringBuilder(10 * l.size());
		int i = 0;
		for (ConversationId s : l) {
			if (i > 0) {
				ret.append(sep);
			}
			ret.append(s.getConversationId());
			i++;
		}
		return ret.toString();
	}

	public List<ConversationId> moveConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId, boolean isMove) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		
		try {
			params.put("move", "" + isMove);
			params.put("conversations", join(conversationId, ','));
			params.put("destination", destination.getName());
			Document doc = execute(params);
			String[] convIdsAsStrings = parseConversationIds(doc.getDocumentElement());
			List<ConversationId> ret = new ArrayList<ConversationId>();	
			for (String convId: convIdsAsStrings) {
				ret.add(new ConversationId(convId));
			}
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public List<ConversationId> moveConversation(String query, Folder destination,
			boolean isMove) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		try {
			params.put("move", "" + isMove);
			params.put("query", query);
			params.put("destination", destination.getName());
			Document doc = execute(params);
			List<ConversationId> conversationIds = new ArrayList<ConversationId>();
			String[] convIds = parseConversationIds(doc.getDocumentElement());
			for (String convId: convIds) {
				conversationIds.add(new ConversationId(convId));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
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

}
