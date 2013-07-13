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

package fr.aliasource.webmail.server.proxy.client.http.folder;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class UnsubscribeMethod extends AbstractFolderMethod {

	private String token;

	public UnsubscribeMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/unsubscribe.do");
		this.token = token;
	}

	public void unsubscribe(Folder f) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("folder", f.getName());
		Document doc = null;
		try {
			doc = getFolderAsXML(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			params.put("xmlfolder", out.toString());
			executeVoid(params);

			if (logger.isInfoEnabled()) {
				logger.info("folder unsubscribed !");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
