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

package fr.aliasource.webmail.client.filter;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

import fr.aliasource.webmail.client.I18N;

public class ForwardToWidget extends HorizontalPanel {

	private CheckBox cb;
	private TextBox addr;
	private HandlerRegistration reg;

	public ForwardToWidget() {
		cb = new CheckBox(I18N.strings.actionForwardItTo());

		addr = new TextBox();
		addr.setText(I18N.strings.forwardPlaceholder());
		add(cb);
		add(new HTML("&nbsp;"));
		add(addr);

		reg = addr.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent sender) {
				addr.setText("");
				reg.removeHandler();
			}
		});
	}

	public String getEmail() {
		if (addr.getText() != null && addr.getText().contains("@")) {
			return addr.getText();
		} else {
			return null;
		}
	}

}
