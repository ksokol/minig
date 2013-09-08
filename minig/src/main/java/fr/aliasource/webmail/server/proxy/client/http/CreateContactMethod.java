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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.obm.sync.book.BookItemsWriter;
import org.obm.sync.book.Contact;
import org.obm.sync.book.Email;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.book.UiContact;

public class CreateContactMethod extends AbstractClientMethod {

	private String token;

	public CreateContactMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/createContact.do");
		this.token = token;
	}

	public void createContact(UiContact ui) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		BookItemsWriter writer = new BookItemsWriter();
		Document doc = null;
		try {
			doc = DOMUtils.createDoc("http://minig.org/xsd/createContact.xsd",
					"contact");
			Element root = doc.getDocumentElement();
			Contact c = fromUi(ui);
			writer.appendContact(root, c);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			params.put("contact", new String(out.toByteArray()));
			executeVoid(params);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Contact fromUi(UiContact ui) {
		Contact c = new Contact();

		// TODO Copy other fields

		c.setLastname(ui.getLastname());
		for (String lbl : ui.getEmails().keySet()) {
			c.addEmail(lbl, new Email(ui.getEmails().get(lbl).getEmail()));
		}
		return c;
	}
}
