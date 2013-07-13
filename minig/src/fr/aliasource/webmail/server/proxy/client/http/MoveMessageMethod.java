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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;

/**
 * Stores a message on the server in the given folder
 * 
 * @author tom
 * 
 */
public class MoveMessageMethod extends AbstractMessageMethod {

	private String token;

	MoveMessageMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/moveMessage.do");
		this.token = token;
	}

	public ConversationId moveMessage(Folder destination, ConversationId convId,
			MessageId messageId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		ConversationId ret = null;
		try {
			params.put("message", String.valueOf(messageId.getMessageId()));
			params.put("conversation", String.valueOf(convId.getConversationId()));
			params.put("destination", destination.getName());

			InputStream in = executeStream(params);
			ret = new ConversationId(streamString(in, true));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	public String streamString(InputStream in, boolean closeIn)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[100000];
		try {
			while (true) {
				int amountRead = in.read(buffer);
				if (amountRead == -1) {
					break;
				}
				out.write(buffer, 0, amountRead);
			}
		} finally {
			if (closeIn) {
				in.close();
			}
			out.flush();
			out.close();
		}
		return new String(out.toByteArray());
	}
}
