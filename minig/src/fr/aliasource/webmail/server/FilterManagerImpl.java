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

package fr.aliasource.webmail.server;

import java.util.List;

import fr.aliasource.webmail.client.rpc.FilterManager;
import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.client.shared.VacationInfo;

public class FilterManagerImpl extends SecureAjaxCall implements FilterManager {

	private static final long serialVersionUID = -3834547558187407931L;

	@Override
	public List<FilterDefinition> listFilters() {
		return getAccount().listFilters();
	}

	@Override
	public void removeFilter(FilterDefinition fd) {
		getAccount().removeFilter(fd);
	}

	@Override
	public void storeFilter(FilterDefinition fd) {
		getAccount().storeFilter(fd);
	}

	@Override
	public void updateFilter(FilterDefinition fd) {
		getAccount().updateFilter(fd);
	}

	@Override
	public ForwardInfo getForwardInfo() {
		return getAccount().fetchForward();
	}

	@Override
	public VacationInfo getVacationInfo() {
		VacationInfo v = getAccount().fetchVacation();
		return v;
	}

	@Override
	public void setForwardInfo(ForwardInfo fi) {
		getAccount().updateForward(fi);
	}

	@Override
	public void updateVacationInfo(VacationInfo vi) {
		getAccount().updateVacation(vi);
	}

}
