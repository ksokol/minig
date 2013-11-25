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

package fr.aliasource.webmail.client.settings;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.shared.IFolder;

/**
 * The grid widgets with the list of Folders
 * 
 * @author matthieu
 * @author ksokol
 * 
 */
public class FolderSettingsDataGrid extends Grid {

	private FolderSettingsTab flt;
	private List<IFolder> folders;
	private String currentPath;

	public FolderSettingsDataGrid(FolderSettingsTab flt) {
		super(1, 5);
		this.flt = flt;
		setWidth("100%");
		getCellFormatter().setWidth(0, 0, "70%");
		setStyleName("folderSettingsTable");
	}

	public void updateGrid(List<IFolder> f) {
		folders = f;
		showGrid();
	}

	public void showGrid() {
		clear();
		resizeRows(1);

		if (folders.isEmpty()) {
			showEmptyList();
		} else {
			if (getRowCount() != folders.size()) {
				resizeRows(folders.size());
			}

			for (int i = 0; i < folders.size(); i++) {
				fillRow(folders.get(i), i);
			}
		}
	}

	private void showEmptyList() {
		clear();
		resizeRows(1);
		setWidget(0, 0, new Label(I18N.strings.noAvailableFolders()));
	}

	private void fillRow(final IFolder folder, final int i) {
		if (folder == null)
			return;

		getCellFormatter().setStyleName(i, 0, "settingsCell");
		getCellFormatter().setStyleName(i, 1, "settingsCell");
		getCellFormatter().setStyleName(i, 2, "settingsCell");
		getCellFormatter().setStyleName(i, 3, "settingsCell");
		getCellFormatter().setStyleName(i, 4, "settingsCell");

		Anchor createLink = new Anchor(I18N.strings.createSubFolder());
		createLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				flt.selectFolder(folder);
			}
		});

		Anchor renameLink = new Anchor(I18N.strings.renameFolder());

		renameLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				final TextBox in = new TextBox();
				in.setText(folder.getName());

				in.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						if (KeyCodes.KEY_ENTER == event.getNativeEvent()
								.getKeyCode()) {
							flt.renameFolder(folder, in.getText());
						}
					}

				});

				HorizontalPanel horizontalPanel = createAlignementPanel(1);
				horizontalPanel.add(in);
				setWidget(i, 0, horizontalPanel);
			}
		});

		if (folder.getEditable() != null && folder.getEditable()) {
			Anchor folderLink = new Anchor(folder.getName());
            folderLink.setHTML(titleWithIndent(folder));

			folderLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					flt.showFolder(folder);
				}
			});

			if (folder.getSubscribed()) {
				folderLink.addStyleName("subscribedFolderSettingsLink");
			} else {
				folderLink.addStyleName("folderSettingsLink");
			}

			HorizontalPanel horizontalPanel = createAlignementPanel(1);
			horizontalPanel.add(folderLink);

			setWidget(i, 0, horizontalPanel);
			setWidget(i, 1, createLink);

			Anchor actionLink = null;
			if (folder.getSubscribed()) {
				actionLink = new Anchor(I18N.strings.unsubscribe());
				actionLink.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent sender) {
						flt.unsubscribe(folder);
					}
				});
			} else {
				actionLink = new Anchor(I18N.strings.subscribe());
				actionLink.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent sender) {
						flt.subscribe(folder);
					}
				});
			}
			setWidget(i, 2, actionLink);
		} else {
			Anchor folderLink = new Anchor(folder.getName());
            folderLink.setHTML(titleWithIndent(folder));
			folderLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					flt.showFolder(folder);
				}
			});

			HorizontalPanel horizontalPanel = createAlignementPanel(1);
			horizontalPanel.add(folderLink);

			setWidget(i, 0, horizontalPanel);
			setWidget(i, 1, createLink);
			setWidget(i, 2, new HTML("&nbsp;"));
		}

		if (folder.getEditable() != null && folder.getEditable()) {
			setWidget(i, 3, renameLink);
		} else {
			setWidget(i, 3, new HTML("&nbsp;"));
		}

		if (folder.getEditable() != null && folder.getEditable()) {
			Anchor deleteLink = new Anchor(I18N.strings.delete());
			deleteLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					// TODO
					if (Window.confirm(I18N.strings.confirmDeleteFolder(folder
							.getName()))) {
						flt.deleteFolder(folder);
					}
				}
			});

			setWidget(i, 4, deleteLink);
		} else {
			setWidget(i, 4, new HTML("&nbsp;"));
		}
	}

	private HorizontalPanel createAlignementPanel(int margin) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		HTML html = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		horizontalPanel.add(html);
		return horizontalPanel;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getCurrentPath() {
		return currentPath;
	}

    private String titleWithIndent(IFolder f) {
        StringBuilder sb = new StringBuilder();
        int numberOfFolderSeparators = f.getId().replaceAll("[^/]","").length();

        for(int i=0;i< numberOfFolderSeparators;i++) {
            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }

        sb.append(f.getName());

        return sb.toString();
    }
}
