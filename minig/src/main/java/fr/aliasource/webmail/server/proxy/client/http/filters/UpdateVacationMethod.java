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

import fr.aliasource.webmail.client.shared.VacationInfo;

public class UpdateVacationMethod extends AbstractFilterMethod {

	protected String token;

	public UpdateVacationMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/updateVacation.do");
		this.token = token;
	}

	public void updateVacation(VacationInfo vi) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("enabled", "" + vi.isEnabled());
		if (vi.getStart() != null) {
			params.put("start", "" + vi.getStart().getTime());
		}
		if (vi.getEnd() != null) {
			params.put("end", "" + vi.getEnd().getTime());
		}
		params.put("text", vi.getText());

		executeVoid(params);
	}

}
