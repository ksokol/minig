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

package fr.aliasource.webmail.server.proxy.client.http.filters;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class FetchForwardMethod extends AbstractFilterMethod {

	protected String token;

	public FetchForwardMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/fetchForward.do");
		this.token = token;
	}

	public ForwardInfo fetchForward() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		if (logger.isDebugEnabled()) {
			DOMUtils.logDom(doc);
		}

		Element r = doc.getDocumentElement();
		ForwardInfo fi = new ForwardInfo();
		fi.setEnabled("true".equals(r.getAttribute("enabled")));
		fi.setEmail(r.getTextContent());
		fi.setLocalCopy("true".equals(r.getAttribute("localCopy")));

		return fi;
	}

}
