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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.EmailAddress;

/**
 * Signature setting component
 * 
 * @author tom
 * 
 */
public class SignatureSettingDataGrid extends VerticalPanel implements
		IChangeListener, ISetting {

	private Map<String, TextArea> textAreas;
	private List<ISettingChangeListener> listeners;
	private ListBox listBox;
	private View ui;
	private HorizontalPanel hPanel;

	public SignatureSettingDataGrid(View ui) {
		this.ui = ui;
		setWidth("100%");
		setStyleName("settingsTable");
		textAreas = new HashMap<String, TextArea>();
		listBox = new ListBox();
		listeners = new ArrayList<ISettingChangeListener>();
		listBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				updateTextAreaSignature();
			}
		});
		add(listBox);
		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		add(hPanel);
	}

	private void updateTextAreaSignature() {
		int selectedIndex = listBox.getSelectedIndex();
		TextArea textArea = textAreas.get(listBox.getValue(selectedIndex));
		hPanel.clear();
		if (textArea != null) {
			hPanel.add(textArea);
		}
	}

	private void updateGrid(List<EmailAddress> identities) {
		listBox.clear();
		textAreas.clear();
		for (EmailAddress address : identities) {
			textAreas.put(address.getEmail(), getTextArea(address));
			listBox.addItem(address.getEmail());
		}

		updateTextAreaSignature();
	}

	private TextArea getTextArea(EmailAddress addr) {
		TextArea textArea = new TextArea();
		textArea.setText(addr.getSignature());
		textArea.setWidth("400px");
		textArea.setHeight("100px");
		textAreas.put(addr.getEmail(), textArea);

		textArea.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				for (ISettingChangeListener listener : listeners) {
					listener.notifySettingChanged();
				}
			}
		});

		return textArea;
	}

	private void saveSignature() {

		Map<String, String> identities = new HashMap<String, String>();
		for (Entry<String, TextArea> entry : textAreas.entrySet()) {
			identities.put(entry.getKey(), entry.getValue().getText());
		}
		ui.getSpinner().startSpinning();
		AjaxCall.settingsManager.saveSignature(identities,
				new AsyncCallback<Map<String, String>>() {
					public void onSuccess(Map<String, String> result) {
						ui.getSpinner().stopSpinning();
						WebmailController.get().updateServerSettings(result);
					}

					public void onFailure(Throwable caught) {
						ui.getSpinner().stopSpinning();
						ui.log("Cannot save signature", caught);
					}
				});
	}

	private List<EmailAddress> getIdentities() {
		List<EmailAddress> identities = null;
		if (WebmailController.get().hasIdentities()) {
			identities = WebmailController.get().getIdentities();
		} else {
			identities = new ArrayList<EmailAddress>();
			identities.add(WebmailController.get().getIdentity());
		}
		return identities;
	}

	public void addChangeListener(ISettingChangeListener listener) {
		this.listeners.add(listener);
	}

	public Widget getDescriptionWidged() {
		return new HTML("<b>" + I18N.strings.signature() + "</b><br/>"
				+ I18N.strings.signatureDescription());
	}

	public Widget getFormWidged() {
		return this;
	}

	public void init() {
		updateGrid(getIdentities());
	}

	public void saveSetting() {
		saveSignature();
	}
}
