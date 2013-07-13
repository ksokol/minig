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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.server.proxy.client.ClientException;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.IProxyClientFactory;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

/**
 * Filter used when running in a tomcat :
 * 
 * 
 * @author tom
 * 
 */
public class LoginFilter implements Filter {

	private Map<String, String> settings;
	private Log logger = LogFactory.getLog(getClass());
	private HashSet<String> ajaxCall;
	private IProxyClientFactory proxyClientFactory;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain fc) throws IOException, ServletException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpSession session = hreq.getSession();
		hreq.setCharacterEncoding("UTF-8");

		IAccount account = (IAccount) session.getAttribute("account");
		String uri = hreq.getRequestURI();
		uri = uri.replace("/minig", "");

		if (account != null) {
			fc.doFilter(req, resp);
			return;
		} else {

			// pass through ajax calls
			if (ajaxCall.contains(uri)) {
				fc.doFilter(req, resp);
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("loginFilter on uri: " + hreq.getRequestURI());
			}

			/* Not logged in */
			try {
				performLoginProcedure(session, fc, hreq,
						(HttpServletResponse) resp);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	private void denyCall(HttpServletRequest hreq, HttpServletResponse resp)
			throws IOException {
		hreq.getSession().invalidate();
		logger.warn("not logged call to '" + computeMyUrl(hreq, resp) + "', denying");
		resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		resp.getWriter().println("MiniG access not allowed.");
	}

	private void performLoginProcedure(HttpSession s, FilterChain fc,
			HttpServletRequest hreq, HttpServletResponse resp) throws Exception {
		logger.info("performLogin(" + hreq.getRequestURI() + ")");
		if ("true".equals(settings.get(GetSettings.AJAX_LOGIN))) {
			fc.doFilter(hreq, resp);
		} else {
			logger.info("Should perform std login procedure => sso server");
			ISSOProvider sso = getSSOProvider(s);
			if (!sso.wentToSSOServer(hreq)) {
				sso.redirectToSSOServer(computeMyUrl(hreq, resp), settings, hreq,
						resp);
				return;
			} else {
				Credentials creds = sso.obtainCredentials(settings, hreq);
				if (creds != null) {
					if (doLogin(s, creds.getLogin(), creds.getPassword())) {
						fc.doFilter(hreq, resp);
					} else {
						logger.warn("sso returned credentials, but minig-backend seems to refuse them.");
						denyCall(hreq, resp);
					}
					return;
				} else {
					logger.warn("SSO server did not validate ticket");
					denyCall(hreq, resp);
					return;
				}
			}
		}
	}

	private String computeMyUrl(HttpServletRequest hreq, HttpServletResponse resp) {
		StringBuilder ret = new StringBuilder(hreq.getRequestURL().toString());
		if (hreq.getQueryString() != null) {
			ret.append("?");
			ret.append(hreq.getQueryString());
		}
		try {
			return URLEncoder.encode(resp.encodeRedirectURL(ret.toString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return hreq.getRequestURL().toString();
		}
	}

	private ISSOProvider getSSOProvider(HttpSession s) {
		String provider = settings.get(GetSettings.SSO_PROVIDER);
		logger.info("Loading sso provider (" + provider + ")...");
		ISSOProvider sso = (ISSOProvider) s.getAttribute("ssoProvider");
		if (sso == null) {
			try {
				sso = (ISSOProvider) Class.forName(provider).newInstance();
				s.setAttribute("ssoProvider", sso);
			} catch (Exception e) {
				logger.error("Error loading SSO provider: " + provider, e);
			}
		}
		return sso;
	}

	private boolean doLogin(HttpSession s, String login, String password)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ClientException {
		logger.info("Filter based login");
		ProxyConfig cfg = new ProxyConfig(settings);

		IAccount account = proxyClientFactory.newProxyClient(cfg);
		try {
			if (login.contains("@")) {
				String[] creds = login.split("@");
				account.login(creds[0], creds[1], password);
			} else {
				account.login(login, null, password);
			}
			s.setAttribute("account", account);
			s.setMaxInactiveInterval(3 * 60);
			return true;
		} catch (Exception e) {
			logger.error("Error loging into backend : user without mailbox ? ("
					+ login + ")");
			return false;
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		XTrustProvider.install();

		ajaxCall = new HashSet<String>();
		ajaxCall.add("/lc");
		ajaxCall.add("/folderManager");
		ajaxCall.add("/login");
		ajaxCall.add("/logout");
		ajaxCall.add("/lemails");
		ajaxCall.add("/sca");
		ajaxCall.add("/settings");
		ajaxCall.add("/sent");
		ajaxCall.add("/search");
		ajaxCall.add("/flags");
		ajaxCall.add("/store");
		ajaxCall.add("/contactGroups");
		ajaxCall.add("/listContacts");
		ajaxCall.add("/attachements");
		ajaxCall.add("/download");
		ajaxCall.add("/attachementsManager");
		ajaxCall.add("/proxy");

		FrontEndConfig fec = new FrontEndConfig();
		settings = Collections.synchronizedMap(fec.get());
		logger.info("ssoProvider: " + settings.get(GetSettings.SSO_PROVIDER));

		if (settings.isEmpty()) {
			logger.error("Empty settings: you need "
					+ "a valid /etc/minig/frontend_conf.ini");
		}

		String klass = new ProxyConfig(settings).getProxyClientFactoryClass();
		try {
			proxyClientFactory = (IProxyClientFactory) Class.forName(klass)
					.newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServletException(e);
		}

	}

}
