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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

public class DummyFolderService implements IFolderService {

	DummyDataProvider ddp;

	private List<Folder> folders = new ArrayList<Folder>();

	private Log logger = LogFactory.getLog(getClass());

	public DummyFolderService(DummyDataProvider ddp) {
		this.ddp = ddp;
		folders.add(new Folder("INBOX", "Inbox", true, true));
		folders.add(new Folder("second", "Second folder", false, true));
		folders.add(new Folder("empty", "Empty folder", true, true));
		folders.add(getDraftFolder());
		folders.add(getSentFolder());
		folders.add(getTrashFolder());

	}

	@Override
	public void createFolder(Folder f) {
		logger.info("CreateFolder");
		folders.add(f);
	}

	@Override
	public List<CloudyFolder> fetchSummary() {
		List<Folder> folders = listSubscribedFolders();
		List<CloudyFolder> ret = new ArrayList<CloudyFolder>(folders.size());
		for (Folder f : folders) {
			ret.add(new CloudyFolder(f.getName(), f.getDisplayName(), ddp.randInt(2)));
		}
		return ret;
	}

	@Override
	public Folder getDraftFolder() {
		return new Folder("INBOX.Drafts", "Drafts", true, true);
	}

	@Override
	public Folder getSentFolder() {
		return new Folder("INBOX.Sent", "Sent", true, true);
	}

	@Override
	public Folder getTrashFolder() {
		return new Folder("INBOX.Trash", "Trash", true, true);
	}

	@Override
	public Folder getTemplateFolder() {
		return new Folder("INBOX.Templates", "Templates", true, true);
	}

	@Override
	public List<Folder> listAvailableFolders() {
		return folders;
	}

	@Override
	public List<Folder> listSubscribedFolders() {
		ArrayList<Folder> ret = new ArrayList<Folder>();
		for (Folder folder : folders) {
			if (folder.isSubscribed()) {
				ret.add(folder);
			}
		}
		return ret;
	}

	@Override
	public void subscribeFolder(Folder f) {
		logger.info("Subscribe");
		for (Iterator<Folder> iterator = folders.iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			if (f.getName().equalsIgnoreCase(folder.getName())) {
				folder.setSubscribed(true);
			}
		}

	}

	@Override
	public void unSubscribeFolder(Folder f) {
		logger.info("UnSubscribe");
		for (Iterator<Folder> iterator = folders.iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			if (f.getName().equalsIgnoreCase(folder.getName())) {
				folder.setSubscribed(false);
			}
		}

	}

	@Override
	public void deleteFolder(Folder f) {
		for (Iterator<Folder> iterator = folders.iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			if (f.getName().equalsIgnoreCase(folder.getName())) {
				iterator.remove();
			}
		}

	}

	@Override
	public void renameFolder(Folder f, String newName) {
		logger.info("Rename " + f.getName() + " in " + newName);
		for (Iterator<Folder> iterator = folders.iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			if (f.getName().equalsIgnoreCase(folder.getName())) {
				f.setDisplayName(newName);
			}
		}

	}

}
