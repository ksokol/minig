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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class MailSuggestion implements Suggestion, IsSerializable {

	private String value;
	private String displayString;

	public MailSuggestion() {

	}

	public MailSuggestion(String val, String display) {
		this.value = val;
		this.displayString = display;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String getReplacementString() {
		return value;
	}

}
