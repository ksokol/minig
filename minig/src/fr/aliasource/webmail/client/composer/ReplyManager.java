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

package fr.aliasource.webmail.client.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.ReplyInfo;

public class ReplyManager {

	private EmailAddress me;

	public ReplyManager(View ui) {
		this.me = WebmailController.get().getIdentity();
	}

	public ClientMessage prepareReply(ClientMessage message, boolean all) {
		ClientMessage ret = new ClientMessage();
		if (!all) {
			ret.setTo(Arrays.asList(message.getSender()));
		} else {
			Set<EmailAddress> ads = new HashSet<EmailAddress>();
			// reply to all recipients except me
			addRecips(ads, message.getTo());
			addRecips(ads, message.getCc());
			addRecips(ads, message.getBcc());
			ads.add(message.getSender());
			ret.setTo(new ArrayList<EmailAddress>(ads));
		}
		ret.setSubject(replySubject(message.getSubject()));
		ret.setAttachements(new String[0]);
		ret.setBody(quoteForReply(message));
		return ret;
	}

	private void addRecips(Set<EmailAddress> ads, List<EmailAddress> rcpt) {
		for (int i = 0; i < rcpt.size(); i++) {
			EmailAddress ad = rcpt.get(i);
			if (!ad.equals(me)) {
				GWT.log("replyAll: '" + ad.getEmail() + "'", null);
				ads.add(ad);
			} else {
				GWT.log("replyAll discarded '" + ad.getEmail() + "'", null);
			}
		}
	}

	private String replySubject(String subject) {
		if (subject == null) {
			return "RE: ";
		}
		if (subject.toLowerCase().startsWith("re:")) {
			return subject;
		} else {
			return "RE: " + subject;
		}
	}

	private Body quoteForReply(ClientMessage message) {
		String endLine = "\n";
		String plainB = message.getBody().getPlain();
		StringBuffer quote = new StringBuffer(2 * plainB.length());
		//insert two empty lines to write the reply
		quote.append(endLine).append(endLine);
		quote.append(I18N.strings.quoteSender(message.getSender().getDisplay())
				+ endLine);
		String[] lines = plainB.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].trim().isEmpty()) {
				quote.append("> ").append(lines[i].trim()).append(endLine);
			}
		}
		quote.append(endLine);

		String quoted = quote.toString();
		Body ret = new Body();
		ret.setHtml(new PlainToHTMLConverter().convert(quoted));

		return ret;
	}

	public void prepareForward(final ClientMessage message,
			final QuickReply quickReply) {
		AsyncCallback<ClientMessage> ac = new AsyncCallback<ClientMessage>() {
			private void forward(ClientMessage cm) {
				cm.setBody(new Body("text/plain", ""));
				quickReply.loadDraft(cm, null);
				quickReply.focusComposer();
			}

			public void onFailure(Throwable caught) {
				GWT.log("prepare forward failed: " + caught.getMessage(), null);
				ClientMessage cm = new ClientMessage();
				cm.setSender(me);
				cm.setSubject(forwardSubject(message.getSubject()));
				forward(cm);
			}

			public void onSuccess(ClientMessage result) {
				GWT.log("prepareForward success", null);
				result.setSender(me);
				result.setSubject(forwardSubject(message.getSubject()));
				forward(result);
			}
		};
		AjaxCall.composerParser.prepareForward(message, ac);
	}

	private String forwardSubject(String subject) {
		if (subject == null) {
			return "[Fwd: ]";
		}
		if (subject.toLowerCase().startsWith("[fwd:")) {
			return subject;
		} else {
			return "[Fwd: " + subject + "]";
		}
	}

	public ReplyInfo getInfo(ClientMessage message) {
		ReplyInfo ri = new ReplyInfo(new Folder(message.getFolderName()),
				message.getUid(), message.getConvId());
		return ri;
	}

}
