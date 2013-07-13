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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;

public class FindConversationMethod extends AbstractConversationMethod {

	FindConversationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, token, backendUrl, "/findConversation.do");
	}

	public ConversationReference findConversation(ConversationId conversationId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("convId", conversationId.getConversationId());

		Document doc = execute(params);
		ConversationReference ret = null;
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			Element root = doc.getDocumentElement();
			Element cr = DOMUtils.getUniqueElement(root, "cr");
			Calendar cal = Calendar.getInstance();
			ret = parseConversation(cal, cr);
			if (logger.isDebugEnabled()) {
				logger.debug("returning parsed conversation " + ret);
			}
		}
		return ret;
	}

}
