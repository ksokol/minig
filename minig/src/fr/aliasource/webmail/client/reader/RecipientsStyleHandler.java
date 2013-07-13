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

package fr.aliasource.webmail.client.reader;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.EmailAddress;

public class RecipientsStyleHandler {

	private Map<EmailAddress, String> styles;

	public RecipientsStyleHandler(ConversationContent cc) {
		styles = new HashMap<EmailAddress, String>();

		Set<EmailAddress> sa = new LinkedHashSet<EmailAddress>();
		for (ClientMessage cm : cc.getMessages()) {
			sa.add(cm.getSender());
			for (EmailAddress a : cm.getTo()) {
				sa.add(a);
			}
			for (EmailAddress a : cm.getCc()) {
				sa.add(a);
			}
			for (EmailAddress a : cm.getBcc()) {
				sa.add(a);
			}
		}
		String style = "recipientLabel";
		int i = 1;
		for (EmailAddress a : sa) {
			styles.put(a, style + i);
			i++;
		}
	}

	public String getStyle(EmailAddress sender) {
		return styles.get(sender);
	}

}
