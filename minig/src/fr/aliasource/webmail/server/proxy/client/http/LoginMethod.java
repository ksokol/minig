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

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

public class LoginMethod extends AbstractClientMethod {

	protected LoginMethod(HttpClient hc, String backendUrl) {
		super(hc, backendUrl, "/login.do");
	}

	public String login(String login, String domain, String password) {
		String token = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("login", login);
		if (domain != null) {
			params.put("domain", domain);
		}
		params.put("password", password);
		Document doc = execute(params);
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}
			token = doc.getDocumentElement().getAttribute("value");
		}
		return token;
	}

}
