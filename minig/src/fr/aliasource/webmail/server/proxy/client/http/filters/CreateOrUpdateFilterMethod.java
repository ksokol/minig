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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;

import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class CreateOrUpdateFilterMethod extends AbstractFilterMethod {

	protected String token;

	public CreateOrUpdateFilterMethod(HttpClient hc, String token,
			String backendUrl) {
		super(hc, backendUrl, "/updateFilter.do");
		this.token = token;
	}

	public void createOrUpdate(FilterDefinition fd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		try {
			Document filter = DOMUtils.createDoc(
					"http://minig.org/xsd/filter-definition.xsd",
					"filter-definition");
			if (fd != null) {
				appendFilterDefinition(filter.getDocumentElement(), fd);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DOMUtils.serialise(filter, out);
				params.put("filter", new String(out.toByteArray()));
			}
			executeVoid(params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
