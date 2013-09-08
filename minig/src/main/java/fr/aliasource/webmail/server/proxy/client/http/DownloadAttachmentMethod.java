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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

public class DownloadAttachmentMethod extends AbstractClientMethod {

	protected String token;

	DownloadAttachmentMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/downloadAttachment.do");
		this.token = token;
	}

	public InputStream download(String attachmentId) {
		logger.info("download(" + attachmentId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("id", attachmentId);

		return executeStream(params);
	}

}
