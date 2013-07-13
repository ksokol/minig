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

package fr.aliasource.webmail.server.proxy.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a proxy client that generates fake values suitable for testing the
 * ui. This "client" is runnable without a proxy and mail servers.
 * 
 * @author tom
 * 
 */
public class DummyProxyClientFactory implements IProxyClientFactory {

	private Log logger = LogFactory.getLog(getClass());

	@Override
	public IAccount newProxyClient(ProxyConfig cfg) {
		logger.info("Create dummy proxy client.");
		return new DummyProxyClient(cfg);
	}

}
