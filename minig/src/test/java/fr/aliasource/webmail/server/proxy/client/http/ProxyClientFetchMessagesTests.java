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

import java.util.List;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;

public class ProxyClientFetchMessagesTests extends ProxyClientTestCase {

	public void testFetchMessages() {
		List<Folder> lf = ac.getFolderService().listSubscribedFolders();
		assertNotNull(lf);
		assertTrue("subscribed folder list should not be empty", lf.size() > 0);
		Folder f = lf.get(0);
		ConversationReferenceList crl = ac.listConversations(0, f, 1, 25);
		assertNotNull(crl);
		if (crl.getPage().size() != 0) {
			ConversationReference cr = crl.getPage().get(0);
			ClientMessage[] cm = ac.fetchMessages(f, cr.getMessageIds());
			assertNotNull(cm);
			if (cm.length != cr.getMessageIds().size()) {
				fail("error fetching messages on conversation " + cr.getTitle()
						+ " with " + cr.getMessageIds() + " id(s)");
			}

		} else {
			fail("Testing fetchMessages with on an empty mail folder is stupid");
		}
	}

	public void testFetchUnreadMessages() {
		List<Folder> lf = ac.getFolderService().listSubscribedFolders();
		assertNotNull(lf);
		assertTrue("subscribed folder list should not be empty", lf.size() > 0);
		for (Folder f : lf) {
			ConversationReferenceList crl = ac.listConversations(0, f, 1, 25);
			assertNotNull(crl);
			for (ConversationReference cr : crl.getPage()) {
				ConversationContent cc = ac.fetchUnreadMessages(cr.getId());
				assertNotNull(cc);
			}
		}
	}

	public void testFetchUnreadMessagesInPouic() {
		ConversationReferenceList crl = ac.listConversations(0, new Folder(
				"Pouic"), 1, 25);
		assertNotNull(crl);
		for (ConversationReference cr : crl.getPage()) {
			ConversationContent cc = ac.fetchUnreadMessages(cr.getId());
			assertNotNull(cc);
			Conversation c = cc.getConversation();
			ClientMessage[] cms = cc.getMessages();
			System.out.println("conv with " + cms.length + " msgs. "
					+ c.getId() + ": " + c.getTitle());
			for (ClientMessage cm : cms) {
				System.out.println(" * cm[" + cm.getUid() + "]: loaded("
						+ cm.isLoaded() + ")");
			}
		}
	}

}
