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

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.FilterDefinition;
import fr.aliasource.webmail.client.shared.ForwardInfo;
import fr.aliasource.webmail.client.shared.VacationInfo;

public interface FilterManagerAsync {

	void storeFilter(FilterDefinition fd, AsyncCallback<Void> cb);

	void listFilters(AsyncCallback<List<FilterDefinition>> cb);

	void updateFilter(FilterDefinition fd, AsyncCallback<Void> cb);

	void removeFilter(FilterDefinition fd, AsyncCallback<Void> cb);

	void getVacationInfo(AsyncCallback<VacationInfo> callback);

	void updateVacationInfo(VacationInfo vi, AsyncCallback<Void> callback);

	void getForwardInfo(AsyncCallback<ForwardInfo> callback);

	void setForwardInfo(ForwardInfo fi, AsyncCallback<Void> callback);

}
