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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;
import java.util.Map;

public class FilterDefinition implements Serializable {

	private static final long serialVersionUID = -2502775739490070726L;

	private String id;

	private Map<String, String> criteria;

	// actions
	private boolean markAsRead;
	private boolean starIt;
	private String deliverInto;
	private boolean delete;
	private String forwardTo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
	}

	public boolean isMarkAsRead() {
		return markAsRead;
	}

	public void setMarkAsRead(boolean markAsRead) {
		this.markAsRead = markAsRead;
	}

	public boolean isStarIt() {
		return starIt;
	}

	public void setStarIt(boolean starIt) {
		this.starIt = starIt;
	}

	public String getDeliverInto() {
		return deliverInto;
	}

	public void setDeliverInto(String deliverInto) {
		this.deliverInto = deliverInto;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

}
