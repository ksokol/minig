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

import java.util.Map;

public class ProxyConfig {

	public static final String PROXY_URL = "frontend.proxyUrl";
	public static final String PROXY_CLIENT_FACTORY_CLASS = "frontend.gwt.proxyClientFactoryClass";

	private String proxyClientFactoryClass;
	private String proxyUrl;

	private static final String DUMMY_PROXY_FACTORY = DummyProxyClientFactory.class
			.getCanonicalName();

	/**
	 * Initializes the frontend with client settings
	 * 
	 * @param synchronizedMap
	 */
	public ProxyConfig(Map<String, String> synchronizedMap) {
		proxyClientFactoryClass = synchronizedMap
				.get(PROXY_CLIENT_FACTORY_CLASS);
		proxyUrl = synchronizedMap.get(PROXY_URL);
	}

	/**
	 * Returns the classname of the factory used to instantiate proxy clients.
	 * 
	 * Defaults to a dummy factory capable of running without proxy & imap
	 * server.
	 * 
	 * @return
	 */
	public String getProxyClientFactoryClass() {
		if (proxyClientFactoryClass != null) {
			return proxyClientFactoryClass;
		} else {
			return DUMMY_PROXY_FACTORY;
		}
	}

	public String getProxyUrl() {
		if (proxyUrl != null) {
			return proxyUrl;
		} else {
			System.out.println(PROXY_URL
					+ " is not set, returning default value");
			return "http://localhost:8081";
		}
	}

}
