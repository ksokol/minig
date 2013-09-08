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

import fr.aliasource.webmail.client.shared.MessageId;

public class DispositionNotificationMethod extends AbstractClientMethod {

	private String token;

	DispositionNotificationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/dispositionNotification.do");
		this.token = token;
	}

	public void denyDispositionNotification(MessageId messageId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("accept", Boolean.FALSE.toString());
		params.put("messageId", String.valueOf(messageId.getMessageId()));
		executeVoid(params);
	}

	public void sendDispositionNotification(MessageId messageId, String folderName) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("accept", Boolean.TRUE.toString());
		params.put("folder", folderName);
		params.put("messageId", String.valueOf(messageId.getMessageId()));
		executeVoid(params);
	}

}
