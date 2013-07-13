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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.Features;
import fr.aliasource.webmail.client.ctrl.WebmailController;

/**
 * Settings widget
 * 
 * @author matthieu
 * 
 */
public class SettingsPanel extends VerticalPanel implements
		IServerSettingsListener {

	private GlobalSettingsTab globalSettingsTab;
	private FolderSettingsTab folderSettingsTab;
	private FilterSettingsTab filterSettingsTab;

	public static int GLOBAL_SETTINGS_TAG = 0;
	public static int FOLDER_SETTINGS_TAG = 1;
	public static int FILTER_SETTINGS_TAG = 2;

	private HorizontalPanel sectionTitles;
	private DeckPanel sections;

	private View ui;

	public SettingsPanel(View wm) {
		this.ui = wm;
		sectionTitles = new HorizontalPanel();
		sectionTitles.setSpacing(5);
		sectionTitles.addStyleName("panelActions");
		sections = new DeckPanel();
		sections.setWidth("100%");

		add(sectionTitles);
		add(sections);

		globalSettingsTab = new GlobalSettingsTab(ui);

		folderSettingsTab = new FolderSettingsTab(ui);

		filterSettingsTab = new FilterSettingsTab(ui);

		addSettingsSection(globalSettingsTab, I18N.strings.general());
		addSettingsSection(folderSettingsTab, I18N.strings.folders());
		if (Features.FILTERS) {
			addSettingsSection(filterSettingsTab, I18N.strings
					.filtersTabTitle());
		}
		WebmailController.get().addServerSettingsListener(this);
	}

	public void addSettingsSection(Widget w, String label) {
		if (sectionTitles.getWidgetCount() > 0) {
			sectionTitles.add(new Label("|"));
		}
		final int cnt = sections.getWidgetCount();
		sections.add(w);
		Anchor cat = new Anchor(label);
		sectionTitles.add(cat);
		cat.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				sections.showWidget(cnt);
				Widget page = sections.getWidget(sections.getVisibleWidget());
				if (page instanceof ISettingsPage) {
					((ISettingsPage) page).init();
				}
			}
		});
	}

	public void showFolderSettings() {
		sections.showWidget(FOLDER_SETTINGS_TAG);
		folderSettingsTab.init();
	}

	public void showFilterSettings() {
		sections.showWidget(FILTER_SETTINGS_TAG);
		filterSettingsTab.init();
	}

	public void showGlobalSettings() {
		sections.showWidget(GLOBAL_SETTINGS_TAG);
		globalSettingsTab.init();
	}

	public void settingsReceived() {

		List<ISetting> settings = new ArrayList<ISetting>();

		// settings.add(new MaxPageSizeSetting(ui));
		settings.add(new SignatureSettingDataGrid(ui));
		if (Features.FILTERS) {
			settings.add(new ForwardSetting(ui));
			settings.add(new VacationSetting(ui));

			// Map<String, String> allSettings = ui.getSettings();
			// for (String k : allSettings.keySet()) {
			// if (k.startsWith("obm/")) {
			// settings.add(new OBMSetting(k, allSettings.get(k)));
			// }
			// }
		}
		globalSettingsTab.update(settings);
	}

}
