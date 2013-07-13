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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;

public class MaxPageSizeSetting implements ISetting, IChangeListener {

	private View ui;
	private ListBox list;
	private List<ISettingChangeListener> listeners;

	public MaxPageSizeSetting(View ui) {
		this.ui = ui;
		this.list = new ListBox();
		this.list.addItem("25");
		this.list.addItem("50");
		this.list.addItem("100");
		listeners = new ArrayList<ISettingChangeListener>();
		this.list.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ce) {
				for (ISettingChangeListener listener : listeners) {
					listener.notifySettingChanged();
				}
			}
		});
	}

	public void addChangeListener(ISettingChangeListener listener) {
		this.listeners.add(listener);
	}

	public Widget getDescriptionWidged() {
		return new HTML("<b>" + I18N.strings.maxPageSize() + ":</b>");
	}

	public Widget getFormWidged() {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(new HTML("<b>" + I18N.strings.show() + "</b>&nbsp;"));
		hPanel.add(list);
		hPanel.add(new HTML("&nbsp;<b>" + I18N.strings.convPerPage() + "</b>"));
		return hPanel;
	}

	public void saveSetting() {
		ui.log("Saving MaxPageSizeSetting");
		// TODO Save Settings
	}

	public void init() {
		// TODO read setting from backend
	}

}
