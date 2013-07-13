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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ListSubFoldersCommand;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Settings page to manage IMAP folders
 * 
 * @author matthieu
 * 
 */
public class FolderSettingsTab extends DockPanel implements ISettingsPage {

	private View ui;
	private FolderSettingsDataGrid dataGrid;
	private TextBox folderName;
	private HTML createLabel;
	private Button cancelButton;
	private Button createButton;

	public FolderSettingsTab(View ui) {
		this.ui = ui;
		setWidth("100%");
		VerticalPanel settingContentPanel = new VerticalPanel();
		settingContentPanel.setWidth("100%");
		addCreateFolder();
		dataGrid = new FolderSettingsDataGrid(this, ui);
		settingContentPanel.add(dataGrid);
		add(settingContentPanel, DockPanel.CENTER);
	}

	private void addCreateFolder() {
		createLabel = new HTML(I18N.strings.createFolder() + ":");
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(createLabel);
		hPanel.setCellVerticalAlignment(createLabel,
				HorizontalPanel.ALIGN_MIDDLE);
		folderName = new TextBox();

		hPanel.add(folderName);
		createButton = new Button(I18N.strings.create());
		createButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				createButton.setEnabled(false);
				createFolder();
			}
		});
		cancelButton = new Button(I18N.strings.cancel());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				selectFolder(null);
			}
		});
		cancelButton.setVisible(false);
		hPanel.add(createButton);
		hPanel.add(cancelButton);
		hPanel.setSpacing(3);
		add(hPanel, DockPanel.NORTH);
	}

	private void createFolder() {
		Folder f = new Folder(formatFolderName(folderName.getText()),
				folderName.getText());
		f.setSubscribed(true);
		AjaxCall.folderManager.createFolder(f, new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				refreshTab();
				refreshSubscribedFolders();
				selectFolder(null);
				createButton.setEnabled(true);
			}

			public void onFailure(Throwable caught) {
				ui.log("Cannot create folder");
			}
		});
	}

	private String formatFolderName(String name) {
		if (dataGrid.getCurrentPath() != null) {
			return dataGrid.getCurrentPath() + "/" + name;
		}
		return name;
	}

	public void subscribe(Folder folder) {
		AjaxCall.folderManager.subscribeFolder(folder,
				new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						refreshTab();
						refreshSubscribedFolders();
					}

					public void onFailure(Throwable caught) {
						ui.log("Cannot subscribe to folder");
					}
				});
	}

	public void unsubscribe(Folder folder) {
		AjaxCall.folderManager.unSubscribeFolder(folder,
				new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						refreshTab();
						refreshSubscribedFolders();
					}

					public void onFailure(Throwable caught) {
						ui.log("Cannot unsubscribe of folder");
					}
				});
	}

	public void deleteFolder(Folder folder) {
		AjaxCall.folderManager.deleteFolder(folder, new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				refreshTab();
				refreshSubscribedFolders();
			}

			public void onFailure(Throwable caught) {
				ui.log("Cannot unsubscribe of folder");
			}
		});

	}

	private void refreshSubscribedFolders() {
		ListSubFoldersCommand lsfc = new ListSubFoldersCommand();
		lsfc.execute();
	}

	public void init() {
		dataGrid.setCurrentPath(null);
		refreshTab();
	}

	private void refreshTab() {
		ui.getSpinner().startSpinning();
		AsyncCallback<Folder[]> callback = new AsyncCallback<Folder[]>() {
			public void onSuccess(Folder[] folders) {
				ui.getSpinner().stopSpinning();
				dataGrid.updateGrid(folders);
			}

			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("nextFailure");
			}
		};

		ui.log("showing folder page settings ");
		AjaxCall.folderManager.listAvailableFolders(callback);

	}

	public void selectFolder(Folder folder) {
		if (folder != null) {
			dataGrid.setCurrentPath(folder.getName());
			createLabel.setHTML(I18N.strings.createSubFolderIn() + " <b>"
					+ folder.getName() + "</b>: ");
			folderName.setText("");
			createButton.setText(I18N.strings.createSubFolder());
			cancelButton.setVisible(true);
		} else {
			dataGrid.setCurrentPath(null);
			createLabel.setHTML(I18N.strings.createFolder() + " :");
			folderName.setText("");
			createButton.setText(I18N.strings.create());
			cancelButton.setVisible(false);
		}
		folderName.setFocus(true);
	}

	public void renameFolder(Folder folder, String newName) {
		AjaxCall.folderManager.renameFolder(folder, newName,
				new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						refreshTab();
						refreshSubscribedFolders();
					}

					public void onFailure(Throwable caught) {
						ui.log("Cannot unsubscribe of folder");
					}
				});

	}

	public void showFolder(Folder f) {
		WebmailController.get().getSelector().select(f);

	}

	@Override
	public void shutdown() {
		GWT.log("shutdown should be implement...", null);
	}

}
