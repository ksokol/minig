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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * AJAX call used to fetch frontend & server settings
 * 
 * @author tom
 * 
 */
public class GetSettingsImpl extends RemoteServiceServlet implements
		GetSettings {

	private static final long serialVersionUID = 1652197895075654185L;

	private Log logger = LogFactory.getLog(getClass());

	public HashMap<String, String> getAllSettings() {
		if (logger.isDebugEnabled()) {
			logger.debug("getAllSettings");
		}

		HashMap<String, String> ret = new HashMap<String, String>();
		FrontEndConfig feCfg = new FrontEndConfig();
		ret.putAll(feCfg.get());

		IAccount account = (IAccount) getThreadLocalRequest().getSession()
				.getAttribute("account");

		/* We are probably logged using a servlet filter */

		if (account != null) {
			if (logger.isInfoEnabled()) {
				logger.info("logged in as " + account.getLogin() + "@"
						+ account.getDomain());
			}
			ret.put(AJAX_LOGIN, "false");
			ret.put(CURRENT_LOGIN, account.getLogin());
			ret.put(CURRENT_DOMAIN, account.getDomain());

			ret.putAll(account.getServerSettings());
		} else {
			if ("false".equals(ret.get(AJAX_LOGIN))) {
				logger.warn("ajaxLogin disabled, but login not performed.");
			}
		}

		return ret;
	}
}
