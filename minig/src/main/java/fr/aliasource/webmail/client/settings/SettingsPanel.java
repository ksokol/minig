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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;

/**
 * Settings widget
 * 
 * @author matthieu
 * 
 */
public class SettingsPanel extends VerticalPanel {

    private FolderSettingsTab folderSettingsTab;
    public static int FOLDER_SETTINGS_TAG = 0;
    private DeckPanel sections;
    private View ui;

    public SettingsPanel(View wm) {
        this.ui = wm;

        sections = new DeckPanel();
        sections.setWidth("100%");

        add(sections);

        folderSettingsTab = new FolderSettingsTab(ui);
        addSettingsSection(folderSettingsTab, I18N.strings.folders());
    }

    public void addSettingsSection(Widget w, String label) {
        final int cnt = sections.getWidgetCount();
        sections.add(w);
        Anchor cat = new Anchor(label);

        cat.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent ev) {
                sections.showWidget(cnt);
                // Widget page =
                // sections.getWidget(sections.getVisibleWidget());
            }
        });
    }

    public void showFolderSettings() {
        sections.showWidget(FOLDER_SETTINGS_TAG);
        folderSettingsTab.init();
    }

}
