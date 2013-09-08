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
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.shared.ConversationId;

public class FlagsMethod extends AbstractClientMethod {

	private String token;

	FlagsMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/flags.do");
		this.token = token;
	}

	private String join(Set<ConversationId> l, char sep) {
		StringBuffer ret = new StringBuffer(10 * l.size());
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

	public void setFlag(Set<ConversationId> conversationIds, String flag) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		String joined = join(conversationIds, ',');
		params.put("conversations", joined);
		params.put("set", flag);

		executeVoid(params);
	}

	public void unsetFlag(Set<ConversationId> conversationIds, String flag) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		String joined = join(conversationIds, ',');
		params.put("conversations", joined);
		params.put("unset", flag);

		executeVoid(params);
	}

	public void setFlag(String query, String flag) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("query", query);
		params.put("set", flag);

		executeVoid(params);
	}

	public void unsetFlag(String query, String flag) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("query", query);
		params.put("unset", flag);

		executeVoid(params);
	}

}
