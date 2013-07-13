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

import java.net.URL;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * 
 * @author matthieu This class add Header and pagination to conversation pdf
 */
public class ConversationPdfEventHandler extends PdfPageEventHelper {

	private Image headerImage;

	private PdfPTable table;

	private PdfTemplate tpl;

	private BaseFont helv;

	private ConversationReference cr;

	private ClientMessage[] cm;

	private IAccount account;

	public ConversationPdfEventHandler(IAccount account,
			ConversationReference cr, ClientMessage[] cm) {
		super();
		this.account = account;
		this.cr = cr;
		this.cm = cm;
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		try {
			headerImage = Image.getInstance(getLogoUrl());
			table = new PdfPTable(new float[] { 1f, 2f });
			Phrase p = new Phrase();
			Chunk ck = new Chunk(cr.getTitle(), new Font(Font.HELVETICA, 16,
					Font.BOLD));
			p.add(ck);
			p.add(Chunk.NEWLINE);
			ck = new Chunk(ConversationExporter.formatName(account), new Font(
					Font.HELVETICA, 12, Font.BOLDITALIC));
			p.add(ck);
			p.add(Chunk.NEWLINE);
			ck = new Chunk(cm.length + " messages",
					new Font(Font.HELVETICA, 10));
			p.add(ck);
			table.getDefaultCell().setBorder(0);
			table.addCell(new Phrase(new Chunk(headerImage, 0, 0)));
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(p);
			// initialization of the template
			tpl = writer.getDirectContent().createTemplate(100, 100);
			tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
			// initialization of the font
			helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// write the headertable
		table.setTotalWidth(document.right() - document.left());
		table.writeSelectedRows(0, -1, document.left(), document.getPageSize()
				.getHeight() - 20, cb);
		// compose the footer
		String text = writer.getPageNumber() + " / ";
		float textSize = helv.getWidthPoint(text, 12);
		float textBase = document.bottom() - 49;
		cb.beginText();
		cb.setFontAndSize(helv, 12);
		float adjust = helv.getWidthPoint("0", 12);
		cb.setTextMatrix(document.right() - textSize - adjust - 5, textBase);
		cb.showText(text);
		cb.endText();
		cb.addTemplate(tpl, document.right() - adjust, textBase);
		cb.saveState();
		// draw a Rectangle around the page
		cb.setLineWidth(1);
		cb.rectangle(20, 20, document.getPageSize().getWidth() - 40, document
				.getPageSize().getHeight() - 40);
		cb.stroke();
		cb.restoreState();
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		tpl.beginText();
		tpl.setFontAndSize(helv, 12);
		tpl.setTextMatrix(-5, 0);
		tpl.showText("" + (writer.getPageNumber() - 1));
		tpl.endText();
	}

	private URL getLogoUrl() {
		return Thread.currentThread().getContextClassLoader().getResource(
				"fr/aliasource/webmail/public/images/logo_print.jpg");
	}

}
