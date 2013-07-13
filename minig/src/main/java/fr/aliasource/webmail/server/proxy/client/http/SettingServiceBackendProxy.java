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

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.server.proxy.client.ISettingService;
import fr.aliasource.webmail.server.proxy.client.http.setting.SaveSignatureMethod;

/**
 * Implementation using the MiniG backend to provide data.
 * 
 * @author adrien
 * 
 */
public class SettingServiceBackendProxy implements ISettingService {

	private SaveSignatureMethod saveSignatureMethod;

	public SettingServiceBackendProxy(HttpClient hc, String token,
			String backendUrl) {
		saveSignatureMethod = new SaveSignatureMethod(hc, token, backendUrl);
	}

	@Override
	public void saveSignature(Map<String, String> identities) {
		saveSignatureMethod.saveSignature(identities);
	}

}
