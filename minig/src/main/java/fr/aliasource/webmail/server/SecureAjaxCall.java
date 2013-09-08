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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * Common operations for ajax calls requiering a logged in user
 * 
 * @author tom
 * 
 */
public abstract class SecureAjaxCall extends RemoteServiceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7809800225395588243L;
	protected Log logger;

	public SecureAjaxCall() {
		logger = LogFactory.getLog(getClass());
	}

	protected IAccount getAccount() {
		IAccount account = (IAccount) getThreadLocalRequest().getSession()
				.getAttribute("account");
		return account;
	}

	protected void logInfo(String info) {
		IAccount account = getAccount();
		if (account != null) {
			logger.info("[" + account.getLogin() + "@" + account.getDomain()
					+ "] " + info);
		} else {
			logger.info("[expired account] " + info);
		}
	}

	protected void logError(String error, Throwable t) {
		IAccount account = getAccount();
		if (account != null) {
			logger.error("[" + account.getLogin() + "@" + account.getDomain()
					+ "] " + error, t);
		} else {
			logger.info("[expired account] " + error, t);
		}
	}

	protected void doUnexpectedFailure(Throwable e) {
		logError("Unexpected failure", e);
		super.doUnexpectedFailure(e);
	}

}
