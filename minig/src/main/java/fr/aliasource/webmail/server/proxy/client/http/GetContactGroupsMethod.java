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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ContactGroup;

public class GetContactGroupsMethod extends AbstractClientMethod {

	private String token;

	public GetContactGroupsMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/contactGroups.do");
		this.token = token;
	}

	public ContactGroup[] getContactGroups() {
		List<ContactGroup> cgs = new LinkedList<ContactGroup>();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			Element root = doc.getDocumentElement();
			String[][] attrs = DOMUtils.getAttributes(root, "cg", new String[] {
					"id", "d", "count" });

			for (int i = 0; i < attrs.length; i++) {
				ContactGroup cg = new ContactGroup(attrs[i][0], attrs[i][1]);
				cg.setSize(Integer.parseInt(attrs[i][2]));
				cgs.add(cg);
			}
		}

		return cgs.toArray(new ContactGroup[cgs.size()]);
	}
}
