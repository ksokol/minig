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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SSO provider that can use a different location for ticket retrieval & ticket
 * validation. The ssoServerUrl should be lemon protected, whereas the
 * ssoValidateUrl should not.
 * 
 * @author tom
 * 
 */
public class LemonSSOProvider implements ISSOProvider {

	public static final String SSO_SERVER_URL = "frontend.ssoServerUrl";
	public static final String SSO_VALIDATE_URL = "frontend.ssoValidateUrl";
	public static final String SSO_LOGOUT_URL = "frontend.logoutUrl";

	private boolean wentToSSO;
	private Log logger = LogFactory.getLog(getClass());

	public LemonSSOProvider() {
		wentToSSO = false;
	}

	@Override
	public Credentials obtainCredentials(Map<String, String> settings,
			HttpServletRequest req) {
		String ticket = req.getParameter("ticket");
		if (ticket == null) {
			logger.warn("no ticket in url");
			return null;
		}

		try {
			URL url = new URL(settings.get(SSO_VALIDATE_URL)
					+ "?action=validate&ticket=" + ticket);
			InputStream in = url.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			transfer(in, out, true);
			String content = out.toString();
			if (logger.isDebugEnabled()) {
				logger.debug("SSO server returned:\n" + content);
			}
			Credentials creds = null;
			if (!content.equals("invalidOBMTicket")) {
				String[] ssoServerReturn = content.split("&password=");
				String login = ssoServerReturn[0].substring("login=".length());
				String pass = ssoServerReturn[1];
				creds = new Credentials(URLDecoder.decode(login, "UTF-8"),
						URLDecoder.decode(pass, "UTF-8"));
			}
			return creds;
		} catch (Exception e) {
			logger.error("Ticket validation error: " + e.getMessage(), e);
			return null;
		}

	}

	@Override
	public boolean redirectToSSOServer(String myUrl, Map<String, String> p,
			HttpServletRequest req, HttpServletResponse resp) {
		String url = p.get(SSO_SERVER_URL) + "?action=ticket&service=" + myUrl;
		try {
			logger.info("Redirecting to sso server: " + url);
			resp.sendRedirect(url);
		} catch (IOException e) {
			logger.error("Error redirecting to sso server", e);
		}
		wentToSSO = true;
		return true;
	}

	@Override
	public boolean wentToSSOServer(HttpServletRequest req) {
		return wentToSSO && req.getParameter("ticket") != null;
	}

	/**
	 * Fast stream transfer method
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void transfer(InputStream in, OutputStream out, boolean closeIn)
			throws IOException {
		final byte[] buffer = new byte[2048];

		try {
			while (true) {
				int amountRead = in.read(buffer);
				if (amountRead == -1) {
					break;
				}
				out.write(buffer, 0, amountRead);
			}
		} finally {
			if (closeIn) {
				in.close();
			}
			out.flush();
			out.close();
		}
	}

}
