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

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;

public class FetchMessagesMethod extends AbstractMessageMethod {

	private String token;

	FetchMessagesMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/fetchMessages.do");
		this.token = token;
	}

	private String join(List<MessageId> l, char sep) {
		StringBuffer ret = new StringBuffer(10 * l.size());
		for (int i = 0; i < l.size(); i++) {
			if (i > 0) {
				ret.append(sep);
			}
			ret.append(String.valueOf(l.get(i).getMessageId()));
		}
		return ret.toString();
	}

	public ClientMessage[] fetchMessages(Folder folder, List<MessageId> mids,
			String defaultTimeZone) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("folder", folder.getName());
		String joined = join(mids, ',');
		params.put("messageIds", joined);

		if (logger.isDebugEnabled()) {
			logger.debug("fetching from " + folder.getName() + ": " + joined);
		}

		Document doc = execute(params);
		ClientMessage[] ret = null;
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			Element root = doc.getDocumentElement();
			List<ClientMessage> cml = new ArrayList<ClientMessage>(mids.size());

			NodeList mnl = root.getElementsByTagName("m");
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < mnl.getLength(); i++) {
				Element m = (Element) mnl.item(i);
				ClientMessage cMess = parseMessage(cal, m);

				Element invitation = DOMUtils.getUniqueElement(m, "invitation");
				if (invitation != null) {
					cMess.setHasInvitation(Boolean.parseBoolean(invitation
							.getTextContent()));
				}

				fetchFwdMessage(cMess, cal, m);
				cml.add(cMess);
				cMess.setFolderName(folder.getName());
			}
			ret = cml.toArray(new ClientMessage[cml.size()]);
		}
		return ret;
	}

}
