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
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.VacationInfo;

/**
 * Vacation setting component
 * 
 * @author tom
 * 
 */
public class VacationSetting extends VerticalPanel implements IChangeListener,
		ISetting {

	private TextArea textArea;
	private List<ISettingChangeListener> listeners;
	// private Webmail ui;
	private VacationStatusBox vsb;

	public VacationSetting(View ui) {
		// this.ui = ui;
		setWidth("100%");
		setStyleName("settingsTable");
		listeners = new ArrayList<ISettingChangeListener>();
		FlexTable hp = new FlexTable();
		vsb = new VacationStatusBox(listeners);
		hp.setWidget(1, 0, vsb);
		hp.getFlexCellFormatter().setColSpan(1, 0, 2);
		hp.setWidget(2, 0, new Label(I18N.strings.vacationText()));
		textArea = new TextArea();
		textArea.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				notifyChange();
			}
		});
		textArea.setWidth("400px");
		textArea.setHeight("100px");
		hp.setWidget(2, 1, textArea);
		hp.getCellFormatter().setAlignment(2, 0, HorizontalPanel.ALIGN_LEFT,
				VerticalPanel.ALIGN_TOP);
		add(hp);
	}

	private void updateGrid(VacationInfo vi) {
		textArea.setText(vi.getText());
		vsb.setState(vi);
	}

	private void saveVacation() {
		GWT.log("Save vacation", null);
		AjaxCall.filters.updateVacationInfo(getVacationInfo(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("error saving vacation", caught);
					}

					@Override
					public void onSuccess(Void result) {
					}
				});
	}

	private VacationInfo getVacationInfo() {
		VacationInfo vi = vsb.getState();
		vi.setText(textArea.getText());
		return vi;
	}

	public void addChangeListener(ISettingChangeListener listener) {
		this.listeners.add(listener);
	}

	public Widget getDescriptionWidged() {
		return new HTML("<b>" + I18N.strings.vacation() + "</b><br/>"
				+ I18N.strings.vacationDescription());
	}

	public Widget getFormWidged() {
		return this;
	}

	private void notifyChange() {
		for (ISettingChangeListener scl : listeners) {
			scl.notifySettingChanged();
		}
	}

	public void init() {
		AjaxCall.filters.getVacationInfo(new AsyncCallback<VacationInfo>() {

			@Override
			public void onSuccess(VacationInfo result) {
				updateGrid(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void saveSetting() {
		saveVacation();
	}
}
