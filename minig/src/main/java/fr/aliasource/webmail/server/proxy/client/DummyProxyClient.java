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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.shared.AttachmentList;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.QuotaInfo;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;
import fr.aliasource.webmail.client.shared.ServerEventKind;
import fr.aliasource.webmail.client.shared.VacationInfo;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.chat.History;

/**
 * This code returns fake data. It is designed to test the GWT ui without
 * running the backend and an IMAP server.
 * 
 * @author tom
 * 
 */
public class DummyProxyClient implements IAccount, HttpSessionBindingListener {

	private ProxyConfig cfg;
	private String login;
	private String domain;
	private DummyDataProvider ddp;
	private DummyFolderService dfs;
	private DummySettingService dss;

	private EmailAddress me;
	private Log logger = LogFactory.getLog(getClass());

	public DummyProxyClient(ProxyConfig cfg) {
		this.cfg = cfg;
		this.ddp = new DummyDataProvider();
		this.dfs = new DummyFolderService(ddp);
	}

	/**
	 * Generates fake messages to test the conversation reader
	 * 
	 * @see fr.aliasource.webmail.server.proxy.client.IAccount#fetchMessages(fr.aliasource.webmail.client.shared.Folder,
	 *      java.util.List)
	 */
	public ClientMessage[] fetchMessages(Folder f, List<MessageId> messageIds) {
		ClientMessage[] ret = new ClientMessage[messageIds.size()];
		int i = 0;
		for (MessageId mid : messageIds) {
			Body b = new Body();
			b.setPlain("Mostly empty email body");
			b.setHtml("<pre>Mostly empty email body</pre>");

			ClientMessage cm = new ClientMessage(new EmailAddress("Sender of " + mid.getMessageId(),
					mid.getMessageId() + "@zz.com"), Arrays.asList(me), "Fake email " + mid.getMessageId()
					+ " " + i + " in folder '" + f.getDisplayName() + "'", b,
					new String[0], ddp.randDate(), "MiniG Webmail", new ConversationId("<"
							+ System.nanoTime() + "@dummy.minig.org>"));
			ret[i++] = cm;
		}
		return ret;
	}

	public ConversationReference findConversation(ConversationId convId) {
		return ddp.getById(convId);
	}

	public String getDomain() {
		return domain;
	}

	public String getLogin() {
		return login;
	}

	public List<Completion> getPossibleCompletions(String completionType,
			String shortQuery, int maxResponse) {
		List<Completion> ret = null;
		if (maxResponse < 25) {
			ret = new ArrayList<Completion>(25);
		} else {
			ret = new LinkedList<Completion>();
		}

		if ("emails".equals(completionType)) {
			ret.add(new Completion("bar@aliasource.fr", "Bar"));
			ret.add(new Completion("foo@aliasource.fr", "Foo"));
			ret.add(new Completion("foobar@aliasource.fr", "Foo Bar"));
		}
		return ret;
	}

	public Map<String, String> getServerSettings() {
		return new HashMap<String, String>();
	}

	@Override
	public ConversationReferenceList listConversations(long lastSeendVersion,
			Folder folder, int page, int pageLength) {
		if (logger.isInfoEnabled()) {
			logInfo("listConversations(" + folder.getName() + ", " + page
					+ ", " + pageLength + ")");
		}

		List<ConversationReference> l = ddp.get(folder);
		List<ConversationReference> sublist = sublist(l, page, pageLength);

		ConversationReferenceList crl = new ConversationReferenceList(sublist,
				l.size());
		return crl;
	}

	public void login(String login, String domain, String password)
			throws ClientException {
		logInfo("Login attempt on proxy at " + cfg.getProxyUrl());
		this.login = login;
		this.domain = domain;
		this.me = new EmailAddress("Me", "" + login + "@" + domain);
	}

	private void logInfo(String info) {
		if (login != null) {
			logger.info("[" + login + "@" + domain + "] " + info);
		} else {
			logger.info("[anonymous] " + info);
		}
	}

	public void logout() {
		logInfo("Logout attempt on proxy at " + cfg.getProxyUrl());
	}

	public ConversationReferenceList search(String query, int page,
			int pageLength) {
		if (logger.isInfoEnabled()) {
			logInfo("search(" + query + ", " + page + ", " + pageLength + ")");
		}

		List<ConversationReference> l = ddp.getSearch();
		List<ConversationReference> sublist = sublist(l, page, pageLength);

		ConversationReferenceList crl = new ConversationReferenceList(sublist,
				l.size());
		return crl;
	}

	public SendResponse send(ClientMessage cm, ReplyInfo ri, SendParameters sp) {
		logInfo("send(" + cm.getSubject() + ")");
		return new SendResponse();
	}

	/**
	 * Extracts conversations for the asked page
	 * 
	 * @param l
	 * @param page
	 * @param pageLength
	 * @return
	 */
	private <T> List<T> sublist(List<T> l, int page, int pageLength) {
		List<T> ret = new LinkedList<T>();
		if (l != null) {
			int idxStart = (page - 1) * pageLength;
			int idxEnd = Math.min(idxStart + pageLength, l.size());

			for (int i = idxStart; i < idxEnd; i++) {
				ret.add(l.get(i));
			}
		}
		return ret;
	}

	public void valueBound(HttpSessionBindingEvent event) {
		logInfo("proxyClient to session.");
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		logout();
		logInfo("proxyClient unbound.");
	}

	public void setFlags(Set<ConversationId> conversationIds, String flag, boolean set) {
		for (ConversationId id : conversationIds) {
			ConversationReference ref = ddp.getById(id);
			if (SetFlags.READ.equals(flag)) {
				ref.setRead(set);
			}
		}
	}

	public ContactGroup[] getContactGroups() {
		return ddp.getContactGroups();
	}

	public UiContact[] getContacts(ContactGroup cg, String query) {
		return ddp.getContacts(cg, query);
	}

	@Override
	public List<ConversationId> moveConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId) {
		return conversationId;
	}

	public List<ConversationId> moveConversation(String query, Folder destination) {
		return null;
	}

	@Override
	public ConversationId storeMessage(Folder f, ClientMessage m, SendParameters sp) {
		slowdown(2);
		return ddp.store(f, m);
	}

	private void slowdown(int seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public String allocateAttachementId() {
		return ddp.allocateAttachementId();
	}

	@Override
	public InputStream downloadAttachement(String attachementId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttachmentMetadata[] getAttachementsMetadata(String[] attachementId) {
		AttachmentMetadata[] ret = new AttachmentMetadata[attachementId.length];
		for (int i = 0; i < attachementId.length; i++) {
			ret[i] = new AttachmentMetadata();
			ret[i].setFileName(attachementId[i] + ".name");
			ret[i].setSize(ddp.randInt(1024 * 1014));
			ret[i].setMime("application/octet-stream");
		}
		return ret;
	}

	@Override
	public void uploadAttachement(String attachementId,
			AttachmentMetadata meta, InputStream attachement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropAttachements(String[] attachementId) {
		for (String atId : attachementId) {
			ddp.dropAttachement(atId);
		}

	}

	@Override
	public QuotaInfo getQuota(String mailBox) {

		return null;
	}

	@Override
	public void deleteConversation(List<ConversationId> conversationId) {

	}

	@Override
	public ClientMessage prepareForward(Folder folder, List<MessageId> uid) {
		return new ClientMessage();
	}

	@Override
	public AttachmentList getAttachmentList(Folder folder, int page,
			int pageLength) {
		if (logger.isInfoEnabled()) {
			logInfo("listAttachments(" + folder.getName() + ", " + page + ", "
					+ pageLength + ")");
		}

		List<AttachmentMetadata> l = ddp.getAttachments(folder);
		List<AttachmentMetadata> sublist = sublist(l, page, pageLength);

		AttachmentList list = new AttachmentList(sublist, l.size());
		return list;
	}

	@Override
	public IFolderService getFolderService() {
		return dfs;
	}

	@Override
	public void purgeFolder(Folder toPurge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlags(String folderName, String flag, boolean set) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getToken() {
		return "dummy@" + System.currentTimeMillis();
	}

	@Override
	public ISettingService getSettingManager() {
		return dss;
	}

	@Override
	public Map<String, String> updateServerSettings() {
		return new HashMap<String, String>();
	}

	@Override
	public List<FilterDefinition> listFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFilter(FilterDefinition fd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeFilter(FilterDefinition fd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFilter(FilterDefinition fd) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ConversationId> copyConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId) {
		return null;
	}

	@Override
	public List<ConversationId> copyConversation(String query, Folder destination) {
		return null;
	}

	@Override
	public VacationInfo fetchVacation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateVacation(VacationInfo vi) {
		// TODO Auto-generated method stub

	}

	@Override
	public ForwardInfo fetchForward() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateForward(ForwardInfo fi) {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream downloadEml(String folderName, String emlId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConversationId moveMessage(Folder destination, ConversationId convId, MessageId uid) {
		return null;
	}

	@Override
	public void deleteMessage(ConversationId convId, MessageId uid) {
	}

	@Override
	public void storeHistory(History chatHistory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createContact(UiContact ui) {
		// TODO Auto-generated method stub
		
	}
	
	public ServerEventKind fetchEvent() {
		return ServerEventKind.NONE;
	}

	@Override
	public ConversationContent fetchUnreadMessages(ConversationId convId) {
		return null;
	}

	@Override
	public void denyDispositionNotification(MessageId messageId) {
	}
	
	@Override
	public void sendDispositionNotification(MessageId messageId,
			String folderName) {
	}
	
	@Override
	public String getEmailAddress() {
		return null;
	}
}
