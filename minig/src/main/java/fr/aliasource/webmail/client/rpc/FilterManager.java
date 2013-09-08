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

package fr.aliasource.webmail.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.client.shared.VacationInfo;

@RemoteServiceRelativePath("filters")
public interface FilterManager extends RemoteService {

	void storeFilter(FilterDefinition fd);

	List<FilterDefinition> listFilters();

	void updateFilter(FilterDefinition fd);

	void removeFilter(FilterDefinition fd);

	VacationInfo getVacationInfo();

	void updateVacationInfo(VacationInfo vi);

	ForwardInfo getForwardInfo();

	void setForwardInfo(ForwardInfo fi);

}
