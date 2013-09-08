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

import fr.aliasource.webmail.server.proxy.client.Completion;

public class CompletionMethod extends AbstractClientMethod {

	private String token;

	CompletionMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/completion.do");
		this.token = token;
	}

	public List<Completion> complete(String type, String query, int limit) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("type", type);
		params.put("query", query);
		params.put("limit", "" + limit);

		Document doc = execute(params);
		List<Completion> ret = null;
		if (doc != null) {
			// if (logger.isDebugEnabled()) {
			// DOMUtils.logDom(doc);
			// }

			Element root = doc.getDocumentElement();
			String[][] cs = DOMUtils.getAttributes(root, "c", new String[] {
					"d", "v" });
			ret = new ArrayList<Completion>(cs.length);
			for (int i = 0; i < cs.length; i++) {
				ret.add(new Completion(cs[i][1], cs[i][0]));
			}
		}
		return ret;
	}

}
