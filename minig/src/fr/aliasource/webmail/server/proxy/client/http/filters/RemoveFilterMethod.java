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
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.shared.FilterDefinition;

public class RemoveFilterMethod extends AbstractFilterMethod {

	protected String token;

	public RemoveFilterMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/deleteFilter.do");
		this.token = token;
	}

	public void remove(FilterDefinition fd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("filterId", fd.getId());
		executeVoid(params);
	}

}
