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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.DockPanel;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.settings.IServerSettingsListener;
import fr.aliasource.webmail.client.shared.Body;

/**
 * The body editor part of the mail composer
 * 
 * @author tom
 * 
 */
public class BodyEditor extends DockPanel implements
		IdentitySelectionBoxChangeListener, IServerSettingsListener {

	private MinigRichTextArea mta;

	private ResizeHandler resizeListener;
	private MailComposer mc;
	private ComposerToolbarSwitcher cts;

	public BodyEditor(MailComposer mc, final View ui) {
		super();
		this.mc = mc;

		mta = new MinigRichTextArea(ui, mc);
		mta.addStyleName("whiteBackground");
		mta.addKeyboardListener(createKeyboardListener());
		add(mta, DockPanel.CENTER);

		cts = new ComposerToolbarSwitcher(mta);
		add(cts.getWidget(), DockPanel.NORTH);

		setStyleName("bodyEditor");
		resizeListener = createResizeListener();

		if (mc.getIdentities() != null) {
			mc.getIdentities().getIdentititesSelectionBox()
					.addIdentitySelectionBoxChangeListener(this);
		}
		WebmailController.get().addServerSettingsListener(this);
	}

	public void destroy() {
		GWT.log("destroy bodyEditor", null);
		WebmailController.get().removeServerSettingsListener(this);
	}

	void switchToPlainText(boolean withConfirm) {
		cts.switchToPlain();
	}

	public Body getMailBody() {
		return mta.getMailBody();
	}

	public void setMailBody(Body b) {
		mta.setMailBody(b,true);
	}

	public void focus() {
		mta.setFocus(true);
	}

	private KeyPressHandler createKeyboardListener() {
		KeyPressHandler kl = new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (!mc.isTimerStarted()) {
					mc.startAutoSaveDraftTimer();
				}
			}
		};
		return kl;
	}

	public void resize(int height) {
		mta.setHeight(height);
	}

	private ResizeHandler createResizeListener() {
		return new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				mta.setHeight(event.getHeight());
			}
		};
	}

	@Override
	public void notifyIdentityChanged() {
		update();
	}

	@Override
	public void settingsReceived() {
		GWT.log("bodyEditor received new server settings", null);
		if (mc.getIdentities() != null) {
			IdentitiesSelectionBox isb = mc.getIdentities()
					.getIdentititesSelectionBox();
			isb.removeIdentitySelectionBoxChangeListener(this);
			isb.addIdentitySelectionBoxChangeListener(this);
		}
		update();
	}

	public void update(Body mailBody, boolean updateSignature) {
		mta.update(mailBody, updateSignature);
		if(mta.isHtmlBody()){
			cts.switchToRich();
		}
	}
	
	public void update(Body mailBody) {
		update(mailBody, true);
	}

	public void update() {
		update(mta.getMailBody(), true);
	}

	public boolean isEmpty() {
		return mta.isEmpty();
	}

	public ResizeHandler getResizeListener() {
		return resizeListener;
	}

	public boolean shouldSendInPlain() {
		return cts.isPlainOnly();
	}

	public void reset() {
		update(new Body());
		switchToPlainText(false);
		cts.reset();
	}

}
