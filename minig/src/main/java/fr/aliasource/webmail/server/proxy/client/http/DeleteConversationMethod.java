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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.shared.ConversationId;

/**
 * Deletes a conversation on the server
 * 
 * @author tom
 * 
 */
public class DeleteConversationMethod extends AbstractMessageMethod {

	private String token;

	DeleteConversationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/deleteConversation.do");
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

	public void deleteConversation(List<ConversationId> conversationId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		try {
			params.put("conversations", join(conversationId, ','));
			executeVoid(params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
