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
import java.util.List;

import junit.framework.TestCase;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.FrontEndConfig;
import fr.aliasource.webmail.server.proxy.client.ClientException;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

public class ProxyClientListSubscribedTests extends TestCase {

	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testListSubscribed() {
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(Collections
				.synchronizedMap(fec.get()));
		IAccount ac = pcf.newProxyClient(cfg);
		assertNotNull(ac);

		try {
			ac.login("thomas", "zz.com", "aliacom");
			List<Folder> lf = ac.getFolderService().listSubscribedFolders();
			assertNotNull(lf);
		} catch (ClientException e) {
			fail("login should work");
		}

	}

}
