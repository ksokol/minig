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
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import fr.aliasource.webmail.client.conversations.UndoMoveWidget;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationContent;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

public class ConversationActions extends HorizontalPanel {

	private boolean expanded;
	private Anchor expandCollapse;
	private Anchor printAll;
	private Anchor exportAll;
	// private ConversationMoreActions cma;
	private ConversationDisplay cd;
	private Anchor prev;
	private Anchor next;
	private MoveConversationsMenu mcm;
	private MoveConversationsMenu ccm;
	private MoreActionMenu mam;
	private TailCall moveSuccessAction;
	private HandlerRegistration delReg;

	public ConversationActions(final ConversationDisplay cd, final View ui,
			ConversationListPanel listPanel, final int page, Position position) {
		this.cd = cd;
		Folder sel = WebmailController.get().getSelector().getCurrent();
		final String selName = sel.getName();
		String displayName = ui.displayName(WebmailController.get()
				.getSelector().getCurrent());
		Anchor back = new Anchor(
				("« " + I18N.strings.backTo() + " " + displayName).replace(" ",
						"&nbsp;"), true);
		back.addStyleName("noWrap");
		add(back);
		back.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				GWT.log("************ back to page " + page, null);
				ui.fetchConversations(WebmailController.get().getSelector()
						.getCurrent().getName(), page);
			}
		});

		moveSuccessAction = new TailCall() {
			@Override
			public void run() {
				GWT.log("running move success tailcall !", null);
				ui.fetchConversations(WebmailController.get().getSelector()
						.getCurrent().getName(), page);
			}
		};

		createDeleteButton(ui, page, selName);
		createMoveCopy(listPanel, page, position);
		createMoreActions(listPanel, page, position);

		createExpandLink(cd);
		createPrintAllLink(cd);
		createExportAllLink(cd);

		HTML spacer = new HTML("&nbsp;");
		spacer.setWidth("100%");
		add(spacer);
		setCellWidth(spacer, "100%");

		createNextPrevious();

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

		mcm = new MoveConversationsMenu(I18N.strings.moveTo(), listPanel, true,
				moveSuccessAction, position);
		hp.add(mcm);

		ccm = new MoveConversationsMenu(I18N.strings.copyTo(), listPanel,
				false, moveSuccessAction, position);
		hp.add(ccm);

		add(hp);
	}

	private void createNextPrevious() {
		prev = new Anchor(I18N.strings.previousConversation().replace(" ",
				"&nbsp;"), true);
		add(prev);
		prev.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConversation(cd.getConversationContent().getConversation()
						.getPrev());
			}
		});
		next = new Anchor(I18N.strings.nextConversation()
				.replace(" ", "&nbsp;"), true);
		add(next);
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConversation(cd.getConversationContent().getConversation()
						.getNext());
			}
		});
	}

	private void createDeleteButton(final View ui, final int page,
			final String selName) {
		Button delete = new Button();

		String trash = WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER);
		String spam = WebmailController.get().getSetting(
				GetSettings.SPAM_FOLDER);
		if (selName.equals(trash) || selName.equals(spam)) {
			delete.setText(I18N.strings.deleteForever());
		} else {
			delete.setText(I18N.strings.delete());
		}

		delete.addStyleName("deleteButton");
		delete.addStyleName("noWrap");
		add(delete);
		delReg = delete.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ConversationContent cc = cd.getConversationContent();
				ConversationId id = cc.getConversation().getId();
				delete(Arrays.asList(id));
			}

			private void delete(List<ConversationId> convIds) {
				String trash = WebmailController.get().getSetting(
						GetSettings.TRASH_FOLDER);
				String spam = WebmailController.get().getSetting(
						GetSettings.SPAM_FOLDER);
				if (trash.equals(selName) || spam.equals(selName)) {
					AsyncCallback<Void> ac = new AsyncCallback<Void>() {
						public void onFailure(Throwable caught) {
							ui.log("Failed to delete conversation.");
						}

						public void onSuccess(Void result) {
							ui.notifyUser(I18N.strings
									.conversationDeleted(I18N.strings.trash()));
							ui.getSpinner().stopSpinning();
							ui.fetchConversations(selName, page);
						}
					};
					ui.getSpinner().startSpinning();
					AjaxCall.store.deleteConversation(convIds, ac);
				} else {
					AsyncCallback<List<ConversationId>> ac = new AsyncCallback<List<ConversationId>>() {
						public void onFailure(Throwable caught) {
							ui.log("Error trashing conversations", caught);
							ui.getSpinner().stopSpinning();
						}

						public void onSuccess(List<ConversationId> newIds) {
							ui.notifyUser(new UndoMoveWidget(ui, selName,
									newIds), 20);
							ui.fetchConversations(selName, page);
							ui.getSpinner().stopSpinning();
						}
					};
					ui.getSpinner().startSpinning();
					AjaxCall.store
							.trashConversation(new Folder[0], convIds, ac);
				}
			}
		});
	}

	private void createExpandLink(final ConversationDisplay cd) {
		expandCollapse = new Anchor(I18N.strings.expandAll().replace(" ",
				"&nbsp;"), true);
		add(expandCollapse);
		expanded = false;

		expandCollapse.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				expanded = !expanded;
				cd.setExpanded(expanded);
			}
		});
	}

	private void createPrintAllLink(final ConversationDisplay cd) {
		printAll = new Anchor(I18N.strings.printAll().replace(" ", "&nbsp;"),
				true);
		add(printAll);
		printAll.setTarget("_blank");

	}

	private void createExportAllLink(final ConversationDisplay cd) {
		exportAll = new Anchor(I18N.strings.exportAll().replace(" ", "&nbsp;"),
				true);
		add(exportAll);
		exportAll.setTarget("_blank");
	}

	public void updateLinks(boolean expanded) {
		this.expanded = expanded;
		if (expanded) {
			expandCollapse.setText(I18N.strings.collapseAll());
		} else {
			expandCollapse.setText(I18N.strings.expandAll());
		}
	}

	public void shutdown() {
		delReg.removeHandler();
		mcm.destroy();
		ccm.destroy();
		mam.destroy();
	}

	private void showConversation(ConversationId id) {
		GWT.log("should show conversation " + id, null);
		AsyncCallback<ConversationContent> ac = new AsyncCallback<ConversationContent>() {
			public void onFailure(Throwable caught) {
				GWT.log("failure", caught);
			}

			public void onSuccess(ConversationContent cc) {
				cd.setConversationContent(cc);
			}
		};
		AjaxCall.sca.show(id, ac);
	}

	public void notifyConvChanged() {
		Folder f = WebmailController.get().getSelector().getCurrent();
		final Conversation cur = cd.getConversationContent().getConversation();

		HashSet<ConversationId> sel = new HashSet<ConversationId>();
		sel.add(cur.getId());
		mam.setSelection(sel);
		ccm.setSelection(sel);
		mcm.setSelection(sel);
		GWT.log("setting selection to " + sel + " (id: " + cur.getId() + ")",
				null);

		String conversationId = URL.encode(cur.getId().getConversationId());
		String url = "export/conv/" + conversationId + ".html";
		printAll.setHref(url);

		url = "export/conv/" + URL.encode(conversationId) + ".pdf";
		exportAll.setHref(url);

		if (!f.isSearch()) {
			setPrevNextLinkStates();
		} else {
			AjaxCall.search.computePrevNext(f, cur.getId(),
					new AsyncCallback<Conversation>() {
						public void onFailure(Throwable caught) {
							GWT.log("computePrevNext failure", caught);
							cur.setPrev(null);
							cur.setNext(null);
							setPrevNextLinkStates();
						}

						public void onSuccess(Conversation result) {
							if (result != null) {
								cur.setPrev(result.getPrev());
								cur.setNext(result.getNext());
							} else {
								cur.setPrev(null);
								cur.setNext(null);
							}
							setPrevNextLinkStates();
						}
					});
		}
	}

	private void setPrevNextLinkStates() {
		if (cd.getConversationContent() != null
				&& cd.getConversationContent().getConversation() != null) {
			Conversation conv = cd.getConversationContent().getConversation();
			prev.setVisible(conv.getPrev() != null);
			next.setVisible(conv.getNext() != null);
		} else {
			prev.setVisible(false);
			next.setVisible(false);
		}
	}

}
