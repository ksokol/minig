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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ForwardInfo;

/**
 * Signature setting component
 * 
 * @author tom
 * 
 */
public class ForwardSetting extends FlexTable implements IChangeListener,
		ISetting {

	private List<ISettingChangeListener> listeners;
	private RadioButton disabled;
	private RadioButton enabled;
	private TextBox email;
	private ListBox localCopy;

	public ForwardSetting(View ui) {
		// this.ui = ui;
		// setWidth("100%");
		setStyleName("settingsTable");
		listeners = new ArrayList<ISettingChangeListener>();

		disabled = new RadioButton("enabled", I18N.strings.forwardDisable());
		setWidget(0, 0, disabled);
		disabled.addStyleName("noWrap");
		disabled.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				notifyChange();
			}
		});

		enabled = new RadioButton("enabled", I18N.strings.forwardEnable());
		enabled.addStyleName("noWrap");
		setWidget(1, 0, enabled);
		enabled.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				notifyChange();
			}
		});

		email = new TextBox();
		email.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				notifyChange();
			}
		});
		setWidget(1, 1, email);

		setWidget(1, 2, new Label(I18N.strings.forwardAnd()));

		localCopy = new ListBox(false);
		localCopy.addItem(I18N.strings.forwardKeep(), "true");
		localCopy.addItem(I18N.strings.forwardDelete(), "false");
		localCopy.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				notifyChange();
			}
		});
		setWidget(1, 3, localCopy);

		getRowFormatter()
				.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		getRowFormatter()
				.setVerticalAlign(1, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	private void update(ForwardInfo fi) {
		enabled.setEnabled(fi.isAllowed());
		disabled.setEnabled(fi.isAllowed());
		email.setEnabled(fi.isAllowed());
		localCopy.setEnabled(fi.isAllowed());

		enabled.setValue(fi.isEnabled());
		disabled.setValue(!fi.isEnabled());
		email.setText(fi.getEmail());
		localCopy.setItemSelected(fi.isLocalCopy() ? 0 : 1, true);
	}

	public void addChangeListener(ISettingChangeListener listener) {
		this.listeners.add(listener);
	}

	public Widget getDescriptionWidged() {
		return new HTML("<b>" + I18N.strings.forwardDescription() + "</b>");
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
		AjaxCall.filters.getForwardInfo(new AsyncCallback<ForwardInfo>() {

			@Override
			public void onSuccess(ForwardInfo result) {
				GWT.log("getForward::onSuccess: " + result, null);
				update(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("error loading forward", caught);
			}
		});
	}

	public void saveSetting() {
		ForwardInfo fi = getForward();
		AjaxCall.filters.setForwardInfo(fi, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("error saving forward", caught);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Forward saved.", null);
			}

		});
	}

	private ForwardInfo getForward() {
		ForwardInfo fi = new ForwardInfo();
		fi.setEnabled(enabled.getValue());
		fi.setEmail(email.getText());
		fi.setLocalCopy(localCopy.getSelectedIndex() == 0 ? true : false);
		fi.setAllowed(true);
		return fi;
	}
}
