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

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.SendParameters;

public class ProxyClientStoreMessageTests extends ProxyClientTestCase {

	public void testStore() {
		ClientMessage cm = getDummyMessage(new String[] { MY_MAIL });
		SendParameters sp = new SendParameters();
		sp.setSendPlainText(true);
		ConversationId mid = ac.storeMessage(new Folder("Sent"), cm, sp);
		System.err.println("stored message id is " + mid);
	}

}
