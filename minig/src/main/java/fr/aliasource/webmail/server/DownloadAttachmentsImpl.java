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

import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class DownloadAttachmentsImpl extends HttpServlet {

	private static final long serialVersionUID = 6118011043311663089L;

	private Log logger = LogFactory.getLog(getClass());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IAccount account = (IAccount) req.getSession().getAttribute("account");

		if (account == null) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String[] items = req.getRequestURI().split("/");
		String atId = null;

		boolean dl = false;
		boolean preview = false;
		for (String s : items) {
			if (s.startsWith("at_")) {
				atId = s;
			} else if (s.equals("download")) {
				dl = true;
			} else if (s.equals("preview")) {
				dl = false;
				preview = true;
			}
		}
		logger.info("[" + account.getLogin() + "] "
				+ (dl ? "download" : "view") + " of attachment id: " + atId
				+ " uri: " + req.getRequestURI().split("/"));

		AttachmentMetadata[] metas = account
				.getAttachementsMetadata(new String[] { atId });

		if (!preview) {
			resp.setHeader("Content-Type", (dl ? "application/octet-stream"
					: metas[0].getMime()));
		} else {
			resp.setHeader("Content-Type", metas[0].getPreviewMime());
		}
		if (!preview) {
			resp.setHeader("Content-Length", "" + metas[0].getSize());
		}

		OutputStream out = resp.getOutputStream();
		InputStream in = account.downloadAttachement(atId
				+ (preview ? ".preview" : ""));
		transfer(in, out, true);
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
