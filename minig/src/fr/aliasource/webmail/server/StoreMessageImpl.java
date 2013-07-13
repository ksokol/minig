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

import java.util.Arrays;
import java.util.List;

import fr.aliasource.webmail.client.rpc.StoreMessage;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.SendParameters;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class StoreMessageImpl extends SecureAjaxCall implements StoreMessage {

	private static final long serialVersionUID = -5149625100446081765L;

	public List<ConversationId> moveConversation(Folder[] origin, Folder destination,
			List<ConversationId> conversationId, boolean move) {
		if (move) {
			return getAccount().moveConversation(origin, destination,
					conversationId);
		} else {
			return getAccount().copyConversation(origin, destination,
					conversationId);
		}
	}

	public List<ConversationId> moveConversation(String query, Folder destination,
			boolean move) {
		if (move) {
			return getAccount().moveConversation(query, destination);
		} else {
			return getAccount().copyConversation(query, destination);
		}
	}

	@Override
	public ConversationId storeDraftMessage(ClientMessage m, SendParameters sp) {
		IAccount ac = getAccount();

		String sub = m.getSubject();
		if (sub == null || sub.isEmpty()) {
			m.setSubject("[Empty Subject]");
		}

		List<EmailAddress> recipients = m.getTo();
		if (recipients.isEmpty()) {
			m.setTo(Arrays.asList(new EmailAddress("", "")));
		}

		return ac.storeMessage(ac.getFolderService().getDraftFolder(), m, sp);
	}

	@Override
	public ConversationId storeSentMessage(ClientMessage m, SendParameters sp) {
		IAccount ac = getAccount();
		return ac.storeMessage(ac.getFolderService().getSentFolder(), m, sp);
	}

	@Override
	public ConversationId storeTemplateMessage(ClientMessage m, SendParameters sp) {
		IAccount ac = getAccount();

		String sub = m.getSubject();
		if (sub == null || sub.isEmpty()) {
			m.setSubject("[Empty Subject]");
		}

		List<EmailAddress> recipients = m.getTo();
		if (recipients.isEmpty()) {
			m.setTo(Arrays.asList(new EmailAddress("", "")));
		}

		return ac.storeMessage(ac.getFolderService().getTemplateFolder(), m, sp);
	}

	public List<ConversationId> trashConversation(Folder[] origin, List<ConversationId> conversationId) {
		IAccount ac = getAccount();
		// FIXME handle chats
		return ac.moveConversation(origin, ac.getFolderService()
				.getTrashFolder(), conversationId);
	}

	@Override
	public List<ConversationId> trashConversation(String query) {
		IAccount ac = getAccount();
		return ac.moveConversation(query, ac.getFolderService()
				.getTrashFolder());

	}

	@Override
	public void deleteConversation(List<ConversationId> conversationId) {
		IAccount ac = getAccount();
		if (ac != null) {
			ac.deleteConversation(conversationId);
		}
	}

	@Override
	public void purgeFolder(Folder toPurge) {
		IAccount ac = getAccount();
		if (ac != null) {
			ac.purgeFolder(toPurge);
		}
	}

	@Override
	public ConversationId trashMessage(ConversationId convId, MessageId uid) {
		IAccount ac = getAccount();
		return ac.moveMessage(ac.getFolderService().getTrashFolder(), convId,
				uid);
	}

	@Override
	public void deleteMessage(ConversationId convId, MessageId uid) {
		IAccount ac = getAccount();
		ac.deleteMessage(convId, uid);
	}

}
