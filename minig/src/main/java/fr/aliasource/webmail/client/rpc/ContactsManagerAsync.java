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

package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.book.UiContact;

public interface ContactsManagerAsync {

	void getContacts(ContactGroup cg, String query,
			AsyncCallback<UiContact[]> ac);

	void getContactGroups(AsyncCallback<ContactGroup[]> callback);

	void createContact(UiContact c, AsyncCallback<Void> callback);

}
