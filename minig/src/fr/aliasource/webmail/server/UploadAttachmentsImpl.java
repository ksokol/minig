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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * Receives attachments uploads from the composer. Uses commons-fileupload to
 * extract the upload file and metadata.
 * 
 * @author tom
 * 
 */
public class UploadAttachmentsImpl extends HttpServlet {

	private static final long serialVersionUID = 3381314419613122761L;

	private Log logger = LogFactory.getLog(getClass());

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestContext ctx = new ServletRequestContext(req);
		String enc = ctx.getCharacterEncoding();
		logger.warn("received encoding is " + enc);
		if (enc == null) {
			enc = "utf-8";
		}
		IAccount account = (IAccount) req.getSession().getAttribute("account");

		if (account == null) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory(100 * 1024,
				new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(20 * 1024 * 1024);

		List items = null;
		try {
			items = upload.parseRequest(req);
		} catch (FileUploadException e1) {
			logger.error("upload exception", e1);
			return;
		}

		// Process the uploaded items
		String id = null;
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (!item.isFormField()) {
				id = item.getFieldName();
				String fileName = removePathElementsFromFilename(item.getName());
				logger.warn("FileItem: " + item);
				long size = item.getSize();
				logger.warn("pushing upload of " + fileName
						+ " to backend for " + account.getLogin() + "@"
						+ account.getDomain() + " size: " + size + ").");
				AttachmentMetadata meta = new AttachmentMetadata();
				meta.setFileName(fileName);
				meta.setSize(size);
				meta.setMime(item.getContentType());
				try {
					account.uploadAttachement(id, meta, item.getInputStream());
				} catch (Exception e) {
					logger.error("Cannot write uploaded file to disk");
				}
			}
		}
	}

	private String removePathElementsFromFilename(String filename) {
		int startOfFilename = filename.lastIndexOf("\\");
		if (startOfFilename != -1) {
			return filename.substring(startOfFilename + 1);
		}
		return filename;
	}

}
