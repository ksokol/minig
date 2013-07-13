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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Folder selection controller
 * 
 * @author tom
 * 
 */
public class FolderSelector {

	private List<IFolderSelectionListener> listeners;
	private Folder current;
	private Timer timer;
	private View ui;
	private Folder[] subscribed;

	public FolderSelector() {
		listeners = new LinkedList<IFolderSelectionListener>();
		current = new Folder("inbox", I18N.strings.inbox());

		this.timer = new Timer() {
			public void run() {
				refreshUnreadCounts();
			}
		};

		ListSubFoldersCommand lsfc = new ListSubFoldersCommand();
		lsfc.execute();
	}

	public void setView(View ui) {
		this.ui = ui;
	}

	public void addListener(IFolderSelectionListener fsl) {
		listeners.add(fsl);
		if (subscribed != null) {
			fsl.foldersChanged(subscribed);
		}
	}

	public void removeListener(IFolderSelectionListener fsl) {
		listeners.remove(fsl);
	}

	private void notifyListeners(Folder f) {
		for (IFolderSelectionListener l : listeners) {
			l.folderSelected(f);
		}
	}

	public void setFolders(Folder[] folders) {
		this.subscribed = folders;
		for (IFolderSelectionListener l : listeners) {
			l.foldersChanged(folders);
		}
	}

	public void select(Folder f) {
		current = f;
		notifyListeners(f);
	}

	public Folder getCurrent() {
		return current;
	}

	public void refreshUnreadCounts() {
		AsyncCallback<CloudyFolder[]> ac = new AsyncCallback<CloudyFolder[]>() {
			public void onFailure(Throwable caught) {
				if (ui != null) {
					ui.getSpinner().stopSpinning();
				}
			}

			public void onSuccess(CloudyFolder[] f) {
				if (ui != null) {
					ui.getSpinner().stopSpinning();
				}
				if (f == null) {
					return;
				}
				for (int i = 0; i < f.length; i++) {
					notifyUnreadCount(f[i]);
				}
			}
		};
		if (ui != null) {
			ui.getSpinner().startSpinning();
		}
		AjaxCall.folderManager.fetchSummary(ac);
	}

	private void notifyUnreadCount(CloudyFolder cloudyFolder) {
		for (IFolderSelectionListener l : listeners) {
			l.unreadCountChanged(cloudyFolder);
		}
	}

	public void startUnreadTimer() {
		timer.scheduleRepeating(10 * 1000);
	}

	public void stopUnreadTimer() {
		timer.cancel();
	}

	public void addSearchFolder(String query) {
		String fName = "search:" + query;
		String fDisplayName = I18N.strings.searchFor(query);
		Folder f = new Folder(fName, fDisplayName);
		select(f);
	}

	public void addSearchFolder(String fname, String query) {
		String fName = "search:" + query;
		String fDisplayName = fname;
		Folder f = new Folder(fName, fDisplayName);
		select(f);
	}

}
