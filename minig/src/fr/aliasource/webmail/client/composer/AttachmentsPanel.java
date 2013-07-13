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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.rpc.AttachementsManagerAsync;

public class AttachmentsPanel extends VerticalPanel {

	private VerticalPanel attachPanel;
	private VerticalPanel attachList;
	private AttachementsManagerAsync manager;
	private ArrayList<String> managedIds;
	private ArrayList<IUploadListener> uploadListeners;
	private Anchor attach;

	public AttachmentsPanel() {
		this.manager = AjaxCall.atMgr;
		this.managedIds = new ArrayList<String>();
		this.uploadListeners = new ArrayList<IUploadListener>();

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
				uploadAnotherFile();
			}
		});
		hp.add(l);
		hp.add(attach);
		attachPanel.add(attachList);
		attachPanel.add(hp);
		attachPanel.setStyleName("enveloppeField");
		add(attachPanel);
	}

	public void uploadAnotherFile() {
		requestAttachementId();
	}

	private void requestAttachementId() {
		AsyncCallback<String> ac = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				GWT.log("Error allocation attachement id", caught);
			}

			public void onSuccess(String attachId) {
				attachIdReceived(attachId, false);
			}
		};
		manager.allocateAttachementId(ac);
	}

	private AttachmentUploadWidget attachIdReceived(String attachId,
			boolean alreadyOnServer) {
		GWT.log("Attachement id received: " + attachId, null);
		AttachmentUploadWidget uw = newFileUpload(attachId, alreadyOnServer);
		attachList.add(uw);
		attach.setText(I18N.strings.attachAnotherFile());
		return uw;
	}

	private AttachmentUploadWidget newFileUpload(String attachId,
			boolean alreadyOnServer) {
		return new AttachmentUploadWidget(this, attachId, alreadyOnServer);
	}

	public String[] getAttachementIds() {
		return (String[]) managedIds.toArray(new String[managedIds.size()]);
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
		manager.dropAttachement(getAttachementIds(), droppAttachmentCallback());
		managedIds.clear();
		createAttachHyperlink();
	}

	public void droppAnAttachment(String attachmentId) {
		String[] id = new String[1];
		id[0] = attachmentId;
		managedIds.remove(attachmentId);
		manager.dropAttachement(id, droppAttachmentCallback());
	}

	AttachementsManagerAsync getManager() {
		return manager;
	}

	public void showAttach(String attachId) {
		attachIdReceived(attachId, true);
	}

	private AsyncCallback<Void> droppAttachmentCallback() {
		return new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				GWT.log("Error dropping attachements", caught);
			}

			public void onSuccess(Void result) {
				GWT.log("Attachements dropped", null);
			}
		};
	}

	public VerticalPanel getAttachList() {
		return attachList;
	}

}
