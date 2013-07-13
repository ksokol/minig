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
import org.w3c.dom.NodeList;

import com.google.gwt.core.client.GWT;

import fr.aliasource.webmail.server.ObmConfig;

public class SettingsMethod extends AbstractClientMethod {

	private String token;
	private HashMap<String, String> links;

	SettingsMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/settings.do");
		this.token = token;
		
		links = new HashMap<String, String>();
		ObmConfig oc = new ObmConfig();
		String eu = oc.getExternalUrl();
		links.put("header_links/calendar", eu+"calendar/calendar_index.php");
		links.put("header_links/contact", eu+"contact/contact_index.php");
		links.put("header_links/todo", eu+"todo/todo_index.php");
		links.put("header_links/document", eu+"document/document_index.php");
	}

	public Map<String, String> getSettings() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		Map<String, String> settings = new HashMap<String, String>();
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}
			Element root = doc.getDocumentElement();
			NodeList cats = root.getChildNodes();
			for (int i = cats.getLength() - 1; i >= 0; i--) {
				Element cat = (Element) cats.item(i);
				String catName = cat.getNodeName();
				NodeList vals = cat.getChildNodes();
				for (int j = vals.getLength() - 1; j >= 0; j--) {
					Element val = (Element) vals.item(j);
					StringBuilder key = new StringBuilder(50);
					key.append(catName);
					key.append('/');
					key.append(val.getNodeName());
					String k = key.toString();
					settings.put(k, val.getAttribute("value"));
					GWT.log("added setting " + k + " ("
							+ val.getAttribute("value") + ")", null);
				}
			}
		}
		
		settings.putAll(links);
		
		return settings;
	}

}
