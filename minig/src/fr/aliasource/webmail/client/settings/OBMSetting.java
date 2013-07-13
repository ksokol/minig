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

package fr.aliasource.webmail.client.settings;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Read only setting coming from OBM database
 * 
 * @author tom
 * 
 */
public class OBMSetting implements ISetting {

	private String key;
	private String value;

	public OBMSetting(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public void addChangeListener(ISettingChangeListener listener) {
		// TODO Auto-generated method stub
	}

	public Widget getDescriptionWidged() {
		return new HTML("<b>" + key + "</b>");
	}

	public Widget getFormWidged() {
		return new Label(value);
	}

	public void init() {
		// TODO Auto-generated method stub
	}

	public void saveSetting() {
		// TODO Auto-generated method stub

	}

}
