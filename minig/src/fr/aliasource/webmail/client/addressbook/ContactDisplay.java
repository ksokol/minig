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

package fr.aliasource.webmail.client.addressbook;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.book.UiContact;

/**
 * Displays informations about selected contacts
 * 
 * @author tom
 * 
 */
public class ContactDisplay extends VerticalPanel implements
		IContactSelectionListener {

	private Set<UiContact> contacts;
	private View ui;

	private HTML empty;

	public ContactDisplay(View ui) {
		super();
		this.ui = ui;
		setSpacing(4);
		empty = new HTML(I18N.strings.emptyContactDisplay());
		add(empty);

		contacts = new HashSet<UiContact>();
	}

	public void reset() {
		clear();
		add(empty);
		contacts.clear();
	}

	public void contactSelectionChanged(UiContact c, boolean selected) {
		if (selected) {
			contacts.add(c);
		} else {
			contacts.remove(c);
		}

		clear();
		if (contacts.size() > 1) {
			// Widget w = createMultiContactsWidget();
			Widget w = new MultiContactDisplay(contacts, ui);
			add(w);
		} else if (contacts.size() == 1) {
			Widget w = new ContactCardDisplay(contacts.iterator().next(), ui);
			// Widget w = createContactWidget();
			add(w);
		} else {
			add(empty);
		}
	}

}
