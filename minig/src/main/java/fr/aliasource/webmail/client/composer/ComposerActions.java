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

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;

/**
 * The button list at the bottom
 * 
 * @author tom
 * 
 */
public class ComposerActions extends HorizontalPanel implements IUploadListener {

	private Button send;
	private Button saveNow;
	private Button saveTemplate;
	private Button discard;
	private Label savedDate;
	private MailComposer mailComposer;

	private ArrayList<String> runningUploads;

	public ComposerActions(View ui, MailComposer mc) {
		this.mailComposer = mc;
		send = new Button(I18N.strings.send());
		saveNow = new Button(I18N.strings.saveNow());
		saveTemplate = new Button(I18N.strings.saveAsTemplate());
		discard = new Button(I18N.strings.discard());
		savedDate = new Label();

		add(send);
		add(saveNow);
		add(saveTemplate);
		add(discard);
		add(savedDate);
		setSpacing(4);

		setCellVerticalAlignment(savedDate, HorizontalPanel.ALIGN_MIDDLE);

		this.runningUploads = new ArrayList<String>();

		initButtons();
	}

	private void initButtons() {
		send.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				send.setEnabled(false);
				new Timer() {
					@Override
					public void run() {
						send.setEnabled(true);
					}
				}.schedule(500);
				mailComposer.sendMessage(null);
			}
		});

		saveNow.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				mailComposer.saveDraft();
			}
		});

		saveTemplate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				mailComposer.saveTemplate();
			}
		});

		discard.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				mailComposer.discard();
			}
		});
	}

	private void setButtonsEnabled(boolean enabled) {
		send.setEnabled(enabled);
		saveNow.setEnabled(enabled);
		saveTemplate.setEnabled(enabled);
		discard.setEnabled(enabled);
	}

	public void uploadComplete(String attachId) {
		runningUploads.remove(attachId);
		if (runningUploads.isEmpty()) {
			setButtonsEnabled(true);
		}
	}

	public void uploadStarted(String attachId) {
		runningUploads.add(attachId);
		setButtonsEnabled(false);
	}

	public Button getSaveNowButton() {
		return saveNow;
	}

	public Button getSaveTemplateButton() {
		return saveTemplate;
	}

	public Label getSavedDate() {
		return savedDate;
	}

}
