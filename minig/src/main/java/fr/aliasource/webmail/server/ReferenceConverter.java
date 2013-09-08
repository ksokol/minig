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

import java.util.Set;

import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;

public class ReferenceConverter {

	public ReferenceConverter() {

	}

	public Conversation referenceToConversation(ConversationReference cr) {
		String abrev = getShortParticipants(cr.getParticipants());
		String full = getFullParticipants(cr.getParticipants());
		String preview = cr.getPreview();
		if (preview == null || preview.length() == 0) {
			preview = "preview not available for this conversation";
		}

		Conversation ret = new Conversation(cr.getId(), cr.getTitle(), !cr
				.isRead(), cr.getLastMessageDate().getTime(), abrev, full, cr
				.getMessageIds().size(), preview, cr.getSourceFolderName(), cr
				.hasAttachements(), cr.hasInvitation(), cr.isStarred(), cr
				.isAnswered(), cr.isHighPriority());

		ret.setPrev(cr.getPrev());
		ret.setNext(cr.getNext());

		return ret;
	}

	protected String getFullParticipants(Set<EmailAddress> participants) {
		String ret = "";
		int i = 0;
		for (EmailAddress a : participants) {
			if (i > 0) {
				ret += ", ";
			}
			ret += a.getDisplay();
			i++;
		}
		return ret;
	}

	protected String getShortParticipants(Set<EmailAddress> participants) {
		String ret = getFullParticipants(participants);
		if (ret.length() < 20) {
			return ret;
		} else if (participants.size() == 1) {
			return ret.substring(0, 17) + "...";
		}

		int i = 0;
		for (EmailAddress a : participants) {
			if (i > 0) {
				ret += ", ";
			}
			ret += a.getDisplay().split(" ")[0];
			i++;
		}
		if (ret.length() > 20) {
			ret = ret.substring(0, 17) + "...";
		}
		return ret;
	}

}
