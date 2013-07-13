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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;

/**
 * Prepares a clientMessage that is a forward of the given message uids
 * 
 * @author tom
 * 
 */
public class PrepareForwardMethod extends AbstractMessageMethod {

	private String token;

	PrepareForwardMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/prepareForward.do");
		this.token = token;
	}

	private String join(List<MessageId> l, char sep) {
		StringBuilder ret = new StringBuilder(10 * l.size());
		int i = 0;
		for (MessageId s : l) {
			if (i > 0) {
				ret.append(sep);
			}
			ret.append(s.getMessageId());
			i++;
		}
		return ret.toString();
	}

	public ClientMessage prepareForward(Folder folder, List<MessageId> uids) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("folder", folder.getName());
		params.put("uids", join(uids, ','));

		Document doc = null;
		try {
			doc = execute(params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		ClientMessage ret = null;
		if (doc != null) {
			Element me = DOMUtils.getUniqueElement(doc.getDocumentElement(),
					"m");
			Calendar cal = Calendar.getInstance();
			ret = parseMessage(cal, me);
		}

		return ret;
	}

}
