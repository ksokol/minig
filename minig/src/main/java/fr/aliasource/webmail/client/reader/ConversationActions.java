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

package fr.aliasource.webmail.client.reader;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.conversations.MoreActionMenu;
import fr.aliasource.webmail.client.conversations.MoveConversationsMenu;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IDeleteMessagesRequest;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

public class ConversationActions extends HorizontalPanel {

	private ConversationDisplay cd;
	private MoveConversationsMenu mcm;
	private MoveConversationsMenu ccm;
	private MoreActionMenu mam;
	private TailCall moveSuccessAction;
	private HandlerRegistration delReg;

	public ConversationActions(final ConversationDisplay cd, final View ui, ConversationListPanel listPanel, final int page,
			Position position) {
		this.cd = cd;
		String sel = WebmailController.get().getSelector().getCurrent().getId();
		final String selName = sel;
		String displayName = ui.displayName(WebmailController.get().getSelector().getCurrent());
		Anchor back = new Anchor(("« " + I18N.strings.backTo() + " " + displayName).replace(" ", "&nbsp;"), true);
		back.addStyleName("noWrap");
		add(back);
		back.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.fetchMessages(WebmailController.get().getSelector().getCurrent().getId(), page);
			}
		});

		moveSuccessAction = new TailCall() {
			@Override
			public void run() {
				ui.fetchMessages(WebmailController.get().getSelector().getCurrent().getId(), page);
			}
		};

		createDeleteButton(ui, page, selName);
		createMoveCopy(listPanel, page, position);
		createMoreActions(listPanel, page, position);

		HTML spacer = new HTML("&nbsp;");
		spacer.setWidth("100%");
		add(spacer);
		setCellWidth(spacer, "100%");

		for (Widget w : getChildren()) {
			setCellVerticalAlignment(w, HorizontalPanel.ALIGN_MIDDLE);
		}

		setSpacing(4);
		setWidth("100%");
		addStyleName("panelActions");
	}

	private void createMoreActions(ConversationListPanel listPanel, int page, Position position) {
		mam = new MoreActionMenu(I18N.strings.moreActions(), listPanel, position);
		add(mam);
	}

	private void createMoveCopy(ConversationListPanel listPanel, int page, Position position) {
		HorizontalPanel hp = new HorizontalPanel();

		mcm = new MoveConversationsMenu(I18N.strings.moveTo(), listPanel, true, moveSuccessAction, position);
		hp.add(mcm);

		ccm = new MoveConversationsMenu(I18N.strings.copyTo(), listPanel, false, moveSuccessAction, position);
		hp.add(ccm);

		add(hp);
	}

	public void enable() {
		mcm.setSelection(cd.getConversationContent().getId());
		ccm.setSelection(cd.getConversationContent().getId());
		mam.setSelection(cd.getConversationContent().getId());
	}

	private void createDeleteButton(final View ui, final int page, final String selName) {
		Button delete = new Button();
		delete.setText(I18N.strings.delete());

		delete.addStyleName("deleteButton");
		delete.addStyleName("noWrap");
		add(delete);

		delReg = delete.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				IClientMessage conversationContent = cd.getConversationContent();

				Ajax<IDeleteMessagesRequest> ajax = AjaxFactory.deleteMessages();
				IDeleteMessagesRequest request = BeanFactory.instance.deleteMessageRequest().as();

				request.setMessageIdList(Arrays.asList(conversationContent.getId()));
				ui.getSpinner().startSpinning();

				try {
					ajax.send(request, new AjaxCallback<IDeleteMessagesRequest>() {

						@Override
						public void onSuccess(IDeleteMessagesRequest object) {
							ui.getSpinner().stopSpinning();
							ui.notifyUser("Message deleted");
							ui.fetchMessages(selName, page);
						}

						@Override
						public void onError(Request request, Throwable exception) {
							ui.getSpinner().stopSpinning();

							if (exception != null) {
								ui.notifyUser(exception.getMessage());
							} else {
								ui.notifyUser("Error deleting message");
							}
						}
					});
				} catch (RequestException e) {
					ui.notifyUser(e.getMessage());
				}
			}
		});
	}

	public void shutdown() {
		delReg.removeHandler();
		mcm.destroy();
		ccm.destroy();
		mam.destroy();
	}

}
