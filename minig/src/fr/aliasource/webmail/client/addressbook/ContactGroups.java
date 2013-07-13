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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.ContactGroup;

/**
 * The clickable list of contact groups on the left of the addressbook
 * 
 * @author tom
 * 
 */
public class ContactGroups extends Grid {

	private int selected = -1;
	private View ui;
	private List<IGroupSelectionListener> listeners;
	private ContactGroup AllGroup;
	private Map<String, String> i18nStrings;

	public ContactGroups(View ui) {
		super(1, 2);
		this.ui = ui;
		AllGroup = null;
		getCellFormatter().setWidth(0, 0, "100%");
		setWidget(0, 0, new Label("Loading..."));
		setCellSpacing(0);
		listeners = new ArrayList<IGroupSelectionListener>();
		setStyleName("addressBookList");
		i18nStrings = new HashMap<String, String>();

		i18nStrings.put("all", I18N.strings.myContactsGroup());
		i18nStrings.put("minig_collected", I18N.strings.collectedAddresses());
		i18nStrings.put("obm_ldap", I18N.strings.obmLdapGroup());
		i18nStrings.put("obm_private", I18N.strings.obmPrivateGroup());
		i18nStrings.put("obm_public", I18N.strings.obmPublicGroup());
	}

	public void updateGrid(final ContactGroup[] groups) {
		clear();
		resizeRows(groups.length + 2);

		// Header
		setWidget(0, 0, new Label(I18N.strings.contactGroup()));
		getRowFormatter().setStyleName(0, "addressBookHeader");

		// Group List
		for (int i = 0; i < groups.length; i++) {
			final ContactGroup cg = groups[i];
			final int row = i + 1;
			String label = cg.getDisplayName();
			if (i18nStrings.containsKey(cg.getId())) {
				label = i18nStrings.get(cg.getId());
			}
			Label groupLabel = new Label(label, true);
			Label groupCount = new Label(Integer.toString(cg.getSize()), false);
			ClickHandler handler = createClickListener(row, groups, cg);
			groupLabel.addClickHandler(handler);
			groupCount.addClickHandler(handler);
			setWidget(row, 0, groupLabel);
			setWidget(row, 1, groupCount);
			getRowFormatter().setStyleName(row, "addressBookItem");
			if (AllGroup == null) {
				AllGroup = cg;
			}
		}

		// Search Results
		setWidget(groups.length + 1, 0, new Label(I18N.strings
				.searchContactResults(), true));
		getRowFormatter().setVisible(groups.length + 1, false);
	}

	private ClickHandler createClickListener(final int row,
			final ContactGroup[] groups, final ContactGroup cg) {
		return new ClickHandler() {
			public void onClick(ClickEvent sender) {
				getRowFormatter().addStyleName(row, "selectedContactGroup");
				if (selected >= 0) {
					getRowFormatter().removeStyleName(selected,
							"selectedContactGroup");
				}

				// Reset contact list
				ui.getAddressBook().getContactList().reset();

				// Hide "Search Results"
				if (selected == groups.length + 1) {
					getRowFormatter().setVisible(groups.length + 1, false);
				}
				// Reset display panel
				ui.getAddressBook().getContactDisplay().reset();
				setSelected(row);
				notifyListeners(cg);

				// Reset search field
				ui.getAddressBook().clearSearchField();
			}
		};
	}

	private void notifyListeners(ContactGroup cg) {
		for (IGroupSelectionListener gsl : listeners) {
			gsl.groupSelected(cg);
		}
	}

	public void addGroupSelectionListener(IGroupSelectionListener gsl) {
		listeners.add(gsl);
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int row) {
		selected = row;
		getRowFormatter().addStyleName(row, "selectedContactGroup");
	}

	public void removeSelected(int row) {
		getRowFormatter().removeStyleName(row, "selectedContactGroup");
	}

	public ContactGroup getAllGroup() {
		return AllGroup;
	}

}
