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

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.SendParameters;

/**
 * Stores a message on the server in the given folder
 * 
 * @author tom
 * 
 */
public class StoreMessageMethod extends AbstractMessageMethod {

	private String token;

	StoreMessageMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/store.do");
		this.token = token;
	}

	public ConversationId storeMessage(Folder dest, ClientMessage cm, SendParameters sp) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		if (sp != null) {
			params.putAll(createMapParamsFromSendParameters(sp));
		}
		
		try {
			Document message = getMessageAsXML(cm, sp);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(message, out);
			params.put("message", out.toString());
			params.put("folder", dest.getName());
			Document doc = execute(params);
			String[] ids = parseConversationIds(doc.getDocumentElement());
			if (ids.length == 1) {
				return new ConversationId(ids[0]);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
