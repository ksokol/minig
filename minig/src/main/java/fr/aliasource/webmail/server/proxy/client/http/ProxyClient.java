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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.shared.AttachmentList;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.QuotaInfo;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;
import fr.aliasource.webmail.client.shared.ServerEventKind;
import fr.aliasource.webmail.client.shared.VacationInfo;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.chat.History;
import fr.aliasource.webmail.server.proxy.client.ClientException;
import fr.aliasource.webmail.server.proxy.client.Completion;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.IFolderService;
import fr.aliasource.webmail.server.proxy.client.ISettingService;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;
import fr.aliasource.webmail.server.proxy.client.http.filters.CreateOrUpdateFilterMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.FetchForwardMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.FetchVacationMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.ListFiltersMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.RemoveFilterMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.UpdateForwardMethod;
import fr.aliasource.webmail.server.proxy.client.http.filters.UpdateVacationMethod;

/**
 * {@link IAccount} implementation using the MiniG backend to provide data.
 * 
 * @author tom
 * 
 */
public class ProxyClient extends AbstractProxyClient implements IAccount,
		HttpSessionBindingListener {

	private Map<String, String> serverSettings;
	private ListConversationsMethod listConversationsMethod;
	private LogoutMethod logoutMethod;
	private FindConversationMethod findConversationMethod;
	private FetchMessagesMethod fetchMessagesMethod;
	private FetchUnreadMessagesMethod fetchUnreadMessagesMethod;
	private CompletionMethod completionMethod;
	private SearchMethod searchMethod;
	private SendMessageMethod sendMethod;
	private StoreMessageMethod storeMethod;
	private GetContactGroupsMethod contactGroupsMethod;
	private GetContactsMethod contactsMethod;
	private CreateContactMethod createContactMethod;
	private FlagsMethod flagsMethod;
	private DispositionNotificationMethod dispositionNotificationMethod;
	private MoveConversationMethod moveConversationMethod;
	private MoveMessageMethod moveMessageMethod;
	private DeleteConversationMethod deleteConversationMethod;
	private DeleteMessageMethod deleteMessageMethod;
	private SettingsMethod settingsMethod;
	private AllocateAttachmentIdMethod attachementsMethod;
	private DropAttachmentsMethod dropAttachmentsMethod;
	private AttachmentsMetadataMethod attachmentsMetadataMethod;
	private UploadAttachmentMethod uploadAttachmentMethod;
	private DownloadAttachmentMethod dlAttachmentMethod;
	private DownloadEmlMethod dlEmlMethod;
	private GetQuotaMethod getQuotaMethod;
	private PrepareForwardMethod prepareForwardMethod;
	private IFolderService folderService;
	private ISettingService settingService;
	private CreateOrUpdateFilterMethod createUpdateFilter;
	private ListFiltersMethod listFiltersMethod;
	private RemoveFilterMethod rmFilterMethod;

	private PurgeMethod purgeMethod;
	private FetchVacationMethod fetchVacationMethod;
	private UpdateVacationMethod updateVacationMethod;
	private FetchForwardMethod fetchForwardMethod;
	private UpdateForwardMethod updateForwardMethod;
	private StoreHistoryMethod storeHistoryMethod;

	private HttpClient hc;
	private MultiThreadedHttpConnectionManager mtConMan;
	private PushMethod pushMethod;

	public ProxyClient(ProxyConfig cfg) {
		this.backendUrl = cfg.getProxyUrl();

		serverSettings = new HashMap<String, String>();

		if (logger.isInfoEnabled()) {
			logInfo("Proxy client created");
		}
		this.hc = createHttpClient();
	}

	public String getToken() {
		return token;
	}

	private HttpClient createHttpClient() {
		mtConMan = new MultiThreadedHttpConnectionManager();
		HttpClient ret = new HttpClient(mtConMan);
		HttpConnectionManagerParams mp = ret.getHttpConnectionManager()
				.getParams();
		mp.setDefaultMaxConnectionsPerHost(4);
		mp.setMaxTotalConnections(8);
		return ret;
	}

	private void loadServerSettings() {
		Map<String, String> loadedSettings = settingsMethod.getSettings();
		serverSettings.putAll(loadedSettings);
		serverSettings.put(GetSettings.SERVER_SETTINGS_LOADED, "true");
	}

	@Override
	public ClientMessage[] fetchMessages(Folder f, List<MessageId> messageIds) {
		return fetchMessagesMethod.fetchMessages(f, messageIds, getTimeZone());
	}

	@Override
	public ConversationContent fetchUnreadMessages(ConversationId convId) {
		return fetchUnreadMessagesMethod.fetchUnreadMessages(convId, getTimeZone());
	}

	@Override
	public ConversationReference findConversation(ConversationId convId) {
		return findConversationMethod.findConversation(convId);
	}

	@Override
	public List<Completion> getPossibleCompletions(String completionType,
			String shortQuery, int maxResponse) {
		return completionMethod.complete(completionType, shortQuery,
				maxResponse);
	}

	@Override
	public Map<String, String> getServerSettings() {
		if (logger.isDebugEnabled()) {
			logDebug("getServerSettings");
		}
		return serverSettings;
	}

	@Override
	public Map<String, String> updateServerSettings() {
		serverSettings.clear();
		loadServerSettings();
		return serverSettings;
	}

	@Override
	public ConversationReferenceList listConversations(long lastSeenVersion,
			Folder folder, int page, int pageLength) {
		return listConversationsMethod.listConversations(lastSeenVersion,
				folder, page, pageLength);
	}

	@Override
	public void login(String login, String domain, String password)
			throws ClientException {
		LoginMethod loginMethod = new LoginMethod(hc, backendUrl);
		String receivedToken = loginMethod.login(login, domain, password);

		if (receivedToken != null) {
			token = receivedToken;
			this.login = login;
			this.domain = domain;

			initTokenMethods();
			loadServerSettings();

			if (logger.isInfoEnabled()) {
				logInfo("token:" + receivedToken);
			}
		} else {
			logger.warn("backend rejected login to l:" + login + " @:" + domain
					+ " p:" + password);
			throw new ClientException();
		}
	}

	private void initTokenMethods() {
		listConversationsMethod = new ListConversationsMethod(hc, token,
				backendUrl);
		logoutMethod = new LogoutMethod(hc, token, backendUrl);
		findConversationMethod = new FindConversationMethod(hc, token,
				backendUrl);
		fetchMessagesMethod = new FetchMessagesMethod(hc, token, backendUrl);
		fetchUnreadMessagesMethod = new FetchUnreadMessagesMethod(hc, token, backendUrl);
		completionMethod = new CompletionMethod(hc, token, backendUrl);
		searchMethod = new SearchMethod(hc, token, backendUrl);
		sendMethod = new SendMessageMethod(hc, token, backendUrl);
		storeMethod = new StoreMessageMethod(hc, token, backendUrl);
		contactGroupsMethod = new GetContactGroupsMethod(hc, token, backendUrl);
		contactsMethod = new GetContactsMethod(hc, token, backendUrl);
		createContactMethod = new CreateContactMethod(hc, token, backendUrl);
		flagsMethod = new FlagsMethod(hc, token, backendUrl);
		dispositionNotificationMethod = new DispositionNotificationMethod(hc, token, backendUrl);
		moveConversationMethod = new MoveConversationMethod(hc, token,
				backendUrl);
		moveMessageMethod = new MoveMessageMethod(hc, token, backendUrl);
		settingsMethod = new SettingsMethod(hc, token, backendUrl);
		attachementsMethod = new AllocateAttachmentIdMethod(hc, token,
				backendUrl);
		dropAttachmentsMethod = new DropAttachmentsMethod(hc, token, backendUrl);
		attachmentsMetadataMethod = new AttachmentsMetadataMethod(hc, token,
				backendUrl);
		uploadAttachmentMethod = new UploadAttachmentMethod(hc, token,
				backendUrl);
		dlAttachmentMethod = new DownloadAttachmentMethod(hc, token, backendUrl);
		dlEmlMethod = new DownloadEmlMethod(hc, token, backendUrl);
		deleteConversationMethod = new DeleteConversationMethod(hc, token,
				backendUrl);
		deleteMessageMethod = new DeleteMessageMethod(hc, token, backendUrl);
		getQuotaMethod = new GetQuotaMethod(hc, token, backendUrl);
		prepareForwardMethod = new PrepareForwardMethod(hc, token, backendUrl);
		folderService = new FolderServiceBackendProxy(serverSettings, hc,
				token, backendUrl);
		settingService = new SettingServiceBackendProxy(hc, token, backendUrl);
		purgeMethod = new PurgeMethod(hc, token, backendUrl);
		createUpdateFilter = new CreateOrUpdateFilterMethod(hc, token,
				backendUrl);
		rmFilterMethod = new RemoveFilterMethod(hc, token, backendUrl);
		listFiltersMethod = new ListFiltersMethod(hc, token, backendUrl);
		fetchVacationMethod = new FetchVacationMethod(hc, token, backendUrl);
		updateVacationMethod = new UpdateVacationMethod(hc, token, backendUrl);
		fetchForwardMethod = new FetchForwardMethod(hc, token, backendUrl);
		updateForwardMethod = new UpdateForwardMethod(hc, token, backendUrl);
		storeHistoryMethod = new StoreHistoryMethod(hc, token, backendUrl);
		pushMethod = new PushMethod(hc, token, backendUrl);
	}

	@Override
	public void logout() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					logoutMethod.logout();
				} catch (Throwable t) {
				}
				try {
					hc = null;
					mtConMan.shutdown();
				} catch (Throwable t) {
				}
			}
		}, "logout-" + getLogin());

		t.start();
	}

	@Override
	public ConversationReferenceList search(String query, int page,
			int pageLength) {
		return searchMethod.search(query, page, pageLength);
	}

	@Override
	public SendResponse send(ClientMessage cm, ReplyInfo ri, SendParameters sp) {
		return sendMethod.sendMessage(cm, ri, sp);
	}

	@Override
	public void setFlags(Set<ConversationId> conversationIds, String flag, boolean set) {
		if (set) {
			flagsMethod.setFlag(conversationIds, flag);
		} else {
			flagsMethod.unsetFlag(conversationIds, flag);
		}
	}

	@Override
	public void setFlags(String folderName, String flag, boolean set) {
		if (set) {
			flagsMethod.setFlag(folderName, flag);
		} else {
			flagsMethod.unsetFlag(folderName, flag);
		}
	}

	@Override
	public ContactGroup[] getContactGroups() {
		return contactGroupsMethod.getContactGroups();
	}

	@Override
	public UiContact[] getContacts(ContactGroup cg, String query) {
		return contactsMethod.getContacts(cg, query);
	}

	@Override
	public void createContact(UiContact ui) {
		createContactMethod.createContact(ui);
	}

	@Override
	public List<ConversationId> moveConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId) {
		return moveConversationMethod.moveConversation(origin, destination,
				conversationId, true);
	}

	@Override
	public List<ConversationId> moveConversation(String query, Folder destination) {
		return moveConversationMethod
				.moveConversation(query, destination, true);
	}

	@Override
	public List<ConversationId> copyConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId) {
		return moveConversationMethod.moveConversation(origin, destination,
				conversationId, false);
	}

	@Override
	public List<ConversationId> copyConversation(String query, Folder destination) {
		return moveConversationMethod.moveConversation(query, destination,
				false);
	}

	@Override
	public ConversationId storeMessage(Folder f, ClientMessage m, SendParameters sp) {
		return storeMethod.storeMessage(f, m, sp);
	}

	public void valueBound(HttpSessionBindingEvent event) {
		logInfo("proxyClient bound to session.");
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		logout();
		logInfo("proxyClient unbound.");
	}

	@Override
	public String allocateAttachementId() {
		return attachementsMethod.allocateAttachmentId();
	}

	@Override
	public InputStream downloadAttachement(String attachementId) {
		return dlAttachmentMethod.download(attachementId);
	}

	@Override
	public InputStream downloadEml(String folderName, String emlId) {
		return dlEmlMethod.download(folderName, emlId);
	}

	@Override
	public AttachmentMetadata[] getAttachementsMetadata(String[] attachmentId) {
		return attachmentsMetadataMethod.getMetadata(attachmentId);
	}

	@Override
	public void uploadAttachement(String attachementId,
			AttachmentMetadata meta, InputStream attachement) {
		uploadAttachmentMethod.upload(attachementId, meta, attachement);

	}

	@Override
	public void dropAttachements(String[] attachmentId) {
		dropAttachmentsMethod.dropAttachements(attachmentId);
	}

	@Override
	public QuotaInfo getQuota(String mailBox) {
		return getQuotaMethod.getQuota(mailBox);
	}

	@Override
	public void deleteConversation(List<ConversationId> conversationId) {
		deleteConversationMethod.deleteConversation(conversationId);
	}

	@Override
	public ClientMessage prepareForward(Folder folder, List<MessageId> uids) {
		return prepareForwardMethod.prepareForward(folder, uids);
	}

	@Override
	public AttachmentList getAttachmentList(Folder f, int page, int pageLength) {
		return attachmentsMetadataMethod.listAttachments(f, page, pageLength);
	}

	@Override
	public IFolderService getFolderService() {
		return folderService;
	}

	@Override
	public void purgeFolder(Folder toPurge) {
		purgeMethod.purgeFolder(toPurge);
	}

	@Override
	public ISettingService getSettingManager() {
		return settingService;
	}

	private String getTimeZone() {
		return getServerSettings().get("obm/set_timezone");
	}

	@Override
	public List<FilterDefinition> listFilters() {
		return listFiltersMethod.listFilters();
	}

	@Override
	public void removeFilter(FilterDefinition fd) {
		rmFilterMethod.remove(fd);
	}

	@Override
	public void storeFilter(FilterDefinition fd) {
		createUpdateFilter.createOrUpdate(fd);
	}

	@Override
	public void updateFilter(FilterDefinition fd) {
		createUpdateFilter.createOrUpdate(fd);
	}

	@Override
	public VacationInfo fetchVacation() {
		return fetchVacationMethod.fetchVacation();
	}

	@Override
	public void updateVacation(VacationInfo vi) {
		updateVacationMethod.updateVacation(vi);
	}

	@Override
	public ForwardInfo fetchForward() {
		return fetchForwardMethod.fetchForward();
	}

	@Override
	public void updateForward(ForwardInfo fi) {
		updateForwardMethod.updateForward(fi);
	}

	@Override
	public ConversationId moveMessage(Folder destination, ConversationId convId,
			MessageId messageId) {
		return moveMessageMethod.moveMessage(destination, convId, messageId);
	}

	@Override
	public void deleteMessage(ConversationId convId, MessageId uid) {
		deleteMessageMethod.deleteMessage(convId, uid);
	}

	@Override
	public void storeHistory(History chatHistory) {
		storeHistoryMethod.store(chatHistory);
	}
	
	public ServerEventKind fetchEvent() {
		long time = System.currentTimeMillis();
		ServerEventKind ret = pushMethod.fetchServerEvent();
		time = System.currentTimeMillis() - time;
		if (time < 2000) {
			try {
				Thread.sleep(3000);
				logger.info("push returned too fast. Forced sleep !!!!");
			} catch (InterruptedException e) {
			}
		}
		return ret;
	}

	@Override
	public void denyDispositionNotification(MessageId messageId) {
		dispositionNotificationMethod.denyDispositionNotification(messageId);
	}
	
	@Override
	public void sendDispositionNotification(MessageId messageId, String folderName) {
		dispositionNotificationMethod.sendDispositionNotification(messageId, folderName);
	}
	
	@Override
	public String getEmailAddress() {
		return getLogin() + "@" + getDomain();
	}
	
}
