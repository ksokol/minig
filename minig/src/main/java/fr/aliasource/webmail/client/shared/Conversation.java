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

import fr.aliasource.webmail.server.proxy.client.ConversationReference;

/**
 * Conversation object used by the GWT ui. The server side code uses
 * {@link ConversationReference} to benefits from generics.
 * 
 * @author tom
 * 
 */
public class Conversation implements Serializable {

	private static final long serialVersionUID = -8069160141588763238L;

	private ConversationId id;
	private String title;
	private boolean unread;
	private long date;
	private String participantsAbrev;
	private String participantsFull;
	private int messageCount;
	private String preview;
	private String sourceFolder;
	private boolean hasAttachements;
	private boolean hasInvitation;
	private boolean starred;
	private boolean answered;

	private ConversationId next;
	private ConversationId prev;

	private boolean highPriority;

	public Conversation() {

	}

	public Conversation(ConversationId id, String title, boolean unread, long date,
			String participantsAbrev, String participantsFull,
			int messageCount, String preview, String sourceFolder,
			boolean hasAttachements, boolean hasInvitation, boolean starred,
			boolean answered, boolean highPriority) {
		this.id = id;
		this.title = title;
		this.unread = unread;
		this.date = date;
		this.participantsAbrev = participantsAbrev;
		this.participantsFull = participantsFull;
		this.messageCount = messageCount;
		this.preview = preview;
		this.sourceFolder = sourceFolder;
		this.hasAttachements = hasAttachements;
		this.hasInvitation = hasInvitation;
		this.starred = starred;
		this.answered = answered;
		this.highPriority = highPriority;
	}

	public ConversationId getId() {
		return id;
	}

	public void setId(ConversationId id) {
		this.id = id;
		if (id != null) {
			this.sourceFolder = id.getSourceFolder();
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public long getDate() {
		return date;
	}

	public String getParticipantsAbrev() {
		return participantsAbrev;
	}

	public String getParticipantsFull() {
		return participantsFull;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public String getPreview() {
		return preview;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public boolean hasAttachements() {
		return hasAttachements;
	}

	public boolean isStarred() {
		return starred;
	}

	public boolean hasInvitation() {
		return hasInvitation;
	}

	public void setHasInvitation(boolean hasInvitation) {
		this.hasInvitation = hasInvitation;
	}

	public ConversationId getNext() {
		return next;
	}

	public void setNext(ConversationId next) {
		this.next = next;
	}

	public ConversationId getPrev() {
		return prev;
	}

	public void setPrev(ConversationId prev) {
		this.prev = prev;
	}

	public boolean isAnswered() {
		return answered;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

}
