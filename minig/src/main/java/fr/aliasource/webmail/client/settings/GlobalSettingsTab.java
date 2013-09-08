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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * 
 * @author matthieu
 * 
 */
public class GlobalSettingsTab extends DockPanel implements ISettingsPage,
		ISettingChangeListener {

	View ui;
	GlobalSettingsDataGrid dataGrid;
	private List<ISetting> settings;
	Button save;

	public GlobalSettingsTab(View ui) {
		this.ui = ui;
		this.settings = new ArrayList<ISetting>();
		setWidth("100%");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");

		dataGrid = new GlobalSettingsDataGrid();
		verticalPanel.add(dataGrid);
		add(verticalPanel, DockPanel.CENTER);

		addUpdateSettingsButton();
	}

	private void addUpdateSettingsButton() {

		HorizontalPanel hPanel = new HorizontalPanel();
		Button cancel = new Button(I18N.strings.cancel());
		cancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				WebmailController.get().getSelector().select(
						new Folder("INBOX"));
			}
		});

		save = new Button(I18N.strings.saveChanges());
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				for (ISetting setting : settings) {
					setting.saveSetting();
				}
				save.setEnabled(false);
			}
		});
		save.setEnabled(false);
		hPanel.add(cancel);
		hPanel.add(save);
		add(hPanel, DockPanel.SOUTH);

	}

	public void addChangeListener(IChangeListener changeListener) {
		changeListener.addChangeListener(this);
	}

	public void init() {
		dataGrid.updateGrid(settings);
	}

	public void notifySettingChanged() {
		save.setEnabled(true);
	}

	public void update(List<ISetting> settings) {
		this.settings = settings;
		for (ISetting setting : this.settings) {
			addChangeListener(setting);
		}
		dataGrid.updateGrid(this.settings);
	}

	@Override
	public void shutdown() {
		GWT.log("shutdown should be implemented", null);
	}
}
