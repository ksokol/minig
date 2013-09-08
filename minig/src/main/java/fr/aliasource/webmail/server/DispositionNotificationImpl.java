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

import fr.aliasource.webmail.client.rpc.DispositionNotification;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class DispositionNotificationImpl extends SecureAjaxCall implements DispositionNotification {

	private static final long serialVersionUID = 3329736614903621716L;

	@Override
	public void sendNotification(MessageId messageId, String folderName) {
		IAccount account = getAccount();
		account.sendDispositionNotification(messageId, folderName);
	}

	@Override
	public void declineNotification(MessageId messageId) {
		IAccount account = getAccount();
		account.denyDispositionNotification(messageId);
	}


}
