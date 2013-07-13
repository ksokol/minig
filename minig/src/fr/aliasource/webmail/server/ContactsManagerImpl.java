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

package fr.aliasource.webmail.server;

import fr.aliasource.webmail.client.rpc.ContactsManager;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.book.UiContact;

public class ContactsManagerImpl extends SecureAjaxCall implements
		ContactsManager {

	private static final long serialVersionUID = 6000271736078314480L;

	public UiContact[] getContacts(ContactGroup cg, String query) {
		UiContact[] ret = getAccount().getContacts(cg, query);
		return ret;
	}

	public ContactGroup[] getContactGroups() {
		return getAccount().getContactGroups();
	}

	@Override
	public void createContact(UiContact c) {
		getAccount().createContact(c);
	}

}
