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

import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.server.proxy.client.http.AbstractClientMethod;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public abstract class AbstractFilterMethod extends AbstractClientMethod {

	protected AbstractFilterMethod(HttpClient hc, String backendUrl,
			String action) {
		super(hc, backendUrl, action);
	}

	protected void appendFilterDefinition(Element p, FilterDefinition fd) {
		Element fde = p;
		if (fd.getId() != null) {
			fde.setAttribute("id", fd.getId());
		}
		Element crits = DOMUtils.createElement(fde, "criteria");
		for (String criterion : fd.getCriteria().keySet()) {
			Element crit = DOMUtils.createElement(crits, "crit");
			crit.setAttribute("name", criterion);
			crit.setAttribute("value", fd.getCriteria().get(criterion));
		}
		Element actions = DOMUtils.createElement(fde, "actions");
		if (fd.isDelete()) {
			DOMUtils.createElement(actions, "delete");
		}
		if (fd.isStarIt()) {
			DOMUtils.createElement(actions, "start");
		}
		if (fd.isMarkAsRead()) {
			DOMUtils.createElement(actions, "mark-as-read");
		}
		if (fd.getDeliverInto() != null) {
			DOMUtils.createElementAndText(actions, "deliver-into", fd
					.getDeliverInto());
		}
		if (fd.getForwardTo() != null) {
			DOMUtils
					.createElementAndText(actions, "forward", fd.getForwardTo());
		}
	}

	protected FilterDefinition parseDefinition(Element fd) {
		FilterDefinition ret = new FilterDefinition();
		if (fd.hasAttribute("id")) {
			ret.setId(fd.getAttribute("id"));
		}
		// crits
		String[][] crits = DOMUtils.getAttributes(fd, "crit", new String[] {
				"name", "value" });
		HashMap<String, String> criteria = new HashMap<String, String>();
		for (String[] criterion : crits) {
			criteria.put(criterion[0], criterion[1]);
		}
		ret.setCriteria(criteria);

		// actions
		ret.setStarIt(DOMUtils.getUniqueElement(fd, "start") != null);
		ret.setDelete(DOMUtils.getUniqueElement(fd, "delete") != null);
		ret
				.setMarkAsRead(DOMUtils.getUniqueElement(fd, "mark-as-read") != null);
		ret.setDeliverInto(DOMUtils.getElementText(fd, "deliver-into"));
		ret.setForwardTo(DOMUtils.getElementText(fd, "forward"));

		return ret;
	}

}
