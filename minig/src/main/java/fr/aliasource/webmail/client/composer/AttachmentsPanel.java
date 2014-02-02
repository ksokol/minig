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
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IAttachmentMetadata;
import fr.aliasource.webmail.client.shared.IAttachmentMetadataList;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

public class AttachmentsPanel extends VerticalPanel {

	private VerticalPanel attachPanel;
	private VerticalPanel attachList;
	private ArrayList<String> managedIds;
	private ArrayList<IUploadListener> uploadListeners;
	private Anchor attach;
	private String messageId;
	private MailComposer composer;

	public AttachmentsPanel(MailComposer composer) {
		this.managedIds = new ArrayList<String>();
		this.uploadListeners = new ArrayList<IUploadListener>();
		this.composer = composer;
		createAttachHyperlink();
	}

	private void createAttachHyperlink() {
		attachPanel = new VerticalPanel();
		attachList = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("panelActions");
		Label l = new Label();
		attach = new Anchor(I18N.strings.attachFile());
		attach.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (messageId == null) {
					composer.takeSnapshotFromDraft(new TailCall() {

						@Override
						public void run() {
							attachIdReceived(false);
						}

					});
				} else {
					attachIdReceived(false);
				}
			}
		});
		hp.add(l);
		hp.add(attach);
		attachPanel.add(attachList);
		attachPanel.add(hp);
		attachPanel.setStyleName("enveloppeField");
		add(attachPanel);
	}

	public void setAttachments(IAttachmentMetadataList list) {
		// AttachmentUploadWidget uw = newFileUpload(alreadyOnServer);
		// attachList.add(uw);

        attachList.clear();

		for (IAttachmentMetadata meta : list.getAttachmentMetadata()) {
			// HorizontalPanel hp = new HorizontalPanel();
			// Label uploadInfo = new Label();
			// // uploadInfo.setText("File '" + attachementId + "' attached.");
			// hp.add(uploadInfo);
			//
			// hp.add(new AttachmentDisplay(meta));

			AttachmentUploadWidget uw = new AttachmentUploadWidget(this, meta);
			attachList.add(uw);
			attach.setText(I18N.strings.attachAnotherFile());
			// return uw;
		}

		// attach.setText(I18N.strings.attachAnotherFile());

	}

	private AttachmentUploadWidget attachIdReceived(boolean alreadyOnServer) {
		AttachmentUploadWidget uw = newFileUpload(alreadyOnServer);
		attachList.add(uw);
		attach.setText(I18N.strings.attachAnotherFile());
		return uw;
	}

	private AttachmentUploadWidget newFileUpload(boolean alreadyOnServer) {
		return new AttachmentUploadWidget(this, messageId, alreadyOnServer);
	}

	public List<String> getAttachementIds() {
		return Collections.unmodifiableList(managedIds);
	}

	public void registerUploadListener(IUploadListener ul) {
		uploadListeners.add(ul);
	}

	public void notifyUploadStarted(String attachId) {
		for (int i = 0; i < uploadListeners.size(); i++) {
			((IUploadListener) uploadListeners.get(i)).uploadStarted(attachId);
		}
	}

	public void notifyUploadComplete(String attachId) {
		managedIds.add(attachId);
		for (IUploadListener ul : uploadListeners) {
			ul.uploadComplete(attachId);
		}
	}

	public boolean isEmpty() {
		return managedIds.isEmpty();
	}

	public void reset() {
		clear();
		managedIds.clear();
		createAttachHyperlink();
		messageId = null;
	}

	public void dropAttachment(final AttachmentUploadWidget attachment) {
		Ajax<String> request = AjaxFactory.deleteAttachment(messageId, attachment.getAttachmentId());
		WebmailController.get().getView().getSpinner().startSpinning();

		try {
			request.send(new AjaxCallback<String>() {

				@Override
				public void onSuccess(String object) {
					managedIds.remove(attachment.getAttachmentId());
                    attachList.remove(attachment);
					WebmailController.get().getView().getSpinner().stopSpinning();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					WebmailController.get().getView().getSpinner().stopSpinning();
					if (exception != null) {
						WebmailController.get().getView().notifyUser(exception.getMessage());
					} else {
						WebmailController.get().getView().notifyUser("something goes wrong.");
					}
				}
			});
		} catch (RequestException e) {
			WebmailController.get().getView().notifyUser(e.getMessage());
			WebmailController.get().getView().getSpinner().stopSpinning();
		}
	}

	public void showAttach(String attachId) {
		attachIdReceived(true);
	}

	public VerticalPanel getAttachList() {
		return attachList;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
		this.composer.setMessageId(messageId);
	}

}
