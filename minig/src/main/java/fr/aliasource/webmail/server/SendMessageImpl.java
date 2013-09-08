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

import java.util.HashSet;

import fr.aliasource.webmail.client.rpc.SendMessage;
import fr.aliasource.webmail.client.rpc.SendResponse;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;

public class SendMessageImpl extends SecureAjaxCall implements SendMessage {

	private static final long serialVersionUID = 1788071651416798650L;

	@Override
	public SendResponse sendMessage(ClientMessage mail, ReplyInfo ri,
			SendParameters sp) {
		SendResponse sr = getAccount().send(mail, ri, sp);

		if (sr.isOk() && ri != null && ri.getConvId() != null) {
			HashSet<ConversationId> convs = new HashSet<ConversationId>();
			convs.add(ri.getConvId());
			getAccount().setFlags(convs, "answered", true);
		}

		return sr;
	}

}
