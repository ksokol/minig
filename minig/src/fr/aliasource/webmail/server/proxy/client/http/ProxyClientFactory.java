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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.server.XTrustProvider;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.IProxyClientFactory;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

/**
 * Create a proxy client, used for communications between the AJAX ui and the
 * IMAP proxy.
 * 
 * @author tom
 * 
 */
public class ProxyClientFactory implements IProxyClientFactory {

	static {
		XTrustProvider.install();
	}

	private Log logger = LogFactory.getLog(getClass());

	@Override
	public IAccount newProxyClient(ProxyConfig cfg) {
		checkConfig(cfg);
		logger.info("Creating proxy client");
		ProxyClient pc = new ProxyClient(cfg);
		return pc;
	}

	private void checkConfig(ProxyConfig cfg) {
		String url = cfg.getProxyUrl();
		if (url == null) {
			throw new RuntimeException(
					"MiniG backend url not found in configuration");
		}
	}

}
