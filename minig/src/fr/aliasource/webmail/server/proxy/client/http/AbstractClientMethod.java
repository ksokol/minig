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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class AbstractClientMethod {

	protected HttpClient hc;
	protected Log logger;
	private String url;

	protected AbstractClientMethod(HttpClient hc, String backendUrl,
			String action) {
		this.logger = LogFactory.getLog(getClass());
		this.hc = hc;
		this.url = backendUrl + action;
	}

	protected final Map<String, String> paramsFrom(String token) {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("token", token);
		return ret;
	}
	
	protected Document execute(Map<String, String> parameters) {
		try {
			InputStream is = executeStream(parameters);
			if (is != null) {
				return DOMUtils.parse(is);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	protected InputStream executeStream(Map<String, String> parameters) {
		InputStream is = null;
		PostMethod pm = new PostMethod(url);
		pm.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		for (String p : parameters.keySet()) {
			pm.setParameter(p, parameters.get(p));
		}
		int ret = 0;
		synchronized (hc) {
			try {
				ret = hc.executeMethod(pm);
				if (ret == HttpStatus.SC_NOT_MODIFIED) {
					logger.info("backend wants us to use cached data");
				} else if (ret != HttpStatus.SC_OK) {
					logger.error("method failed:\n" + pm.getStatusLine() + "\n"
							+ pm.getResponseBodyAsString());
				} else {
					is = pm.getResponseBodyAsStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					transfer(is, out, true);
					return new ByteArrayInputStream(out.toByteArray());
				}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			} finally {
				pm.releaseConnection();
			}
		}
		return is;
	}

	protected void executeVoid(Map<String, String> parameters) {
		executeStream(parameters);
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

}
