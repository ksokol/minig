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

import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;

public class ConversationReference {

	private String title;
	private ConversationId id;
	private Set<EmailAddress> participants;
	private Date lastMessageDate;
	private boolean read;
	private boolean starred;
	private boolean answered;
	private boolean highPriority;
	private List<MessageId> messageIds;
	private String preview;
	private String sourceFolderName;
	private boolean hasAttachements;
	private boolean hasInvitation;

	private ConversationId next;
	private ConversationId prev;

	private String html;

	public ConversationReference() {

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ConversationId getId() {
		return id;
	}

	public void setId(ConversationId id) {
		this.id = id;
	}

	public Set<EmailAddress> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<EmailAddress> participants) {
		this.participants = participants;
	}

	public Date getLastMessageDate() {
		return lastMessageDate;
	}

	public void setLastMessageDate(Date lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public List<MessageId> getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(List<MessageId> messageIds) {
		this.messageIds = messageIds;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getSourceFolderName() {
		return sourceFolderName;
	}

	public void setSourceFolderName(String sourceFolderName) {
		this.sourceFolderName = sourceFolderName;
	}

	public boolean hasAttachements() {
		return hasAttachements;
	}

	public void setHasAttachements(boolean hasAttachements) {
		this.hasAttachements = hasAttachements;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
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

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

}
