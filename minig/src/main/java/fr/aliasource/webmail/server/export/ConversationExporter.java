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

import java.awt.Color;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * 
 * @author matthieu This class is used to convert conversation to printable pdf
 *         or html
 * 
 */
public class ConversationExporter {

	private static Log logger = LogFactory.getLog(ConversationExporter.class);

	private String logoUrl;

	public ConversationExporter(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public void exportToPdf(IAccount account, ConversationReference cr,
			ClientMessage[] cm, OutputStream out)
			throws ConversationExporterException {
		try {
			Document document = new Document(PageSize.A4, 22, 22, 80, 72);
			PdfWriter writer = PdfWriter.getInstance(document, out);
			writer
					.setPageEvent(new ConversationPdfEventHandler(account, cr,
							cm));
			// NPP by Tom
			if (cr.getTitle() != null) {
				document.addTitle(cr.getTitle());
			} else {
				document.addTitle("");
			}
			document.addAuthor("MiniG");
			document.open();

			document.add(Chunk.NEWLINE);

			Set<ClientMessage> scm = new LinkedHashSet<ClientMessage>();
			for (int i = 0; i < cm.length; i++) {
				scm.add(cm[i]);
			}
			this.exportMessage(scm, document, false);
			document.close();
		} catch (Exception e) {
			throw new ConversationExporterException(
					"Cannot export conversation ", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void exportMessage(Set<ClientMessage> cm, Document d,
			boolean isForward) throws DocumentException {

		LineSeparator hr = new LineSeparator();
		StyleSheet styles = new StyleSheet();
		Font fnormal = new Font(Font.HELVETICA, 9, Font.NORMAL);
		Font fbold = new Font(Font.HELVETICA, 9, Font.BOLD);

		Iterator<ClientMessage> it = cm.iterator();
		Cell c = null;
		while (it.hasNext()) {
			ClientMessage fwdCm = it.next();
			if (isForward) {
				c = new Cell();
			}

			// Subject (only if isForward)
			if (isForward) {
				String subjectText = fwdCm.getSubject();
				String dateText = formatDate(fwdCm.getDate());
				Chunk subject = new Chunk(subjectText, fbold);
				Chunk date = new Chunk(dateText, fbold);
				Paragraph subjectPar = new Paragraph(subject + ", " + date);
				subjectPar.setIndentationLeft(5.0f);
				c.add(subjectPar);
				c.add(Chunk.NEWLINE);
			} else {
				String dateText = formatDate(fwdCm.getDate());
				Chunk date = new Chunk(dateText, fbold);
				Paragraph datePar = new Paragraph(date);
				datePar.setAlignment(Element.ALIGN_RIGHT);
				d.add(datePar);
			}

			// Sender
			String senderText = formatAddress(fwdCm.getSender());
			Chunk sender = new Chunk(senderText, fbold);
			sender.setTextRise(10.0f);
			Paragraph senderPar = new Paragraph(sender);
			if (isForward) {
				senderPar.setIndentationLeft(5.0f);
				c.add(senderPar);
			} else {
				d.add(senderPar);
			}

			appendRecipients(d, c, isForward, "To:", fwdCm.getTo());
			appendRecipients(d, c, isForward, "Cc:", fwdCm.getCc());
			appendRecipients(d, c, isForward, "Bcc:", fwdCm.getBcc());
			if (isForward) {
				c.add(Chunk.NEWLINE);
			}

			// Body
			String bodyText = fwdCm.getBody().getCleanHtml();
			Paragraph bodyPar = new Paragraph();
			bodyPar.setFont(fnormal);
			if (bodyText != null && !bodyText.isEmpty()) {
				try {
					List<Element> objects = HTMLWorker.parseToList(
							new StringReader(bodyText), styles);
					for (Iterator<Element> iterator = objects.iterator(); iterator
							.hasNext();) {
						Element el = iterator.next();
						if (!(el instanceof Image)) {
							// bodyPar.add(el);
							if (isForward) {
								c.add(el);
							} else {
								bodyPar.add(el);
							}
						}
					}
				} catch (Exception e) {
					logger
							.warn(
									"Cannot generate pdf from html body use plain text instead",
									e);
					// bodyPar.add(fwdCm.getBody().getPlain());
					if (isForward) {
						Chunk t = new Chunk(fwdCm.getBody().getPlain());
						t.setFont(fnormal);
						c.add(t);
					} else {
						bodyPar.add(fwdCm.getBody().getPlain());
					}
				}
			} else {
				if (isForward) {
					Chunk t = new Chunk(fwdCm.getBody().getPlain());
					t.setFont(fnormal);
					c.add(t);
				} else {
					bodyPar.add(fwdCm.getBody().getPlain());
				}
			}

			if (isForward) {
				// c.add(bodyPar);
				Table t = new Table(1);
				t.setPadding(5);
				t.setBackgroundColor(new Color(242, 242, 242));
				t.addCell(c);
				d.add(t);
			} else {
				bodyPar.setIndentationLeft(15.0f);
				d.add(bodyPar);
			}

			if (fwdCm.getFwdMessages() != null) {
				this.exportMessage(fwdCm.getFwdMessages(), d, true);
			}
			d.add(hr);
		}

	}

	private String formatBody(Body body, boolean cleanHtml) {
		String bodyText = null;
		if (cleanHtml) {
			bodyText = body.getCleanHtml();
		} else {
			bodyText = body.getPartialCleanHtml();
		}
		if (bodyText == null || bodyText.isEmpty()) {
			bodyText = body.getPlain();
		}
		return bodyText;
	}

	public void exportToHtml(IAccount account, ConversationReference cr,
			ClientMessage[] cm, OutputStream out)
			throws ConversationExporterException {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer
					.append("<head><meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">"
							+ "<title>"
							+ cr.getTitle()
							+ "</title>"
							+ "<style type=\"text/css\">"
							+ "body, td{font-family:arial,sans-serif;font-size:80%} "
							+ "a:link, a:active, a:visited{color:#0000CC} img{border:0}"
							+ ".conversation { width:100%;padding: 4px; border: 1px solid #F0A528; }"
							+ ".forward { width:95%; border: 1px solid #666; background: #F2F2F2; padding: 4px;  }"
							+ "</style>"
							+ "<script>function Print(){document.body.offsetHeight;window.print()}</script>"
							+ "</head><body onload=\"Print()\">"
							+ "<table width=100% cellpadding=0 cellspacing=0 border=0>"
							+ "<tr><td width=143><img src=\""
							+ logoUrl
							+ "\" width=95 height=42 alt=\"MiniG\"></td>"
							+ "<td align=right><font size=-1><b>"
							+ formatName(account)
							+ "</b></td></tr></table>"
							+ "<hr><font size=+2><b>"
							+ cr.getTitle()
							+ "</b></font><br>");
			if (cm != null) {
				for (int i = 0; i < cm.length; i++) {
					displayMessage(cm[i], buffer, "conversation", false);
					buffer.append("<hr>");
				}
			}
			buffer.append("</body></html>");
			IOUtils.write(buffer, out);
		} catch (Exception e) {
			throw new ConversationExporterException(
					"Cannot export conversation", e);
		}
	}

	private void displayMessage(ClientMessage cm, StringBuffer buffer,
			String css, boolean displaySubject) {
		String senderText = StringEscapeUtils.escapeHtml(formatAddress(cm
				.getSender()));
		String dateText = formatDate(cm.getDate());
		StringBuilder toText = new StringBuilder();
		StringBuilder ccText = new StringBuilder();
		StringBuilder bccText = new StringBuilder();
		formatRecipients(toText, cm.getTo(), true);
		formatRecipients(ccText, cm.getCc(), true);
		formatRecipients(bccText, cm.getBcc(), true);
		String bodyText = formatBody(cm.getBody(), false).replaceAll("\n",
				"<br/>");

		String sub = "";
		if (displaySubject) {
			String subject = cm.getSubject();
			sub = "<tr><td><font size=-1><b>" + subject + "</b></font></td>";
		}

		buffer.append("<table class=" + css
				+ " cellpadding=0 cellspacing=0 border=0>" + sub
				+ "<td align=right><font size=-1><b>" + dateText
				+ "</b></font></td></tr>"
				+ "<tr><td colspan=2>&nbsp;</td></tr>"
				+ "<tr><td colspan=2><font size=-1>" + "<div><b>From : </b>"
				+ senderText);

		if (toText.length() > 0) {
			buffer.append("<b> To : </b>" + toText);
		}
		if (ccText.length() > 0) {
			buffer.append("<b> Cc : </b>" + ccText);
		}
		if (bccText.length() > 0) {
			buffer.append("<b> Bcc : </b>" + bccText);
		}

		buffer.append("</div></font></td></tr>" + "<tr><td colspan=2>"
				+ bodyText + "</td></tr>"
				+ "<tr><td colspan=2>&nbsp;</td></tr>");

		if (cm.getFwdMessages() != null) {
			for (ClientMessage fwd : cm.getFwdMessages()) {
				buffer.append("<tr><td colspan=2 align=center>");
				displayMessage(fwd, buffer, "forward", true);
				buffer.append("</td></tr>");
			}

		}

		buffer.append("</table>");
	}

	// FIXME gruik, public as used by ConversationPdfEventHandler
	public static String formatName(IAccount account) {
		StringBuilder sb = new StringBuilder();
		sb.append(account.getLogin());
		if (account.getDomain() != null && !account.getDomain().isEmpty()) {
			sb.append("@");
			sb.append(account.getDomain());
		}

		return sb.toString();
	}

	private void formatRecipients(StringBuilder sb, List<EmailAddress> recipients,
			boolean inHtml) {
		for (int i = 0; i < recipients.size(); i++) {
			String addr = formatAddress(recipients.get(i));
			if (inHtml) {
				addr = StringEscapeUtils.escapeHtml(addr);
			}
			sb.append(addr);
			if (i < recipients.size() - 1) {
				sb.append(", ");
			}
		}
	}

	private String formatDate(Date date) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		dateFormater.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		return dateFormater.format(date);
	}

	private String formatAddress(EmailAddress add) {
		String formated = add.getDisplay();
		if (add.getEmail() != null && add.getEmail().length() > 0
				&& !add.getEmail().equals(add.getDisplay())) {
			formated += " <" + add.getEmail() + ">";
		}
		return formated;
	}

	private void appendRecipients(Document d, Cell c, boolean isForward,
			String label, List<EmailAddress> mails) throws DocumentException {
		Chunk parLabel = getRecipientLabel(label);
		Chunk parValue = getRecipientValue(mails);
		if (parValue != null) {
			Chunk line = Chunk.NEWLINE;
			if (isForward) {
				c.add(line);
				c.add(parLabel);
				// c.add(parValue);
			} else {
				d.add(line);
				d.add(parLabel);
				// d.add(parValue);
			}
		}
	}

	// Label should be "To:", "Cc:" or "Bcc:"
	private Chunk getRecipientValue(List<EmailAddress> recipients) {
		Chunk recipientsChunk = null;
		if (recipients != null && !recipients.isEmpty()) {
			StringBuilder recipientsText = new StringBuilder(200);
			formatRecipients(recipientsText, recipients, false);
			recipientsChunk = new Chunk(recipientsText.toString(), new Font(
					Font.HELVETICA, 9));
			recipientsChunk.setTextRise(15.0f);
		}
		return recipientsChunk;
	}

	private Chunk getRecipientLabel(String label) {
		Chunk recipientsLabel = new Chunk(label, new Font(Font.HELVETICA, 10,
				Font.BOLD));
		recipientsLabel.setTextRise(15.0f);
		return recipientsLabel;
	}

}
