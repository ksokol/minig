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

public class DownloadEmlMethod extends AbstractClientMethod {

	protected String token;

	DownloadEmlMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/downloadEml.do");
		this.token = token;
	}

	public InputStream download(String folderName, String emlId) {
		logger.info("download(" + emlId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("uid", emlId);
		params.put("folder", folderName);

		return executeStream(params);
	}

}
