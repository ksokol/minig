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

package fr.aliasource.webmail.server.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * 
 * @author matthieu Use to render conversation in pdf/html for printing
 * 
 */
public class ExportConversationImpl extends HttpServlet {

	private static final long serialVersionUID = 6118011043311663089L;

	private Log logger = LogFactory.getLog(getClass());

	/**
	 * The actual business logic.
	 * 
	 * @param requ
	 *            the request object
	 * @param resp
	 *            the response object
	 * @throws IOException
	 * @throws ServletException
	 */
	public void service(HttpServletRequest req, HttpServletResponse response)
			throws IOException, ServletException {
		logger.info("Export conversation called.");

		IAccount account = (IAccount) req.getSession().getAttribute("account");

		if (account == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		String uri = req.getRequestURI();
		String convAndMessageIds = extractConversationIdFromRequestURI(uri);
		MessageId messageId = getMessageIdPart(convAndMessageIds);
		ConversationId conversationId = getConversationIdPart(convAndMessageIds);

		String folder = conversationId.getSourceFolder();

		logger.info("Conversation id: " + conversationId.getConversationId() + " folder: " + folder
				+ " uri: " + uri + "Message id: " + messageId);

		Folder f = new Folder(folder, folder);
		ConversationReference cr = account.findConversation(conversationId);
		ClientMessage[] cm = null;
		if (messageId == null) {
			cm = account.fetchMessages(f, cr.getMessageIds());
		} else {
			cm = account.fetchMessages(f, Arrays.asList(messageId));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConversationExporter exporter = new ConversationExporter(req
				.getContextPath()
				+ "/minig/images/logo_print.jpg");
		try {
			if (req.getRequestURI().endsWith(".html")) {
				exporter.exportToHtml(account, cr, cm, baos);
				response.setContentType("text/html");
			} else {
				exporter.exportToPdf(account, cr, cm, baos);
				response.setContentType("application/pdf");
			}
		} catch (ConversationExporterException e) {
			logger.error("Cannot render conversation", e);
			throw new ServletException(e);
		}

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");

		response.setContentLength(baos.size());
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out);
		out.flush();

	}

	private String extractConversationIdFromRequestURI(String uri)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(
				uri.substring(uri.lastIndexOf("export/conv") + 12, uri.lastIndexOf(".")), "UTF-8");
	}

	private MessageId getMessageIdPart(String convAndMessageIds) {
		int pipe = convAndMessageIds.indexOf("|");
		if (pipe > 0) {
			return new MessageId(Long.valueOf(convAndMessageIds.substring(pipe + 1)));
		}
		return null;
	}
	
	private ConversationId getConversationIdPart(String convAndMessageIds) {
		int pipe = convAndMessageIds.indexOf("|");
		if (pipe > 0) {
			return new ConversationId(convAndMessageIds.substring(0, pipe));
		}
		return null;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Export conversation servlet initialized");
	}

}
