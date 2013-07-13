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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class ListFiltersMethod extends AbstractFilterMethod {

	protected String token;

	public ListFiltersMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/listFilters.do");
		this.token = token;
	}

	public List<FilterDefinition> listFilters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		Document doc = execute(params);
		if (logger.isDebugEnabled()) {
			DOMUtils.logDom(doc);
		}

		NodeList nl = doc.getElementsByTagName("filter-definition");
		List<FilterDefinition> ret = new ArrayList<FilterDefinition>(nl
				.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			ret.add(parseDefinition(e));
		}
		return ret;
	}

}
