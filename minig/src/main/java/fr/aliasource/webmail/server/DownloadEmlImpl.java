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
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.server.proxy.client.IAccount;

public class DownloadEmlImpl extends HttpServlet {

	private static final long serialVersionUID = 6118011043311663089L;

	private Log logger = LogFactory.getLog(getClass());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IAccount account = (IAccount) req.getSession().getAttribute("account");

		if (account == null) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String emlId = req.getParameter("emlId");
		String folderName = req.getParameter("folderName");

		logger.info("[" + account.getLogin() + "] " + "download eml with id: "
				+ emlId);

		InputStream in = account.downloadEml(folderName, emlId);

		resp.setHeader("Content-Disposition",
				"application/force-download; filename=\"email.eml\"");
		resp.setHeader("Content-Transfer-Encoding", "binary");
		resp.setHeader("Content-Type",
				"application/force-download; name=\"email.eml\"");
		OutputStream out = resp.getOutputStream();
		int size = transfer(in, out, true);
		resp.setHeader("Content-Length", "" + size);
	}

	@Override
	public void init() throws ServletException {
		super.init();

		logger.info("Download servlet initialized");
	}

	/**
	 * Fast stream transfer method
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private int transfer(InputStream in, OutputStream out, boolean closeIn)
			throws IOException {
		final byte[] buffer = new byte[2048];
		int size = 0;
		try {
			while (true) {
				int amountRead = in.read(buffer);
				if (amountRead == -1) {
					break;
				}
				size += amountRead;
				out.write(buffer, 0, amountRead);
			}
		} finally {
			if (closeIn) {
				in.close();
			}
			out.flush();
			out.close();
		}
		return size;
	}

}
