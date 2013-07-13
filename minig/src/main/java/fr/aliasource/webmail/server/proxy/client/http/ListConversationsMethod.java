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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.rpc.UseCachedData;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;

public class ListConversationsMethod extends AbstractConversationMethod {

	// private String cachedFolderVersionPage;

	ListConversationsMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, token, backendUrl, "/listConversations.do");
		// this.cachedFolderVersionPage = "inbox/-1/1";
	}

	public ConversationReferenceList listConversations(long lastSeenVersion,
			Folder folder, int page, int pageLength) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("folder", folder.getName());
		params.put("page", "" + page);
		params.put("pageLength", "" + pageLength);

		// long versionToUse = lastSeenVersion;
		// String versionKey = folder.getName().toLowerCase() + "/"
		// + lastSeenVersion + "/" + page;
		// if (!versionKey.equals(cachedFolderVersionPage)) {
		// versionToUse = 0;
		// }
		// if (logger.isDebugEnabled()) {
		// logger.debug("[" + token + "] should query version " + versionToUse
		// + " (vkey: " + versionKey + ")");
		// }
		params.put("version", "-1"); // FIXME

		ConversationReferenceList ret = null;
		Document doc = execute(params);
		long version = 0;
		if (doc != null) {
			Element root = doc.getDocumentElement();
			if (root.hasAttribute("version")) {
				version = Long.parseLong(root.getAttribute("version"));
			} else {
				logger.warn("unversionned response");
			}
			NodeList crs = root.getElementsByTagName("cr");
			List<ConversationReference> cPage = new ArrayList<ConversationReference>(
					crs.getLength());
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < crs.getLength(); i++) {
				Element cr = (Element) crs.item(i);
				cPage.add(parseConversation(cal, cr));
			}
			ret = new ConversationReferenceList(cPage, Integer.parseInt(root
					.getAttribute("fullLength")));
			ret.setVersion(version);

			// cachedFolderVersionPage = folder.getName().toLowerCase() + "/"
			// + ret.getVersion() + "/" + page;

			return ret;

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("informing frontend to use cache");
			}
			throw new UseCachedData();
		}
	}

}
