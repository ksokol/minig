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

import fr.aliasource.webmail.client.rpc.GetQuota;
import fr.aliasource.webmail.client.shared.QuotaInfo;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class GetQuotaImpl extends SecureAjaxCall implements GetQuota {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8001542221159979175L;

	@Override
	public QuotaInfo getQuota(String mailBox) {
		if (logger.isDebugEnabled()) {
			logger.debug("quota called");
		}
		IAccount account = getAccount();

		// QuotaInfo ret = new
		if (account != null) {
			return account.getQuota(mailBox);
		}
		return new QuotaInfo();
	}

}
