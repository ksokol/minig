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

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.SendParameters;

public interface StoreMessageAsync {

	void storeSentMessage(ClientMessage m, SendParameters sp, AsyncCallback<ConversationId> ac);

	void storeDraftMessage(ClientMessage m, SendParameters sp, AsyncCallback<ConversationId> ac);

	void storeTemplateMessage(ClientMessage m, SendParameters sp, AsyncCallback<ConversationId> ac);

	void trashConversation(Folder[] origins, List<ConversationId> conversationId,
			AsyncCallback<List<ConversationId>> ac);

	void trashConversation(String query, AsyncCallback<List<ConversationId>> ac);

	void moveConversation(Folder[] origins, Folder destination,
			List<ConversationId> conversationId, boolean move, AsyncCallback<List<ConversationId>> ac);

	void moveConversation(String query, Folder destination, boolean move,
			AsyncCallback<List<ConversationId>> ac);

	void deleteConversation(List<ConversationId> conversationId, AsyncCallback<Void> success);

	void purgeFolder(Folder toPurge, AsyncCallback<Void> voidCall);

	void trashMessage(ConversationId convId, MessageId uid, AsyncCallback<ConversationId> callback);

	void deleteMessage(ConversationId convId, MessageId uid, AsyncCallback<Void> success);

}
