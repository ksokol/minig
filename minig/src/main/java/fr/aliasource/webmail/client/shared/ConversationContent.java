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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class ConversationContent implements Serializable {

	private static final long serialVersionUID = 1939187144555283599L;
	private ClientMessage[] messages;
	private Conversation conversation;

	public ConversationContent() {
	}

	public ConversationContent(Conversation c, ClientMessage[] messages) {
		this.conversation = c;
		setMessages(messages);
	}

	public ClientMessage[] getMessages() {
		return messages;
	}

	public String getTitle() {
		return conversation.getTitle();
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public void setMessages(ClientMessage[] messages) {
		this.messages = messages;
		for (ClientMessage cm : messages) {
			cm.setConvId(conversation.getId());
		}
	}
	
	public void destroy() {
		messages = null;
		conversation = null;
	}

}
