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

package fr.aliasource.webmail.server.proxy.client.http.filters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.VacationInfo;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class FetchVacationMethod extends AbstractFilterMethod {

	protected String token;

	public FetchVacationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/fetchVacation.do");
		this.token = token;
	}

	public VacationInfo fetchVacation() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		if (logger.isDebugEnabled()) {
			DOMUtils.logDom(doc);
		}

		Element r = doc.getDocumentElement();
		VacationInfo vi = new VacationInfo();
		vi.setEnabled("true".equals(r.getAttribute("enabled")));
		if (r.hasAttribute("start")) {
			vi.setStart(new Date(Long.parseLong(r.getAttribute("start"))));
		}
		if (r.hasAttribute("end")) {
			vi.setEnd(new Date(Long.parseLong(r.getAttribute("end"))));
		}
		vi.setText(r.getTextContent());

		return vi;
	}

}
