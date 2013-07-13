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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.obm.sync.book.Address;
import org.obm.sync.book.BookItemsParser;
import org.obm.sync.book.Contact;
import org.obm.sync.book.Email;
import org.obm.sync.book.Phone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.book.UiAddress;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.book.UiEmail;
import fr.aliasource.webmail.client.shared.book.UiPhone;

public class GetContactsMethod extends AbstractClientMethod {

	private String token;

	public GetContactsMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/contacts.do");
		this.token = token;
	}

	public UiContact[] getContacts(ContactGroup cg, String query) {
		List<UiContact> cs = new LinkedList<UiContact>();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("group", "" + cg.getId());
		if (query != null) {
			params.put("query", query);
		}

		Document doc = execute(params);
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			BookItemsParser parser = new BookItemsParser();

			Element root = doc.getDocumentElement();
			NodeList contactNodes = root.getElementsByTagName("contact");

			for (int i = 0; i < contactNodes.getLength(); i++) {
				Element cn = (Element) contactNodes.item(i);
				Contact obm = parser.parseContact(cn);
				UiContact ui = toUi(obm);

				cs.add(ui);
			}
			// String[][] attrs = DOMUtils.getAttributes(root, "c", new String[]
			// {
			// "id", "d", "function", "home_voice", "cell_voice", "home_street",
			// "home_zip", "home_location",
			// "work_voice", "home_fax", "work_fax", "pager", "other",
			// "work_street", "work_zip", "work_location",
			// "other_street", "other_zip", "other_location", "company" });
			//
			// for (int i = 0; i < attrs.length; i++) {
			// UiContact c = new UiContact(attrs[i][0], attrs[i][1],
			// attrs[i][2], attrs[i][3],
			// attrs[i][4], attrs[i][5], attrs[i][6], attrs[i][7], attrs[i][8],
			// attrs[i][9], attrs[i][10], attrs[i][11], attrs[i][12],
			// attrs[i][13], attrs[i][14], attrs[i][15], attrs[i][16],
			// attrs[i][17], attrs[i][18],
			// attrs[i][19]);
			// cs.add(c);
			// }

		}

		return cs.toArray(new UiContact[cs.size()]);
	}

	private UiContact toUi(Contact obm) {
		UiContact ui = new UiContact();

		// TODO Copy other fields

		ui.setLastname(obm.getLastname());
		ui.setFirstname(obm.getFirstname());

		for (String lbl : obm.getEmails().keySet()) {
			Email e = obm.getEmails().get(lbl);
			ui.addEmail(lbl, new UiEmail(e.getEmail()));
		}

		for (String lbl : obm.getAddresses().keySet()) {
			Address e = obm.getAddresses().get(lbl);
			UiAddress a = new UiAddress(e.getStreet(), e.getZipCode(), e
					.getExpressPostal(), e.getTown(), e.getCountry(), e
					.getState());
			ui.addAddress(lbl, a);
		}

		for (String lbl : obm.getPhones().keySet()) {
			Phone e = obm.getPhones().get(lbl);
			ui.addPhone(lbl, new UiPhone(e.getNumber()));
		}

		return ui;
	}
}
