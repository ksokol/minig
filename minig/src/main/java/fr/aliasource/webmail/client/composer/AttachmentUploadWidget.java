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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.reader.AttachmentDisplay;
import fr.aliasource.webmail.client.shared.IAttachmentMetadata;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

/**
 * Widget used to display the attachments upload form and a link to the uploaded
 * attachment.
 * 
 * @author tom
 * 
 */
public class AttachmentUploadWidget extends FormPanel {

	private String attachementId;
	private FileUpload upload;
	private Image upSpinner;
	private Anchor droppAttachmentLink;
	private AttachmentsPanel attachPanel;
	public DockPanel dp;
	private String messageId;
	private IAttachmentMetadata meta;

	public AttachmentUploadWidget(AttachmentsPanel attPanel, IAttachmentMetadata meta) {
		this.attachPanel = attPanel;
		// this.messageId = messageId;
		dp = new DockPanel();
		dp.setSpacing(1);

		attachementId = meta.getFileName();
		this.meta = meta;
		setWidget(dp);
		buildDisplay(dp);
	}

	public AttachmentUploadWidget(AttachmentsPanel attPanel, String messageId, boolean alreadyOnServer) {
		this.attachPanel = attPanel;
		this.messageId = messageId;
		dp = new DockPanel();
		dp.setSpacing(1);

		setWidget(dp);

		if (!alreadyOnServer) {
			// when we create a forward, the attachment are already on the
			// backend
			buildUpload(dp);
		} else {
			// we're creating a forwarded message
			buildDisplay(dp);
		}

	}

	private void buildDisplay(final DockPanel dp) {
		// attachPanel.notifyUploadComplete(attachementId);
		droppAttachmentLink = new Anchor(I18N.strings.delete());
		droppAttachmentLink.addClickHandler(createDropAttachmentClickListener());
		HorizontalPanel eastPanel = new HorizontalPanel();
		upSpinner = new Image("minig/images/spinner_moz.gif");
		upSpinner.setVisible(false);
		eastPanel.add(upSpinner);
		eastPanel.add(droppAttachmentLink);
		dp.add(eastPanel, DockPanel.EAST);

		HorizontalPanel hp = new HorizontalPanel();
		dp.add(hp, DockPanel.CENTER);
		hp.clear();
		hp.add(new AttachmentDisplay(meta));
	}

	private void buildUpload(final DockPanel dp) {
		setEncoding(FormPanel.ENCODING_MULTIPART);
		setMethod(FormPanel.METHOD_POST);

		setAction(AjaxFactory.uploadAttachment(messageId));

		Label l = new Label();
		dp.add(l, DockPanel.WEST);
		dp.setCellVerticalAlignment(l, VerticalPanel.ALIGN_MIDDLE);
		upload = new FileUpload();

		dp.add(upload, DockPanel.CENTER);

		droppAttachmentLink = new Anchor(I18N.strings.delete());
		droppAttachmentLink.addClickHandler(createDropAttachmentClickListener());
		HorizontalPanel eastPanel = new HorizontalPanel();
		upSpinner = new Image("minig/images/spinner_moz.gif");
		upSpinner.setVisible(false);
		eastPanel.add(upSpinner);
		eastPanel.add(droppAttachmentLink);
		dp.add(eastPanel, DockPanel.EAST);

		addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				GWT.log("on submit " + attachementId, null);
				upSpinner.setVisible(true);
				droppAttachmentLink.setVisible(false);
				attachPanel.notifyUploadStarted(attachementId);
			}
		});

		addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				GWT.log("on submit complete " + attachementId, null);
				upSpinner.setVisible(false);
				droppAttachmentLink.setVisible(true);
				attachPanel.notifyUploadComplete(attachementId);

				HorizontalPanel hp = new HorizontalPanel();
				Label uploadInfo = new Label();
				uploadInfo.setText("File '" + attachementId + "' attached.");
				hp.add(uploadInfo);

                String results = event.getResults().trim();

                //TODO GWT must be replaced!!!!
                if(!results.startsWith("<pre>")) {
                    Window.alert("wrong response: " + results);
                    return;
                }

				String cut = results.substring(5, results.length() -6).trim();
				String replaceAll = cut.replaceAll("&gt;", ">").replaceAll("&lt;", "<");

                messageId = replaceAll;
				attachPanel.setMessageId(replaceAll);

				dp.remove(upload);
				dp.add(hp, DockPanel.CENTER);
				updateMetadata(hp);

			}
		});

		Timer t = new Timer() {
			public void run() {
				if (upload.getFilename() != null && upload.getFilename().length() > 0) {
					GWT.log("filename before upload: " + upload.getFilename(), null);
					upload.setName(upload.getFilename());
					attachementId = upload.getFilename();
					cancel();
					submit();
				}
			}
		};
		t.scheduleRepeating(300);
	}

	private void updateMetadata(final HorizontalPanel hp) {
		Ajax<IAttachmentMetadata> request = AjaxFactory.attachmentMetadata(messageId, attachementId);

		try {
			request.send(new AjaxCallback<IAttachmentMetadata>() {

				@Override
				public void onSuccess(IAttachmentMetadata meta) {
					upSpinner.setVisible(false);
					hp.clear();
					hp.add(new AttachmentDisplay(meta));
				}

				@Override
				public void onError(Request request, Throwable exception) {
					upSpinner.setVisible(false);

					if (exception != null) {
						WebmailController.get().getView().notifyUser(exception.getMessage());
					} else {
						WebmailController.get().getView().notifyUser("something goes wrong.");
					}
				}
			});
		} catch (RequestException e) {
			WebmailController.get().getView().notifyUser(e.getMessage());
		}
	}

	private ClickHandler createDropAttachmentClickListener() {
		final AttachmentUploadWidget auw = this;
		return new ClickHandler() {
			public void onClick(ClickEvent ev) {
				//attachPanel.getAttachList().remove(auw);
				attachPanel.dropAttachment(auw);
				//
				// if (attachPanel.getAttachList().getWidgetCount() == 1) {
				// attachPanel.reset();
				// } else {
				// attachPanel.getAttachList().remove(auw);
				// attachPanel.droppAnAttachment(auw.attachementId);
				// }
			}
		};
	}

    public String getAttachmentId() {
        return this.attachementId;
    }

}
