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

package fr.aliasource.webmail.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.ctrl.Features;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * The search textfield & search button widget
 * 
 * @author tom
 * 
 */
public class SearchBox extends HorizontalPanel implements
		IFolderSelectionListener {

	private TextBox searchMailQuery;
	private Button searchMailButton;
	private View ui;

	public SearchBox(View ui) {
		this.ui = ui;
		searchMailQuery = new TextBox();
		searchMailQuery.setWidth("15em");
		HorizontalPanel hp = new HorizontalPanel();
		searchMailButton = new Button(I18N.strings.searchMail());
		searchMailButton.addStyleName("searchButton");
		hp.add(searchMailButton);
		hp.add(new HTML("&nbsp;"));

		VerticalPanel vp = new VerticalPanel();

		Anchor sal = createShowAdvancedLink();
		vp.add(sal);

		if (Features.FILTERS) {
			sal = createCreateAFilterLink();
			vp.add(sal);
		}

		hp.add(vp);
		hp.setCellVerticalAlignment(vp, HorizontalPanel.ALIGN_MIDDLE);

		add(searchMailQuery);
		add(hp);

		initSearchAction();
		addStyleName("searchBox");

		searchMailQuery.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					doSearch();
				}
			}
		});

		WebmailController.get().getSelector().addListener(this);
	}

	private Anchor createCreateAFilterLink() {
		Anchor hl = new Anchor(I18N.strings.createAFilter());
		hl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.log("Create a filter");
				ui.getToolbar().getSearchBox().setVisible(false);
				ui.getToolbar().getCreateAFilterBox().setVisible(true);
			}
		});
		return hl;
	}

	private Anchor createShowAdvancedLink() {
		Anchor hl = new Anchor(I18N.strings.showSearchOptions());
		hl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.log("Show search options");
				ui.getToolbar().getSearchBox().setVisible(false);
				ui.getToolbar().getAdvancedSearchBox().setVisible(true);
			}
		});
		return hl;
	}

	private void doSearch() {
		String query = searchMailQuery.getText();
		if (query != null && query.length() > 0) {
			ui.log("Searching for '" + searchMailQuery.getText() + "'...");
			// this will trigger all IFolderSelectionListener
		} else {
			ui.log("Empty query ...");
			ui.notifyUser(I18N.strings.invalidSearchQuery());
		}
		WebmailController.get().getSelector().addSearchFolder(query);
		ui.setQuery(query);
	}

	private void initSearchAction() {
		searchMailButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				doSearch();
			}
		});
	}

	public void folderSelected(Folder f) {
		String folderName = f.getName();
		if (!folderName.startsWith("search:")) {
			searchMailQuery.setText("");
		} else {
			searchMailQuery.setText(folderName.substring("search:".length()));
		}
	}

	public void foldersChanged(Folder[] folders) {
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
	}

	public void unreadCountUpdated(Folder f, int delta) {
	}

	public void setSearchMailQuery(String query) {
		this.searchMailQuery.setText(query);
	}
}
