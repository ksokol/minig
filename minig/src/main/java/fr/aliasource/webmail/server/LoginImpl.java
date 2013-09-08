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

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.aliasource.webmail.client.rpc.Login;
import fr.aliasource.webmail.server.proxy.client.ClientException;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.IProxyClientFactory;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

/**
 * AJAX login RPC impl
 * 
 * Logs into the webmail proxy and stores a proxy client in the http session
 * 
 * @author tom
 * 
 */
public class LoginImpl extends RemoteServiceServlet implements Login {

	private static final long serialVersionUID = 2441340547999640054L;

	private ProxyConfig cfg;
	private IProxyClientFactory proxyClientFactory;
	private static final Log logger = LogFactory.getLog(LoginImpl.class);

	public LoginImpl() {
		FrontEndConfig fec = new FrontEndConfig();
		cfg = new ProxyConfig(Collections.synchronizedMap(fec.get()));
		try {
			proxyClientFactory = (IProxyClientFactory) Class.forName(
					cfg.getProxyClientFactoryClass()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void doUnexpectedFailure(Throwable e) {
		logger.error("doUnexpectedFailure", e);
		super.doUnexpectedFailure(e);
	}

	public boolean doLogin(String login, String password, String domain) {
		HttpSession session = getThreadLocalRequest().getSession();
		// needs to be shorter than backend session
		session.setMaxInactiveInterval(1 * 60);

		IAccount account = (IAccount) session.getAttribute("account");
		if (account == null) {
			try {
				account = proxyClientFactory.newProxyClient(cfg);
				account.login(login, domain, password);
				session.setAttribute("account", account);
				return true;
			} catch (ClientException e) {
				return false;
			}
		} else {
			return true;
		}
	}

}
