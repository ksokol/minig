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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

@RemoteServiceRelativePath("folderManager")
public interface FolderManager extends RemoteService {

	public void unSubscribeFolder(Folder f);

	public void subscribeFolder(Folder f);

	public void createFolder(Folder f);

	public Folder[] listAvailableFolders();

	public Folder[] listSubscribedFolders();

	public CloudyFolder[] fetchSummary();

	public void deleteFolder(Folder folder);

	void renameFolder(Folder folder, String newName);

}
