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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.rpc.ShowConversation;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class ShowConversationImpl extends SecureAjaxCall implements
		ShowConversation {

	private ExecutorService exec;

	public ShowConversationImpl() {
		exec = Executors.newFixedThreadPool(5);
	}

	private static final long serialVersionUID = 940632500727855731L;

	public ConversationContent show(ConversationId convId) {

		final IAccount account = getAccount();
		if (account == null) {
			logger.error("no account in session");
			return null;
		}

		// reset last_seen_version to force re-query on "back to inbox" links
		getThreadLocalRequest().getSession().setAttribute("last_seen_version",
				"0");


		if (convId.hasFolder()) {
			return loadEmailConversation(convId, account);
		} else {
			return loadChatHistory(convId, account);
		}
	}

	private ConversationContent loadEmailConversation(ConversationId convId,
			final IAccount account) {
		ConversationContent ret = account.fetchUnreadMessages(convId);

		final Conversation c = ret.getConversation();

		logger.info("["+account.getLogin()+"][" + convId + "] conv content with "
				+ ret.getMessages().length + " msgs");
		if (c.isUnread()) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Set<ConversationId> ids = new HashSet<ConversationId>();
						ids.add(c.getId());
						account.setFlags(ids, SetFlags.READ, true);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
			});
		}

		return ret;
	}

	private ConversationContent loadChatHistory(ConversationId convId,
			final IAccount account) {
		ConversationReferenceList list = 
			account.search("id:" + convId.getConversationId(), 1, 2);
		ConversationReference cr = list.getPage().get(0);
		Conversation c = new ReferenceConverter().referenceToConversation(cr);

		Body body = new Body("text/html", cr.getHtml());
		List<EmailAddress> recipients = new ArrayList<EmailAddress>(cr.getParticipants());
		EmailAddress sender = recipients.get(0);
		ClientMessage cm = new ClientMessage(sender, recipients,
				cr.getTitle(), body, new String[] {},
				cr.getLastMessageDate(), "MiniG chat", convId);

		return new ConversationContent(c, new ClientMessage[] { cm });
	}

	@Override
	public ClientMessage loadMessage(ClientMessage toLoad) {
		ClientMessage[] loaded = getAccount().fetchMessages(
				new Folder(toLoad.getFolderName()),
				Arrays.asList(toLoad.getUid()));
		
		return loaded[0];
	}
}
