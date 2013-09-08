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

import com.google.gwt.user.client.ui.DockPanel;

import fr.aliasource.webmail.client.filter.CreateAFilterForm;

/**
 * Toolbar with search box
 * 
 * 
 * @author tom
 * 
 */
public class Toolbar extends DockPanel {

	private DockPanel dp;
	private SearchBox search;
	private AdvancedSearchForms asf;
	private CreateAFilterForm caf;

	public Toolbar(View webmail) {
		final View ui = webmail;

		setWidth("100%");

		dp = new DockPanel();
		search = new SearchBox(ui);
		dp.add(search, DockPanel.WEST);

		asf = new AdvancedSearchForms(ui);
		dp.add(asf, DockPanel.NORTH);
		asf.setVisible(false);

		caf = new CreateAFilterForm(ui);
		dp.add(caf, DockPanel.NORTH);
		caf.setVisible(false);

		add(dp, DockPanel.NORTH);
		dp.setWidth("100%");
		setWidth("100%");
		setSpacing(4);
	}

	public SearchBox getSearchBox() {
		return search;
	}

	public AdvancedSearchForms getAdvancedSearchBox() {
		return asf;
	}

	public CreateAFilterForm getCreateAFilterBox() {
		return caf;
	}

}