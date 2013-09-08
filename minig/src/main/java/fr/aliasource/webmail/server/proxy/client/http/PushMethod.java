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

import fr.aliasource.webmail.client.shared.ServerEventKind;

public class PushMethod extends AbstractClientMethod {

	private String token;

	protected PushMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/push");
		this.token = token;
	}

	public ServerEventKind fetchServerEvent() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		Document doc = execute(params);
		DOMUtils.logDom(doc);
		String event = DOMUtils.getElementText(doc.getDocumentElement(),
				"value");
		logger.info("[" + token + "] received event: " + event);
		return ServerEventKind.valueOf(event);
	}

}
