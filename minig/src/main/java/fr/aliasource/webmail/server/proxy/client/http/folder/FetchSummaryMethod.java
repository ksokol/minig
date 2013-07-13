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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.server.proxy.client.http.AbstractClientMethod;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

/**
 * Backend call to fetch the mailbox unread count summary
 * 
 * @author tom
 * 
 */
public class FetchSummaryMethod extends AbstractClientMethod {

	private String token;

	private static final String[] COLUMNS = new String[] { "name",
			"displayName", "unread" };

	public FetchSummaryMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/summary.do");
		this.token = token;
	}

	public List<CloudyFolder> fetchSummary() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		List<CloudyFolder> ret = null;
		Document doc = execute(params);
		if (doc != null) {
			String[][] values = DOMUtils.getAttributes(
					doc.getDocumentElement(), "folder", COLUMNS);
			ret = new ArrayList<CloudyFolder>(values.length);
			for (int i = 0; i < values.length; i++) {
				ret.add(new CloudyFolder(values[i][0], values[i][1], Integer
						.parseInt(values[i][2])));
			}
		} else {
			logger.warn("fetchSummary returned null document");
		}
		return ret;
	}
}
