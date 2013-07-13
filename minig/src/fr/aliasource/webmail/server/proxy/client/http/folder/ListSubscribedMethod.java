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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.http.AbstractClientMethod;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class ListSubscribedMethod extends AbstractClientMethod {

	private String token;

	public ListSubscribedMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/listSubscribed.do");
		this.token = token;
	}

	public List<Folder> listSubscribed() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		List<Folder> ret = new LinkedList<Folder>();
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}
			String[][] values = DOMUtils.getAttributes(
					doc.getDocumentElement(), "folder", new String[] { "name",
							"displayName" , "shared"});
			for (int i = 0; i < values.length; i++) {
				ret.add(new Folder(values[i][0], values[i][1], true, Boolean.valueOf(values[i][2])));
			}
		}
		return ret;
	}

}
