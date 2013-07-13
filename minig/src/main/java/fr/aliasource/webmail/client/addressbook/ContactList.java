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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.book.UiContact;

public class ContactList extends Grid implements IGroupSelectionListener {

	private View webmail;
	private boolean displayAllContact;
	private List<IContactSelectionListener> listeners;
	private Map<CheckBox, Integer> checkedItems;

	public ContactList(View webmail) {
		super(1, 2);
		this.webmail = webmail;
		this.displayAllContact = false;
		getCellFormatter().setWidth(0, 1, "100%");
		setWidget(0, 1, new Label(I18N.strings.emptyContactList(), true));
		setCellSpacing(0);
		listeners = new ArrayList<IContactSelectionListener>();
		setStyleName("addressBookList");
		checkedItems = new HashMap<CheckBox, Integer>();
	}

	public void reset() {
		clear();
		resizeRows(1);
		setDisplayAllContact(false);
		setWidget(0, 1, new Label(I18N.strings.emptyContactList(), true));
		getRowFormatter().setStyleName(0, ""); // Remove css (contentInfo &&
		// selectedContactGroup)
		setHeight("");
		checkedItems.clear();
	}

	public void updateGrid(final UiContact[] contacts) {
		Arrays.sort(contacts, new ContactComp());
		clear();
		int limit = getNbDisplayItems();
		if (limit > contacts.length || isDisplayAllContact()) {
			limit = contacts.length;
			setDisplayAllContact(true);
		}
		resizeRows(limit + 1);

		// Header
		setWidget(0, 0, new Label(I18N.strings.contactName()));
		getRowFormatter().setStyleName(0, "addressBookHeader");

		for (int i = 0; i < limit; i++) {
			final CheckBox cb = new CheckBox();
			final UiContact co = contacts[i];
			final int row = i + 1;
			Label l = new Label(contacts[i].getDisplayName(), true);
			l.addClickHandler(createLabelClickListener(cb, co, row));
			cb.addClickHandler(createCbClickListener(cb, row, co));
			setWidget(row, 0, cb);
			setWidget(row, 1, l);
			getRowFormatter().setStyleName(row, "addressBookItem");
		}
		if (!isDisplayAllContact()) {
			int pos = limit + 1;
			resizeRows(pos + 1);
			Anchor hl = new Anchor(I18N.strings.displayAllXContacts(Integer
					.toString(contacts.length)));
			hl.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					// TODO Auto-generated method stub
					setDisplayAllContact(!isDisplayAllContact());
					clear();
					updateGrid(contacts);
					getRowFormatter().removeStyleName(getNbDisplayItems(),
							"contentInfo");
					webmail.getAddressBook().getContactDisplay().reset();
				}
			});
			setWidget(pos, 1, hl);
			getRowFormatter().setStyleName(pos, "contentInfo");
			setHeight("100%"); // "Display all" link on grid bottom
		} else {
			setHeight("");
		}
	}

	private void selectNone() {
		for (CheckBox c : checkedItems.keySet()) {
			c.setValue(false);
			getRowFormatter().removeStyleName(checkedItems.get(c),
					"selectedContactGroup");
		}
		checkedItems.clear();
	}

	private ClickHandler createLabelClickListener(final CheckBox c,
			final UiContact co, final int row) {
		return new ClickHandler() {
			public void onClick(ClickEvent sender) {

				webmail.getAddressBook().getContactDisplay().reset();
				selectNone();

				if (!c.getValue()) {
					getRowFormatter().addStyleName(row, "selectedContactGroup");
					notifyListeners(co, true);
					c.setValue(true);
					checkedItems.put(c, row);
				} else {
					getRowFormatter().removeStyleName(row,
							"selectedContactGroup");
					notifyListeners(co, false);
					c.setValue(false);
					checkedItems.remove(c);
				}
			}
		};
	}

	private ClickHandler createCbClickListener(final CheckBox c, final int row,
			final UiContact co) {
		return new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (!c.getValue()) {
					getRowFormatter().removeStyleName(row,
							"selectedContactGroup");
					notifyListeners(co, false);
					checkedItems.remove(c);
				} else {
					getRowFormatter().addStyleName(row, "selectedContactGroup");
					notifyListeners(co, true);
					checkedItems.put(c, row);
				}
			}
		};
	}

	public void groupSelected(ContactGroup group) {
		GWT.log("Group selected " + group.getDisplayName(), null);
		loadGroup(group);
	}

	private void loadGroup(ContactGroup group) {
		this.setDisplayAllContact(false);
		webmail.getSpinner().startSpinning();
		AsyncCallback<UiContact[]> ac = new AsyncCallback<UiContact[]>() {
			public void onFailure(Throwable caught) {
				webmail.log("loadGroup failure");
				webmail.getSpinner().stopSpinning();
			}

			public void onSuccess(UiContact[] contacts) {
				webmail.getSpinner().stopSpinning();
				updateGrid(contacts);
			}

		};
		AjaxCall.contacts.getContacts(group, "", ac);
	}

	private void notifyListeners(UiContact c, boolean selected) {
		for (IContactSelectionListener gsl : listeners) {
			gsl.contactSelectionChanged(c, selected);
		}
	}

	public void addContactSelectionListerner(IContactSelectionListener csl) {
		listeners.add(csl);
	}

	private int getNbDisplayItems() {
		int height = this.getParent().getOffsetHeight();
		return (height / 23) - 1;
	}

	public void setDisplayAllContact(boolean displayAllContact) {
		this.displayAllContact = displayAllContact;
	}

	public boolean isDisplayAllContact() {
		return displayAllContact;
	}

	private class ContactComp implements Comparator<UiContact> {
		public int compare(UiContact c1, UiContact c2) {
			if (c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName()) > 0) {
				return 1;
			}
			return -1;
		}
	}

}
