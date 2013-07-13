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

public class ProxyClientCompletionTests extends TestCase {

	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompletion() {
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(Collections
				.synchronizedMap(fec.get()));
		IAccount ac = pcf.newProxyClient(cfg);
		assertNotNull(ac);

		try {
			ac.login("thomas", "zz.com", "aliacom");
			// one call to warm the server
			ac.getPossibleCompletions("emails", "a", 10);

			long time = System.currentTimeMillis();
			int count = 1000;
			for (int i = 0; i < count; i++) {
				ac.getPossibleCompletions("emails", "t", 10);
			}
			time = System.currentTimeMillis() - time;
			int msForOneCall = (int) (time / count);
			int completionsPerSeconds = 1000 / msForOneCall;
			System.err.println("perf report: " + msForOneCall
					+ "ms for one completion, running at "
					+ completionsPerSeconds + " c/s.");
		} catch (ClientException e) {
			fail("login should work, is obm.buffy.kvm running & accepting the thomas@zz.com / aliacom password ?");
		}

	}

}
