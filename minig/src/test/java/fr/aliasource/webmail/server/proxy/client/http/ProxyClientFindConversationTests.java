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
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

public class ProxyClientFindConversationTests extends TestCase {

	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFindConversation() {
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
			assertTrue("subscribed folder list should not be empty",
					lf.size() > 0);
			Folder f = lf.get(0);
			ConversationReferenceList crl = ac.listConversations(0, f, 1, 25);
			assertNotNull(crl);
			if (crl.getPage().size() != 0) {
				ConversationReference cr = crl.getPage().get(0);
				ConversationReference fetchedCr = ac.findConversation(cr
						.getId());
				assertNotNull(fetchedCr);
				assertEquals(cr.getId(), fetchedCr.getId());
				assertEquals(cr.getTitle(), fetchedCr.getTitle());
			} else {
				fail("Testing fetchMessages with on an empty mail folder is stupid");
			}
		} catch (ClientException e) {
			fail("login should work, is obm.buffy.kvm running & accepting the thomas@zz.com / aliacom password ?");
		}

	}

}
