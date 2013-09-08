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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import fr.aliasource.webmail.client.FolderComparator;
import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * The grid widgets with the list of Folders
 * 
 * @author matthieu
 * 
 */
public class FolderSettingsDataGrid extends Grid {

	private FolderSettingsTab flt;
	private List<Folder> folders;
	private String currentPath;
	private View ui;

	public FolderSettingsDataGrid(FolderSettingsTab flt, View ui) {
		super(1, 5);
		this.flt = flt;
		this.ui = ui;
		setWidth("100%");
		getCellFormatter().setWidth(0, 0, "70%");
		setStyleName("folderSettingsTable");
	}

	public void updateGrid(Folder[] f) {
		folders = new ArrayList<Folder>();
		for (int i = 0; i < f.length; i++) {
			folders.add(f[i]);
		}
		showGrid();
	}

	public void showGrid() {
		clear();
		resizeRows(1);
		List<Folder> folders = getDisplayFolder();
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

	private List<Folder> getDisplayFolder() {
		Map<String, Folder> map = new HashMap<String, Folder>();

		for (Folder folder : folders) {
			map.put(folder.getName(), folder);
			String[] split = folder.getName().split("/");
			String currentPath = "";
			for (int i = 0; i < split.length; i++) {
				currentPath += split[i];
				if (!map.containsKey(currentPath)) {
					Folder virtualFolder = null;
					virtualFolder = new Folder(currentPath, split[i]);
					virtualFolder.setEditable(false);
					map.put(currentPath, virtualFolder);
				}
				currentPath += "/";
			}

		}
		List<Folder> ret = new ArrayList<Folder>();
		ret.addAll(map.values());
		Collections.sort(ret, new FolderComparator());
		return ret;
	}

	private int sepCount(String fName) {
		int cnt = 0;
		for (int i = 0; i < fName.length(); i++) {
			if (fName.charAt(i) == '/') {
				cnt++;
			}
		}
		return cnt;
	}

	private void showEmptyList() {
		clear();
		resizeRows(1);
		setWidget(0, 0, new Label(I18N.strings.noAvailableFolders()));
	}

	private void fillRow(final Folder folder, final int i) {

		final int margin = sepCount(folder.getName());

		if (i % 2 == 0) {
			getRowFormatter().setStyleName(i, "odd");
		} else {
			getRowFormatter().setStyleName(i, "even");
		}

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

		final String folderName = ui.displayName(folder);

		Anchor renameLink = new Anchor(I18N.strings.renameFolder());
		renameLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				final TextBox in = new TextBox();
				in.setText(folderName);

				in.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						if (KeyCodes.KEY_ENTER == event.getNativeEvent()
								.getKeyCode()) {
							flt.renameFolder(folder, in.getText());
						}
					}

				});

				HorizontalPanel horizontalPanel = createAlignementPanel(margin);
				horizontalPanel.add(in);
				setWidget(i, 0, horizontalPanel);

			}

		});

		if (folder.canChangeSubscription()) {
			Anchor folderLink = new Anchor(ui.displayName(folder));
			folderLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					flt.showFolder(folder);
				}
			});

			if (folder.isSubscribed()) {
				folderLink.addStyleName("subscribedFolderSettingsLink");
			} else {
				folderLink.addStyleName("folderSettingsLink");
			}

			HorizontalPanel horizontalPanel = createAlignementPanel(margin);
			horizontalPanel.add(folderLink);

			setWidget(i, 0, horizontalPanel);

			setWidget(i, 1, createLink);

			Anchor actionLink = null;
			if (folder.isSubscribed()) {
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
			Label label = new Label(ui.displayName(folder.getName()));
			label.addStyleName("folderSettingsLabel");

			label.getElement().setAttribute("style",
					"margin-left: " + margin + "px");
			setWidget(i, 0, label);
			setWidget(i, 1, createLink);
			setWidget(i, 2, new HTML("&nbsp;"));
		}
		
		boolean folderProtected = isProtectedFolders(folder);
		
		if (folder.canRename() && !folderProtected) {
			setWidget(i, 3, renameLink);
		} else {
			setWidget(i, 3, new HTML("&nbsp;"));
		}
		
		if (folder.canDelete() && !folderProtected) {
			Anchor deleteLink = new Anchor(I18N.strings.delete());
			deleteLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					if (isInTrash(folder)) {
						if (Window.confirm(I18N.strings
								.confirmDirectDelete(folder
										.getDisplayName()))) {
							flt.deleteFolder(folder);
						}
					} else {
						if (Window.confirm(I18N.strings
								.confirmDeleteFolder(folder
										.getDisplayName()))) {
							flt.deleteFolder(folder);
						}
					}
				}
			});

			setWidget(i, 4, deleteLink);
		} else {
			setWidget(i, 4, new HTML("&nbsp;"));
		}
	}

	private boolean isInTrash(Folder f) {
		return f.getName().startsWith(
				WebmailController.get().getSetting(GetSettings.TRASH_FOLDER));
	}

	private HorizontalPanel createAlignementPanel(int margin) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		for (int j = 0; j < margin; j++) {
			HTML html = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			horizontalPanel.add(html);
		}
		return horizontalPanel;
	}

	private boolean isProtectedFolders(Folder folder) {
		if (folder.getName().equals(
				WebmailController.get().getSetting(GetSettings.SPAM_FOLDER))
				|| folder.getName().equalsIgnoreCase(
						WebmailController.get().getSetting(
								GetSettings.SENT_FOLDER))
				|| folder.getName().equalsIgnoreCase(
						WebmailController.get().getSetting(
								GetSettings.DRAFTS_FOLDER))
				|| folder.getName().equalsIgnoreCase(
						WebmailController.get().getSetting(
								GetSettings.TRASH_FOLDER))
				|| folder.getName().equalsIgnoreCase(
						WebmailController.get().getSetting(
								GetSettings.TEMPLATES_FOLDER))
				|| folder.getName().equalsIgnoreCase("INBOX")) {
			return true;
		}

		return false;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getCurrentPath() {
		return currentPath;
	}

}
