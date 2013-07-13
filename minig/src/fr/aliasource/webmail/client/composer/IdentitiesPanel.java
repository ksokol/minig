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

package fr.aliasource.webmail.client.composer;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;

public class IdentitiesPanel extends HorizontalPanel {

	private IdentitiesSelectionBox isb;

	public IdentitiesPanel() {
		super();

		Label from = new Label(I18N.strings.from() + ": ");
		add(from);
		setCellHorizontalAlignment(from, HorizontalPanel.ALIGN_RIGHT);
		isb = new IdentitiesSelectionBox();
		add(isb);
		// isb.setWidth("46em");
		// from.setWidth("4em");

		// setSpacing(1);
		setCellWidth(isb, "100%");
		setCellVerticalAlignment(from, VerticalPanel.ALIGN_MIDDLE);

	}

	public IdentitiesSelectionBox getIdentititesSelectionBox() {
		return isb;
	}
}
