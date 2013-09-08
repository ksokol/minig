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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Canned response widget
 * 
 * @author david
 * 
 */
public class CannedResponsePanel extends HorizontalPanel {

	private View ui;
	private PopupPanel pp;
	private VerticalPanel vp;
	private MailComposer mc;

	public CannedResponsePanel(final View ui, MailComposer mc) {
		this.ui = ui;
		this.mc = mc;
		pp = new PopupPanel(true);
		vp = new VerticalPanel();
		pp.add(vp);

		Anchor cannedResponse = new Anchor(I18N.strings.cannedResponses());
		cannedResponse.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {

				int left = ev.getRelativeElement().getAbsoluteLeft();
				int top = ev.getRelativeElement().getAbsoluteTop() + 15;
				pp.setPopupPosition(left, top);

				AjaxCall.listConversations.list(new Folder(WebmailController
						.get().getSetting(GetSettings.TEMPLATES_FOLDER)), 1,
						ConversationListPanel.PAGE_LENGTH,
						getListConvCallback());
				pp.show();
			}
		});

		add(new Label());
		add(cannedResponse);
	}

	private AsyncCallback<ConversationList> getListConvCallback() {
		vp.clear();
		return new AsyncCallback<ConversationList>() {
			public void onSuccess(ConversationList convs) {

				if (convs.getFullLength() == 0) {
					Label l = new Label(I18N.strings.noCannedResponses());
					vp.add(l);
				} else {
					Conversation[] c = convs.getData();
					for (Conversation conv : c) {
						String title = conv.getTitle();
						final ConversationId id = conv.getId();
						Anchor hl = new Anchor(title);
						hl.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent sender) {
								if (!mc.isEmpty()) {
									if (Window.confirm(I18N.strings
											.loadCannedResponse())) {
										loadCannedResponse(id);
									}
								} else {
									loadCannedResponse(id);
								}
								pp.hide();
							}
						});
						vp.add(hl);
					}
				}
			}

			public void onFailure(Throwable caught) {
				Label l = new Label(I18N.strings.noCannedResponses());
				vp.add(l);
			}
		};
	}

	private void loadCannedResponse(ConversationId id) {
		AsyncCallback<ConversationContent> ac = new AsyncCallback<ConversationContent>() {
			public void onFailure(Throwable caught) {
				ui.getSpinner().stopSpinning();
				ui.log("failure on loadCannedResponse");
			}

			public void onSuccess(ConversationContent cc) {
				ui.log("success on loadCannedResponse");
				ui.getSpinner().stopSpinning();
				ClientMessage cm = cc.getMessages()[cc.getMessages().length - 1];
				mc.loadDraft(cm, null);
			}
		};

		AjaxCall.sca.show(id, ac);
	}

}