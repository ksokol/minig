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

package fr.aliasource.webmail.server;

import java.util.List;

import fr.aliasource.webmail.client.rpc.FolderManager;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class FolderManagerImpl extends SecureAjaxCall implements FolderManager {

	private static final long serialVersionUID = 4461463999664604286L;

	public Folder[] listSubscribedFolders() {
		if (logger.isDebugEnabled()) {
			logger.debug("folderManager called");
		}

		IAccount account = getAccount();

		Folder[] ret = new Folder[0];
		try {
			if (account != null) {
				List<Folder> folders = account.getFolderService()
						.listSubscribedFolders();
				ret = new Folder[folders.size()];
				ret = folders.toArray(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public CloudyFolder[] fetchSummary() {
		CloudyFolder[] ret = new CloudyFolder[0];
		IAccount account = getAccount();
		if (account != null) {
			List<CloudyFolder> lcf = account.getFolderService().fetchSummary();
			if (lcf != null) {
				ret = lcf.toArray(new CloudyFolder[lcf.size()]);
			} else {
				logger.warn("fetched null summary");
			}
		}
		return ret;
	}

	public void createFolder(Folder f) {
		getAccount().getFolderService().createFolder(f);
	}

	public void deleteFolder(Folder f) {
		getAccount().getFolderService().deleteFolder(f);
	}

	public void subscribeFolder(Folder f) {
		getAccount().getFolderService().subscribeFolder(f);
	}

	public void unSubscribeFolder(Folder f) {
		getAccount().getFolderService().unSubscribeFolder(f);

	}

	public Folder[] listAvailableFolders() {
		if (logger.isDebugEnabled()) {
			logger.debug("folderManager called");
		}

		IAccount account = getAccount();

		Folder[] ret = new Folder[0];
		try {
			if (account != null) {
				List<Folder> folders = account.getFolderService()
						.listAvailableFolders();
				ret = new Folder[folders.size()];
				ret = folders.toArray(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public void renameFolder(Folder folder, String newName) {
		getAccount().getFolderService().renameFolder(folder, newName);

	}

}
