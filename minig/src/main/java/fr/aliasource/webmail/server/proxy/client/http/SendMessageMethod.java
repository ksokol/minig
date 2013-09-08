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

import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;

public class SendMessageMethod extends AbstractMessageMethod {

	private String token;

	SendMessageMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/sendMessage.do");
		this.token = token;
	}

	public SendResponse sendMessage(ClientMessage cm, ReplyInfo ri,
			SendParameters sp) {

		SendResponse ret = new SendResponse();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		if (ri != null) {
			params.put("folder", ri.getOrigFolder().getName());
			params.put("uid", String.valueOf(ri.getId().getMessageId()));
		}

		if (sp != null) {
			params.putAll(createMapParamsFromSendParameters(sp));
		}

		Document doc = null;
		try {
			doc = getMessageAsXML(cm, sp);

			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			params.put("message", out.toString());
			doc = execute(params);

			if (logger.isInfoEnabled()) {
				logger.info("[" + token + "] message sent.");
			}

			if (doc.getDocumentElement().getNodeName().equals("error")) {
				ret.setReason(DOMUtils.getElementText(doc.getDocumentElement(),
						"reason"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

}
