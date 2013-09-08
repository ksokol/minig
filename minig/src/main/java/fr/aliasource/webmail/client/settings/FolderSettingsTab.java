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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ListSubFoldersCommand;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.ICreateFolderRequest;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.shared.IFolderList;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * Settings page to manage IMAP folders
 * 
 * @author matthieu
 * 
 */
public class FolderSettingsTab extends DockPanel {

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
		dataGrid = new FolderSettingsDataGrid(this);
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
		Ajax<ICreateFolderRequest> builder = AjaxFactory.createFolder(dataGrid
				.getCurrentPath());
		ICreateFolderRequest createFolderRequest = BeanFactory.instance
				.createFolder().as();

		createFolderRequest.setFolder(folderName.getText());

		try {
			builder.send(createFolderRequest,
					new AjaxCallback<ICreateFolderRequest>() {

						@Override
						public void onSuccess(ICreateFolderRequest object) {
							refreshTab();
							refreshSubscribedFolders();
							selectFolder(null);
							createButton.setEnabled(true);
						}

						@Override
						public void onError(Request request, Throwable exception) {
							if (exception != null) {
								ui.notifyUser(exception.getMessage());
							} else {
								ui.notifyUser("something went wrong");
							}
						}
					});
		} catch (RequestException e) {
			ui.notifyUser(e.getMessage());
		}
	}

	private void updateFolder(IFolder folder) {
		Ajax<IFolder> ajax = AjaxFactory.updateFolder(folder.getId());

		try {
			ajax.send(folder, new AjaxCallback<IFolder>() {

				@Override
				public void onSuccess(IFolder object) {
					refreshTab();
					refreshSubscribedFolders();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					if (exception != null) {
						ui.notifyUser(exception.getMessage());
					} else {
						ui.notifyUser("something went wrong");
					}
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void subscribe(IFolder folder) {
		folder.setSubscribed(true);
		updateFolder(folder);
	}

	public void unsubscribe(IFolder folder) {
		folder.setSubscribed(false);
		updateFolder(folder);
	}

	public void deleteFolder(IFolder folder) {
		Ajax<IFolder> request = AjaxFactory.deleteFolder(folder.getId());

		try {
			request.send(new AjaxCallback<IFolder>() {

				@Override
				public void onSuccess(IFolder object) {
					refreshTab();
					refreshSubscribedFolders();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					if (exception != null) {
						ui.notifyUser(exception.getMessage());
					}
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			ui.notifyUser(e.getMessage());
		}

		// AjaxCall.folderManager.deleteFolder(folder, new AsyncCallback<Void>()
		// {
		// public void onSuccess(Void result) {
		// refreshTab();
		// refreshSubscribedFolders();
		// }
		//
		// public void onFailure(Throwable caught) {
		// ui.log("Cannot unsubscribe of folder");
		// }
		// });

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
		Ajax<IFolderList> builder = AjaxFactory.subscribedFolder();

		try {
			builder.send(new AjaxCallback<IFolderList>() {

				@Override
				public void onSuccess(IFolderList object) {
					ui.getSpinner().stopSpinning();
					dataGrid.updateGrid(object.getFolderList());
				}

				@Override
				public void onError(Request request, Throwable exception) {
					ui.getSpinner().stopSpinning();
					// TODO
					if (exception != null) {
						exception.printStackTrace();
					}
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectFolder(IFolder folder) {
		if (folder != null) {
			dataGrid.setCurrentPath(folder.getId());
			createLabel.setHTML(I18N.strings.createSubFolderIn() + " <b>"
					+ folder.getId() + "</b>: ");
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

	public void renameFolder(IFolder folder, String newName) {
		folder.setName(newName);
		updateFolder(folder);
	}

	public void showFolder(IFolder f) {
		WebmailController.get().getSelector().select(f);
	}
}
