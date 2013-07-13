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

public class QuotaInfo implements Serializable {

	private static final long serialVersionUID = -5543747161870534944L;
	private boolean enable;
	private int usage;
	private int limit;
	private int filling;

	public QuotaInfo() {
		this.enable = false;
		this.usage = 0;
		this.limit = 0;
		this.filling = 0;
	}

	public QuotaInfo(boolean enable, int usages, int limites) {
		this.enable = enable;
		this.usage = usages;
		this.limit = limites;
		if (enable && limites != 0) {
			this.filling = Math.min((usages * 100) / limites, 100);
		} else {
			this.filling = 0;
		}
	}

	public boolean isEnable() {
		return enable;
	}

	public int getUsage() {
		return usage;
	}

	public int getLimit() {
		return limit;
	}

	public String toString() {
		return "enable: " + enable + " usages: " + usage + " limit: " + limit
				+ " filling: " + filling;
	}

	public int getFilling() {
		return filling;
	}

}
