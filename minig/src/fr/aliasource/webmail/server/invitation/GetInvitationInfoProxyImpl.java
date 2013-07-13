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

package fr.aliasource.webmail.server.invitation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import com.google.gwt.core.client.GWT;

import fr.aliasource.webmail.server.FrontEndConfig;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * 
 * @author adrienp
 * 
 */
public class GetInvitationInfoProxyImpl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1678473146329711601L;

	private String backendUrl;

	private HttpClient hc;

	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IAccount ac = (IAccount) req.getSession().getAttribute("account");

		if (ac == null) {
			GWT.log("Account not found in session", null);
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		PostMethod pm = new PostMethod(backendUrl);
		if (req.getQueryString() != null) {
			pm.setQueryString(req.getQueryString());
		}
		Map<String, String[]> params = req.getParameterMap();
		for (String p : params.keySet()) {
			String[] val = params.get(p);
			pm.setParameter(p, val[0]);
		}

		synchronized (hc) {
			try {
				int ret = hc.executeMethod(pm);
				if (ret != HttpStatus.SC_OK) {
					log("method failed:\n" + pm.getStatusLine() + "\n"
							+ pm.getResponseBodyAsString());
					resp.setStatus(ret);
				} else {
					InputStream is = pm.getResponseBodyAsStream();
					transfer(is, resp.getOutputStream(), false);
				}

			} catch (Exception e) {
				log("error occured on call proxyfication", e);
			} finally {
				pm.releaseConnection();
			}
		}
	}

	private HttpClient createHttpClient() {
		HttpClient ret = new HttpClient(
				new MultiThreadedHttpConnectionManager());
		HttpConnectionManagerParams mp = ret.getHttpConnectionManager()
				.getParams();
		mp.setDefaultMaxConnectionsPerHost(4);
		mp.setMaxTotalConnections(8);
		return ret;
	}

	/**
	 * Fast stream transfer method
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void transfer(InputStream in, OutputStream out,
			boolean closeIn) throws IOException {
		final byte[] buffer = new byte[4096];

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

	@Override
	public void init() throws ServletException {
		super.init();

		Map<String, String> prefs = new FrontEndConfig().get();
		backendUrl = prefs.get("frontend.proxyUrl") + "/getInvitationInfo.do";
		this.hc = createHttpClient();
	}

}
