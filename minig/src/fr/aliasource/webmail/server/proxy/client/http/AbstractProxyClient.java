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

package fr.aliasource.webmail.server.proxy.client.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds utility methods
 * 
 * @author tom
 * 
 */
public class AbstractProxyClient {

	protected Log logger;
	protected String login;
	protected String domain;
	protected String backendUrl;
	protected String token;

	protected AbstractProxyClient() {
		logger = LogFactory.getLog(getClass());
	}

	protected void logInfo(String info) {
		if (login != null) {
			logger.info("[" + token + "] " + info);
		} else {
			logger.info("[anonymous] [" + backendUrl + "] " + info);
		}
	}

	protected void logDebug(String debug) {
		if (login != null) {
			logger.debug("[" + login + "@" + domain + "][" + backendUrl + "] "
					+ debug);
		} else {
			logger.debug("[anonymous][" + backendUrl + "] " + debug);
		}
	}

	public String getDomain() {
		return domain;
	}

	public String getLogin() {
		return login;
	}

}
