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

package fr.aliasource.webmail.client.composer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.settings.IServerSettingsListener;
import fr.aliasource.webmail.client.shared.EmailAddress;

public class IdentitiesSelectionBox extends ListBox implements
		IServerSettingsListener {

	private List<EmailAddress> addresses;
	private List<IdentitySelectionBoxChangeListener> listeners;

	public IdentitiesSelectionBox() {
		this.addAdresses();
		this.listeners = new ArrayList<IdentitySelectionBoxChangeListener>();
		this.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent sender) {
				GWT.log("onChange IdentitiesSelectionBox " + listeners.size(),
						null);
				for (IdentitySelectionBoxChangeListener lis : listeners) {
					lis.notifyIdentityChanged();
				}
			}
		});
		WebmailController.get().addServerSettingsListener(this);
	}

	private void addAdresses() {
		addresses = WebmailController.get().getIdentities();
		for (EmailAddress a : addresses) {
			addItem(a.getDisplay() + " <" + a.getEmail() + ">");
		}
	}

	public EmailAddress getSelectedAddress() {
		return addresses.get(getSelectedIndex());
	}

	public void setSelectedAddress(EmailAddress address) {
		if (addresses.lastIndexOf(address) != -1) {
			setSelectedIndex(addresses.lastIndexOf(address));
		} else {
			setSelectedIndex(0);
		}

	}

	public void removeIdentitySelectionBoxChangeListener(
			IdentitySelectionBoxChangeListener isbcl) {
		listeners.remove(isbcl);
	}

	public void addIdentitySelectionBoxChangeListener(
			IdentitySelectionBoxChangeListener isbcl) {
		GWT.log("addIdentitySelectionBoxChangeListener", null);
		listeners.add(isbcl);
	}

	public void settingsReceived() {
		this.clear();
		this.addAdresses();
	}

}
