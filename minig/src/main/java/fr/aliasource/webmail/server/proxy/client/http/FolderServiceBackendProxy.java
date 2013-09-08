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

package fr.aliasource.webmail.server.proxy.client.http;

import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.IFolderService;
import fr.aliasource.webmail.server.proxy.client.http.folder.CreateFolderMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.DeleteFolderMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.FetchSummaryMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.ListAvailableFolderMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.ListSubscribedMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.RenameFolderMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.SubscribeMethod;
import fr.aliasource.webmail.server.proxy.client.http.folder.UnsubscribeMethod;

/**
 * Implementation using the MiniG backend to provide data.
 * 
 * @author matthieu
 * 
 */
public class FolderServiceBackendProxy implements IFolderService {

	private Map<String, String> serverSettings;
	private ListSubscribedMethod listSubscribedMethod;
	private ListAvailableFolderMethod listAvailableFolderMethod;
	private CreateFolderMethod createFolderMethod;
	private SubscribeMethod subscribeMethod;
	private DeleteFolderMethod deleteFolderMethod;
	private UnsubscribeMethod unsubscribeMethod;
	private RenameFolderMethod renameFolderMethod;
	private FetchSummaryMethod fetchSummaryMethod;

	public FolderServiceBackendProxy(Map<String, String> serverSettings,
			HttpClient hc, String token, String backendUrl) {
		this.serverSettings = serverSettings;
		this.listSubscribedMethod = new ListSubscribedMethod(hc, token,
				backendUrl);
		this.listAvailableFolderMethod = new ListAvailableFolderMethod(hc,
				token, backendUrl);
		this.unsubscribeMethod = new UnsubscribeMethod(hc, token, backendUrl);
		this.createFolderMethod = new CreateFolderMethod(hc, token, backendUrl);
		this.subscribeMethod = new SubscribeMethod(hc, token, backendUrl);
		this.renameFolderMethod = new RenameFolderMethod(hc, token, backendUrl);
		this.deleteFolderMethod = new DeleteFolderMethod(hc, token, backendUrl);
		this.fetchSummaryMethod = new FetchSummaryMethod(hc, token, backendUrl);
	}

	@Override
	public void createFolder(Folder f) {
		createFolderMethod.createFolder(f);

	}

	@Override
	public void deleteFolder(Folder f) {
		deleteFolderMethod.deleteFolder(f);

	}

	@Override
	public void subscribeFolder(Folder f) {
		subscribeMethod.subscribe(f);

	}

	@Override
	public void unSubscribeFolder(Folder f) {
		unsubscribeMethod.unsubscribe(f);

	}

	@Override
	public void renameFolder(Folder folder, String newName) {
		renameFolderMethod.rename(folder, newName);

	}

	@Override
	public Folder getDraftFolder() {
		return new Folder(serverSettings.get(GetSettings.DRAFTS_FOLDER));
	}

	@Override
	public Folder getSentFolder() {
		return new Folder(serverSettings.get(GetSettings.SENT_FOLDER));

	}

	@Override
	public Folder getTrashFolder() {
		return new Folder(serverSettings.get(GetSettings.TRASH_FOLDER));
	}

	@Override
	public Folder getTemplateFolder() {
		return new Folder(serverSettings.get(GetSettings.TEMPLATES_FOLDER));
	}

	@Override
	public List<Folder> listSubscribedFolders() {
		return listSubscribedMethod.listSubscribed();
	}

	@Override
	public List<CloudyFolder> fetchSummary() {
		return fetchSummaryMethod.fetchSummary();
	}

	@Override
	public List<Folder> listAvailableFolders() {
		return listAvailableFolderMethod.listAvailable();
	}

}
