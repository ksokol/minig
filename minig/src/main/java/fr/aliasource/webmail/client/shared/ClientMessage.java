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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ClientMessage implements Serializable {

	private static final long serialVersionUID = 3674683234342852514L;

	private String subject;
	private Body body;
	private String[] attachements;
	private EmailAddress sender;
	private List<EmailAddress> to;
	private List<EmailAddress> cc;
	private List<EmailAddress> bcc;
	private List<EmailAddress> dispositionNotification;
	private Date date;
	private String mailer;
	private MessageId uid;
	private String folderName;
	private Set<ClientMessage> fwdMessage;
	private Boolean hasInvitation;
	private ConversationId convId;
	private ConversationId messageId;
	private boolean read;
	private boolean starred;
	private boolean answered;
	private boolean highPriority;
	
	
	private boolean loaded;

	public ClientMessage() {
		this(new EmailAddress("John doe", "john.doe@foobar.org"),
				Arrays.asList(
						new EmailAddress("Happy Recipient",	"happy.recipient@foobar.org")), 
						"subject", new Body("text/plain", "body"), new String[0], new Date(),
				"MiniG Webmail", false, null);
	}

	public ClientMessage(EmailAddress sender, List<EmailAddress> recipients, String subject,
			Body body, String[] attachements, Date date, String mailer,
			ConversationId messageId) {
		this(sender, recipients, subject, body, attachements, date, mailer,
				false, messageId);
		this.loaded = true;
	}

	private ClientMessage(EmailAddress sender, List<EmailAddress> recipients, String subject,
			Body body, String[] attachements, Date date, String mailer,
			Boolean invitation, ConversationId messageId) {
		super();
		this.subject = subject;
		this.body = body;
		this.attachements = attachements;
		this.sender = sender;
		this.to = recipients;
		this.cc = new ArrayList<EmailAddress>(0);
		this.bcc = new ArrayList<EmailAddress>(0);
		this.date = date;
		this.mailer = mailer;
		this.fwdMessage = new HashSet<ClientMessage>();
		this.hasInvitation = invitation;
		if (messageId != null) {
			this.messageId = messageId;
		} else if (sender != null) {
			this.messageId = new ConversationId("<" + System.currentTimeMillis() + "-"
					+ sender.getEmail() + ">");
		}
		this.read = true;
	}

	public String getSubject() {
		return subject;
	}

	public Body getBody() {
		return body;
	}

	public String[] getAttachments() {
		return attachements;
	}

	public EmailAddress getSender() {
		return sender;
	}

	public Date getDate() {
		return date;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public void setAttachements(String[] attachements) {
		this.attachements = attachements;
	}

	public void setSender(EmailAddress sender) {
		this.sender = sender;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMailer() {
		return this.mailer;
	}

	public void setMailer(String mailer) {
		this.mailer = mailer;
	}

	public MessageId getUid() {
		return uid;
	}

	public void setUid(MessageId uid) {
		this.uid = uid;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void setFwdMessage(Set<ClientMessage> fwdMessage) {
		this.fwdMessage = fwdMessage;
	}

	public Set<ClientMessage> getFwdMessages() {
		return fwdMessage;
	}

	public void addFwdMessage(ClientMessage cm) {
		fwdMessage.add(cm);
	}

	public List<EmailAddress> getTo() {
		return to;
	}

	public void setTo(List<EmailAddress> to) {
		this.to = to;
	}

	public List<EmailAddress> getCc() {
		return cc;
	}

	public void setCc(List<EmailAddress> cc) {
		if (cc != null) {
			this.cc = cc;
		}
	}

	public List<EmailAddress> getBcc() {
		return bcc;
	}

	public void setBcc(List<EmailAddress> bcc) {
		if (bcc != null) {
			this.bcc = bcc;
		}
	}

	public Boolean getHasInvitation() {
		return hasInvitation;
	}

	public void setHasInvitation(Boolean hasInvitation) {
		this.hasInvitation = hasInvitation;
	}

	public ConversationId getConvId() {
		return convId;
	}

	public void setConvId(ConversationId convId) {
		this.convId = convId;
	}

	public ConversationId getMessageId() {
		if (messageId != null) {
			return messageId;
		} else if (sender != null) {
			return new ConversationId(
					"<" + System.currentTimeMillis() + "-" + sender.getEmail() + ">");
		} else {
			return new ConversationId("<" + System.currentTimeMillis() + "@messageid.minig.org>");
		}
	}

	public void setMessageId(ConversationId messageId) {
		this.messageId = messageId;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
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

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	public List<EmailAddress> getDispositionNotification() {
		return dispositionNotification;
	}
	
	public void setDispositionNotification(
			List<EmailAddress> dispositionNotification) {
		this.dispositionNotification = dispositionNotification;
	}
}
