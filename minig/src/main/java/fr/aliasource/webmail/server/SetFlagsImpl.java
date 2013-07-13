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

import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class SetFlagsImpl extends SecureAjaxCall implements SetFlags {

	private static final long serialVersionUID = -3941231833930836032L;

	public void setFlags(Set<ConversationId> conversationIds, String flag, boolean set) {
		if (logger.isInfoEnabled()) {
			logInfo((set ? "setting " : "unsetting ") + flag);
		}

		IAccount account = getAccount();
		account.setFlags(conversationIds, flag, set);
	}

	public void setFlags(String folderName, String flag, boolean set) {
		if (logger.isInfoEnabled()) {
			logInfo((set ? "setting " : "unsetting ") + flag);
		}

		IAccount account = getAccount();
		account.setFlags(folderName, flag, set);
	}

}
