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

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.MessageId;

/**
 * Stores a message on the server in the given folder
 * 
 * @author tom
 * 
 */
public class DeleteMessageMethod extends AbstractMessageMethod {

	private String token;

	DeleteMessageMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/deleteMessage.do");
		this.token = token;
	}

	public void deleteMessage(ConversationId convId, MessageId uid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		try {
			params.put("message", String.valueOf(uid.getMessageId()));
			params.put("conversation", String.valueOf(convId.getConversationId()));

			executeVoid(params);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
