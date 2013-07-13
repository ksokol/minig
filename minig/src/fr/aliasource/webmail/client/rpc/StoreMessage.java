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

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.SendParameters;

/**
 * Message actions that requires interaction with the mail store
 * 
 * @author tom
 * 
 */
@RemoteServiceRelativePath("store")
public interface StoreMessage extends RemoteService {

	/**
	 * Returns the conversation id of the stored message
	 */
	ConversationId storeSentMessage(ClientMessage m, SendParameters sp);

	/**
	 * Returns the conversation id of the stored message
	 */
	ConversationId storeDraftMessage(ClientMessage m, SendParameters sp);

	/**
	 * Returns the conversation id of the stored message
	 */
	ConversationId storeTemplateMessage(ClientMessage m, SendParameters sp);

	/**
	 * Returns the conversation id of the stored message
	 */
	List<ConversationId> trashConversation(Folder[] origins, List<ConversationId> conversationId);

	/**
	 * Returns the message id of the stored message
	 */
	ConversationId trashMessage(ConversationId convId, MessageId uid);

	/**
	 * Returns the conversation id of the stored message
	 */
	List<ConversationId> trashConversation(String query);

	/**
	 * Returns the new conversationIds
	 */
	List<ConversationId> moveConversation(Folder[] origins, Folder destination,
			List<ConversationId> conversationId, boolean move);

	/**
	 * Returns the new conversationIds
	 */
	List<ConversationId> moveConversation(String query, Folder destination, boolean move);

	void deleteConversation(List<ConversationId> conversationId);

	void purgeFolder(Folder toPurge);

	void deleteMessage(ConversationId convId, MessageId uid);

}
