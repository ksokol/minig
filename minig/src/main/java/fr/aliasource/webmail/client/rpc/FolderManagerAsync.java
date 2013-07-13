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

package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

public interface FolderManagerAsync {

	public void unSubscribeFolder(Folder f, AsyncCallback<Void> ac);

	public void subscribeFolder(Folder f, AsyncCallback<Void> ac);

	public void createFolder(Folder f, AsyncCallback<Void> ac);

	public void listAvailableFolders(AsyncCallback<Folder[]> ac);

	public void listSubscribedFolders(AsyncCallback<Folder[]> ac);

	public void fetchSummary(AsyncCallback<CloudyFolder[]> ac);

	public void deleteFolder(Folder folder, AsyncCallback<Void> asyncCallback);

	public void renameFolder(Folder folder, String newName,
			AsyncCallback<Void> asyncCallback);

}
