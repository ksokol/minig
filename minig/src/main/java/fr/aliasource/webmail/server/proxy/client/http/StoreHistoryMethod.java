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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.chat.History;
import fr.aliasource.webmail.client.shared.chat.HistoryItem;

public class StoreHistoryMethod extends AbstractClientMethod {

	protected String token;

	public StoreHistoryMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/saveChatHistory.do");
		this.token = token;
	}

	public void store(History history) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = null;
		try {
			doc = asXml(history);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			params.put("history", out.toString());

			executeVoid(params);
		} catch (Exception e) {
			logger.error("Error saving history", e);
		}
	}

	private Document asXml(History history)
			throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = DOMUtils.createDoc("http://minig.org/xsd/history.xsd",
				"history");
		Element root = doc.getDocumentElement();

		for (HistoryItem it : history) {
			Element item = DOMUtils.createElement(root, "item");
			item.setAttribute("ts", "" + it.getTimestamp().getTime());
			item.setAttribute("from", it.getFrom());
			item.setTextContent(it.getText());
		}

		root.setAttribute("ts", "" + history.getLastChat().getTime());
		return doc;
	}

}
