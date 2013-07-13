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

import java.util.List;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

/**
 * The grid widgets with the list of settings
 * 
 * @author matthieu
 * 
 */
public class GlobalSettingsDataGrid extends Grid {

	public GlobalSettingsDataGrid() {
		super(1, 2);
		setWidth("100%");
		getCellFormatter().setWidth(0, 0, "30%");
		setStyleName("settingsTable");
	}

	public void updateGrid(List<ISetting> settings) {
		clear();
		resizeRows(1);
		int rc = settings.size();
		if (rc == 0) {
			showEmptyList();
		} else {
			if (getRowCount() != rc) {
				resizeRows(rc);
			}
			for (int i = 0; i < rc; i++) {
				ISetting setting = settings.get(i);
				setting.init();
				fillRow(setting, i);
			}
		}
	}

	private void fillRow(ISetting settings, int i) {
		if (i % 2 == 0) {
			getRowFormatter().setStyleName(i, "odd");
		} else {
			getRowFormatter().setStyleName(i, "even");
		}

		getCellFormatter().setStyleName(i, 0, "settingsCell");
		getCellFormatter().setStyleName(i, 1, "settingsCell");
		setWidget(i, 0, settings.getDescriptionWidged());
		setWidget(i, 1, settings.getFormWidged());

	}

	private void showEmptyList() {
		clear();
		resizeRows(1);
		setWidget(0, 0, new Label("No available folder"));
	}

}
