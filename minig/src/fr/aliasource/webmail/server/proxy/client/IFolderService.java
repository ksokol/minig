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

package fr.aliasource.webmail.server.proxy.client;

import java.util.List;

import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

public interface IFolderService {

	void createFolder(Folder f);

	void subscribeFolder(Folder f);

	void unSubscribeFolder(Folder f);

	Folder getDraftFolder();

	Folder getTemplateFolder();

	Folder getSentFolder();

	Folder getTrashFolder();

	List<Folder> listSubscribedFolders();

	List<CloudyFolder> fetchSummary();

	List<Folder> listAvailableFolders();

	void deleteFolder(Folder f);

	void renameFolder(Folder folder, String newName);

}
