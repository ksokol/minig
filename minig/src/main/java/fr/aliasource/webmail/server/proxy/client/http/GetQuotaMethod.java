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
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.QuotaInfo;

public class GetQuotaMethod extends AbstractClientMethod {

	private String token;

	protected GetQuotaMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/getQuota.do");
		this.token = token;
	}

	public QuotaInfo getQuota(String mailBox) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("mailBox", mailBox);

		Document doc = execute(params);
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}
			Element root = doc.getDocumentElement();
			boolean enable = Boolean.valueOf(DOMUtils.getElementText(root,
					"enable"));
			int usage = Integer
					.parseInt(DOMUtils.getElementText(root, "usage"));
			int limit = Integer
					.parseInt(DOMUtils.getElementText(root, "limit"));

			return new QuotaInfo(enable, usage, limit);
		}

		return new QuotaInfo();
	}
}
