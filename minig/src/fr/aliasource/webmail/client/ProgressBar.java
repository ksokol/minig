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

package fr.aliasource.webmail.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBar extends Widget {

	private Element shell;
	private Element fill;

	public ProgressBar() {
		shell = DOM.createDiv();
		setElement(shell);
		DOM.setStyleAttribute(shell, "position", "relative");
		setStyleName("quotaBar-shell");

		// Create the bar element
		fill = DOM.createDiv();
		DOM.appendChild(getElement(), fill);
		DOM.setStyleAttribute(fill, "height", "100%");
		DOM.setElementProperty(fill, "className", "quotaBar-bar");
		DOM.setElementProperty(fill, "className", "quotaBar-bar-low");

	}

	public void setPercent(int percent) {
		DOM.setStyleAttribute(fill, "width", percent + "%");
	}

}
