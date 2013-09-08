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

import java.util.Collections;

import junit.framework.TestCase;
import fr.aliasource.webmail.server.FrontEndConfig;
import fr.aliasource.webmail.server.proxy.client.ClientException;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

public class ProxyClientSettingsTests extends TestCase {

	public void testLoadSettings() {
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(Collections
				.synchronizedMap(fec.get()));
		IAccount ac = pcf.newProxyClient(cfg);

		final int COUNT = 5;

		for (int i = 0; i < COUNT; i++) {
			System.err.println("Starting round " + (i + 1) + "/" + COUNT
					+ "...");
			loginLogout(ac);
			System.err.println("Ending round " + (i + 1) + "/" + COUNT + ".");
		}
	}

	private void loginLogout(IAccount ac) {
		long time = System.currentTimeMillis();
		try {
			ac.login("thomas", "zz.com", "aliacom");
		} catch (ClientException e) {
			e.printStackTrace();
			fail("should not be reached");
		} finally {
			ac.logout();
			time = System.currentTimeMillis() - time;
			System.err.println("login/logout done in " + time + "ms");
		}
	}

}
