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

public class ProxyClientLoginTests extends TestCase {

	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLogin() {
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(Collections
				.synchronizedMap(fec.get()));
		IAccount ac = pcf.newProxyClient(cfg);
		long time;

		time = System.currentTimeMillis();
		try {
			// using a wrong password instead of an empty one
			// cause a 3 sec delay in imap response
			ac.login("blabla", "blabla.net", "");
			fail("login accepted with invalid credential");
		} catch (ClientException e) {
			time = System.currentTimeMillis() - time;
			System.err.println("login refused in " + time + "ms");
		}

		time = System.currentTimeMillis();
		try {
			ac.login("thomas", "zz.com", "aliacom");
			time = System.currentTimeMillis() - time;
			System.err.println("login granted in " + time + "ms");
			ac.logout();
		} catch (ClientException e) {
			e.printStackTrace();
			fail("should not be reached");
		}
	}

}
