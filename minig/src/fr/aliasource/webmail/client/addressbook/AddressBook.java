/*
 *  BEGIN LICENSE BLOCK Version: GPL 2.0
 * 
 * The contents of this file are subject to the GNU General Public License
 * Version 2 or later (the "GPL").
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Initial Developer of the Original Code is MiniG.org project members
 * 
 * END LICENSE BLOCK
 */

package fr.aliasource.webmail.client.addressbook;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.book.UiContact;

/**
 * Address book widget
 * 
 * @author tom
 * 
 */
public class AddressBook extends DockPanel {

	private TextBox searchField;
	private View webmail;
	private Image upSpinner;

	private ContactGroups groupsList;
	private ContactList contactList;
	private ContactDisplay display;

	public AddressBook(View wm) {
		this.webmail = wm;

		int height = Window.getClientHeight() - 150;
		HorizontalPanel search = new HorizontalPanel();
		search.addStyleName("wrap");
		search.addStyleName("addressBookSearchField");
		search.setWidth("16em");

		upSpinner = new Image("minig/images/spinner_moz.gif");
		upSpinner.setVisible(false);

		searchField = new TextBox();
		searchField.setText(I18N.strings.searchContactField());
		searchField.setWidth("16em");
		searchField.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent sender) {
				if (searchField.getText().equals(
						I18N.strings.searchContactField())) {
					searchField.setText("");
				}
			}
		});

		searchField.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent be) {
				if (searchField.getText().isEmpty()) {
					searchField.setText(I18N.strings.searchContactField());
				}
			}
		});

		searchField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent ev) {
				if (KeyCodes.KEY_ESCAPE == ev.getNativeKeyCode()) {
					searchField.setText("");
				} else {
					if (!searchField.getText().isEmpty()) {
						searchResult();
					} else {
						reset();
					}
				}
			}
		});

		search.add(searchField);
		search.add(upSpinner);
		Anchor refresh = new Anchor(I18N.strings.refresh());
		refresh.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				webmail.getSpinner().startSpinning();
				new Timer() {
					@Override
					public void run() {
						webmail.getSpinner().stopSpinning();
					}
				}.schedule(500);
			}
		});
		HorizontalPanel toolbar = new HorizontalPanel();
		toolbar.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		toolbar.add(search);
		toolbar.add(new HTML("&nbsp;"));
		toolbar.add(refresh);

		add(toolbar, DockPanel.NORTH);

		setWidth("100%");

		ScrollPanel sp;

		HorizontalPanel hp = new HorizontalPanel();
		groupsList = new ContactGroups(webmail);
		sp = new ScrollPanel(groupsList);
		sp.addStyleName("whiteBackground");
		sp.setWidth("16em");
		sp.setHeight(height + "px");
		hp.add(sp);

		contactList = new ContactList(webmail);
		groupsList.addGroupSelectionListener(contactList);
		sp = new ScrollPanel(contactList);
		sp.addStyleName("whiteBackground");
		sp.setWidth("16em");
		sp.setHeight(height + "px");
		hp.add(sp);

		display = new ContactDisplay(webmail);
		contactList.addContactSelectionListerner(display);
		sp = new ScrollPanel(display);
		sp.addStyleName("contactDisplay");
		sp.setHeight(height + "px");
		hp.add(sp);

		hp.setWidth("100%");
		hp.setCellWidth(sp, "100%");

		hp.setSpacing(1);
		hp.setStyleName("addressBook");
		add(hp, DockPanel.CENTER);
		addTabListener();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize(event.getHeight());
			}
		});
	}

	private void searchResult() {
		upSpinner.setVisible(true);
		AsyncCallback<UiContact[]> ac = new AsyncCallback<UiContact[]>() {
			public void onFailure(Throwable caught) {
				webmail.log("loadGroup failure");
				webmail.getSpinner().stopSpinning();
			}

			public void onSuccess(UiContact[] contacts) {
				contactList.clear();
				display.reset();

				int row = groupsList.getRowCount() - 1;
				if (groupsList.getSelected() != -1) {
					groupsList.removeSelected(groupsList.getSelected());
				}
				groupsList.getRowFormatter().setVisible(row, true);
				groupsList.setSelected(row);
				groupsList.setWidget(row, 1, new Label(Integer
						.toString(contacts.length), false));

				contactList.setDisplayAllContact(false);
				contactList.updateGrid(contacts);

				upSpinner.setVisible(false);
			}

		};
		AjaxCall.contacts.getContacts(groupsList.getAllGroup(), searchField
				.getText(), ac);
	}

	private void reset() {
		contactList.reset();
		groupsList.removeSelected(groupsList.getSelected());
		loadGroups();
		display.reset();
	}

	private void addTabListener() {
		webmail.getTabPanel().addSelectionHandler(
				new SelectionHandler<Integer>() {
					@Override
					public void onSelection(SelectionEvent<Integer> event) {
						if (event.getSelectedItem() == View.ADDRESSBOOK) {
							loadBook();
						}
					}
				});
	}

	protected void loadBook() {
		GWT.log("loadBook", null);
		loadGroups();
	}

	private void loadGroups() {
		if (groupsList == null) {
			return;
		}
		webmail.getSpinner().startSpinning();
		AsyncCallback<ContactGroup[]> ac = new AsyncCallback<ContactGroup[]>() {
			public void onFailure(Throwable caught) {
				webmail.log("contactGroups failure");
				webmail.getSpinner().stopSpinning();
			}

			public void onSuccess(ContactGroup[] groups) {
				webmail.getSpinner().stopSpinning();
				Arrays.sort(groups);
				groupsList.updateGrid(groups);
			}

		};
		AjaxCall.contacts.getContactGroups(ac);
	}

	public void resize(int height) {
		groupsList.getParent().setHeight(height - 150 + "px");
		contactList.getParent().setHeight(height - 150 + "px");
		display.getParent().setHeight(height - 150 + "px");
	}

	public ContactList getContactList() {
		return contactList;
	}

	public ContactDisplay getContactDisplay() {
		return display;
	}

	public void clearSearchField() {
		searchField.setText(I18N.strings.searchContactField());
	}
}
