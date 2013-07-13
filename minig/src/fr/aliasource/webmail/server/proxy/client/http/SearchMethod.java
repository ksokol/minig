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
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;

public class SearchMethod extends AbstractConversationMethod {

	SearchMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, token, backendUrl, "/search.do");
	}

	public ConversationReferenceList search(String query, int page,
			int pageLength) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("query", query);
		params.put("page", "" + page);
		params.put("pageLength", "" + pageLength);

		Document doc = execute(params);
		ConversationReferenceList ret = null;
		if (doc != null) {
			// if (logger.isDebugEnabled()) {
			// DOMUtils.logDom(doc);
			// }

			Element root = doc.getDocumentElement();
			NodeList crs = root.getElementsByTagName("cr");
			List<ConversationReference> cPage = new ArrayList<ConversationReference>(
					crs.getLength());
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < crs.getLength(); i++) {
				Element cr = (Element) crs.item(i);
				cPage.add(parseConversation(cal, cr));
			}
			ret = new ConversationReferenceList(cPage, Integer.parseInt(root
					.getAttribute("fullLength")));
		}
		return ret;
	}

}
