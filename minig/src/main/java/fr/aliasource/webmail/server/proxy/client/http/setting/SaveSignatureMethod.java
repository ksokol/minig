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

package fr.aliasource.webmail.server.proxy.client.http.setting;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.server.proxy.client.http.AbstractClientMethod;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class SaveSignatureMethod extends AbstractClientMethod {

	private String token;

	public SaveSignatureMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/saveSignature.do");
		this.token = token;
	}

	public void saveSignature(Map<String, String> identities) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = null;
		try {
			doc = getSignaturesAsXML(identities);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			params.put("xmlsignature", out.toString());
			executeVoid(params);

			if (logger.isInfoEnabled()) {
				logger.info("signature saved !");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected Document getSignaturesAsXML(Map<String, String> addrs)
			throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = DOMUtils.createDoc(
				"http://obm.aliasource.fr/xsd/signature_list", "signatureList");
		Element root = doc.getDocumentElement();
		for (Entry<String, String> addr : addrs.entrySet()) {
			Element fe = DOMUtils.createElement(root, "signature");
			fe.setAttribute("email", addr.getKey());
			fe.setTextContent(addr.getValue());
		}
		return doc;
	}
}