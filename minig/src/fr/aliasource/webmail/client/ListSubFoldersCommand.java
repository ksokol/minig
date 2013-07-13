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

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Folder;

public class ListSubFoldersCommand implements Command {

	public ListSubFoldersCommand() {
	}

	public void execute() {
		AsyncCallback<Folder[]> callback = new AsyncCallback<Folder[]>() {
			public void onSuccess(Folder[] result) {
				Arrays.sort(result, new FolderComparator());
				WebmailController.get().getSelector().setFolders(result);
				GWT.log("/folderManager call successfull", null);
			}

			public void onFailure(Throwable caught) {
				GWT.log("/folderManager failure (" + caught.getMessage() + ")",
						null);
			}
		};

		AjaxCall.folderManager.listSubscribedFolders(callback);
	}

}
