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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.rpc.UseCachedData;
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
import fr.aliasource.webmail.server.proxy.client.http.ProxyClient;

/**
 * This the API to interact with the backend system. There are two
 * implementations : {@link DummyProxyClient} and {@link ProxyClient}. The first
 * one is suitable for UI testing, while the other one connects the real MiniG
 * backend.
 * 
 * @author tom
 * 
 */
public interface IAccount {

	/**
	 * Gets a new uid suitable for adding a new attachement.
	 * 
	 * WARNING: this kind of API is completly unsecure if SSL is not used
	 * between client and minig frontend.
	 * 
	 * @return an attachement uid usable in the given session
	 */
	String allocateAttachementId();

	String getToken();

	void deleteConversation(List<ConversationId> conversationId);

	/**
	 * @param attachementId
	 */
	InputStream downloadAttachement(String attachementId);

	/**
	 * Removes completely some attachements. This is only possible when
	 * composing messages.
	 * 
	 * @param attachementIds
	 */
	void dropAttachements(String[] attachementIds);

	ClientMessage[] fetchMessages(Folder f, List<MessageId> messageIds);

	ConversationContent fetchUnreadMessages(ConversationId convId);

	ConversationReference findConversation(ConversationId convId);

	/**
	 * Get info about attachements
	 * 
	 * @param attachementId
	 * @return
	 */
	AttachmentMetadata[] getAttachementsMetadata(String[] attachementId);

	AttachmentList getAttachmentList(Folder f, int page, int pageLength);

	ContactGroup[] getContactGroups();

	UiContact[] getContacts(ContactGroup cg, String query);

	/**
	 * 
	 * 
	 * @return the domain of the user
	 */
	String getDomain();

	IFolderService getFolderService();

	ISettingService getSettingManager();

	/**
	 * @return user login, without the right part (ie. the @ domain part)
	 */
	String getLogin();

	String getEmailAddress();
	
	List<Completion> getPossibleCompletions(String completionType,
			String shortQuery, int maxResponse);

	QuotaInfo getQuota(String mailBox);

	Map<String, String> getServerSettings();

	Map<String, String> updateServerSettings();

	ConversationReferenceList listConversations(long lastSeenVersion,
			Folder folder, int page, int pageLength) throws UseCachedData;

	void login(String login, String domain, String password)
			throws ClientException;

	void logout();

	List<ConversationId> moveConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId);

	List<ConversationId> moveConversation(String query, Folder destination);

	List<ConversationId> copyConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId);

	ConversationId moveMessage(Folder destination, ConversationId convId, MessageId messageId);

	void deleteMessage(ConversationId convId, MessageId uid);

	List<ConversationId> copyConversation(String query, Folder destination);

	ClientMessage prepareForward(Folder folder, List<MessageId> uid);

	void purgeFolder(Folder toPurge);

	ConversationReferenceList search(String query, int page, int pageLength);

	SendResponse send(ClientMessage cm, ReplyInfo info, SendParameters sp);

	void setFlags(Set<ConversationId> conversationIds, String flag, boolean set);

	void setFlags(String folderName, String flag, boolean set);

	ConversationId storeMessage(Folder f, ClientMessage m, SendParameters sp);

	/**
	 * Attachement upload method.
	 * 
	 * @param attachementId
	 * @param meta
	 * @param attachement
	 */
	void uploadAttachement(String attachementId, AttachmentMetadata meta,
			InputStream attachement);

	// FILTER API

	void storeFilter(FilterDefinition fd);

	List<FilterDefinition> listFilters();

	void updateFilter(FilterDefinition fd);

	void removeFilter(FilterDefinition fd);

	VacationInfo fetchVacation();

	void updateVacation(VacationInfo vi);

	ForwardInfo fetchForward();

	void updateForward(ForwardInfo fi);

	InputStream downloadEml(String folderName, String emlId);

	void storeHistory(History chatHistory);

	void createContact(UiContact ui);
	
	ServerEventKind fetchEvent();

	void sendDispositionNotification(MessageId messageId, String folderName);
	
	void denyDispositionNotification(MessageId messageId);
	
}
