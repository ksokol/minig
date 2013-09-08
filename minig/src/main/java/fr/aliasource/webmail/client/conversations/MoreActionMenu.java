package fr.aliasource.webmail.client.conversations;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.composer.MenuButton;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.shared.ConversationId;

public class MoreActionMenu extends MenuButton {

	private Set<ConversationId> ids;
	private ConversationListPanel clp;
	private HandlerRegistration marReg;
	private HandlerRegistration mauReg;
	private HandlerRegistration staReg;
	private HandlerRegistration ustReg;

	public MoreActionMenu(String lbl, ConversationListPanel clp, Position position) {
		super(lbl, position == Position.North ? PopupOrientation.DownRight: PopupOrientation.UpRight);
		setSelection(new HashSet<ConversationId>());
		this.clp = clp;

		createContent();
	}

	private void createContent() {
		FlexTable ft = new FlexTable();

		int idx = 0;
		Anchor markAsRead = new Anchor(I18N.strings.markAsRead());
		ft.setWidget(idx++, 0, markAsRead);
		marReg = markAsRead.addClickHandler(createChangeFlag(SetFlags.READ,
				true));

		Anchor markAsUnread = new Anchor(I18N.strings.markAsUnread());
		ft.setWidget(idx++, 0, markAsUnread);
		mauReg = markAsUnread.addClickHandler(createChangeFlag(SetFlags.READ,
				false));

		Anchor star = new Anchor(I18N.strings.addStar());
		ft.setWidget(idx++, 0, star);
		staReg = star.addClickHandler(createChangeFlag(SetFlags.STAR, true));

		Anchor unstar = new Anchor(I18N.strings.removeStar());
		ft.setWidget(idx++, 0, unstar);
		ustReg = unstar.addClickHandler(createChangeFlag(SetFlags.STAR, false));

		pp.add(ft);
	}

	private ClickHandler createChangeFlag(final String flag, final boolean add) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDown(false);
				pp.hide();
				setFlag(clp, flag, add, createSetFlagCB());
			}
		};
	}

	private void setFlag(ConversationListPanel clp, String flag, boolean set,
			AsyncCallback<Void> ac) {
		View ui = WebmailController.get().getView();
		if (clp != null && clp.isAllSelected()) {
			if (ui.confirmFolderAction(clp.getCurrentData().cListFullLength,
					WebmailController.get().getSelector().getCurrent())) {
				String query = ui.getQuery();
				if (query == null) {
					query = "in:\""
							+ WebmailController.get().getSelector()
									.getCurrent().getName() + "\"";
				}
				GWT.log("query is " + query, null);
				AjaxCall.flags.setFlags(query, flag, set, ac);
			}
		} else {
			AjaxCall.flags.setFlags(ids, flag, set, ac);
		}
	}

	private AsyncCallback<Void> createSetFlagCB() {
		return new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				GWT.log("Error removing seen flags", caught);
			}

			public void onSuccess(Void result) {
				if (clp != null) {
					clp.selectNone();
					clp.showPage(clp.getCurrentPage());
				}
				WebmailController.get().getSelector().refreshUnreadCounts();
			}
		};
	}

	public void setSelection(Set<ConversationId> selectedIds) {
		this.ids = selectedIds;
		setEnabled(this.ids != null && !this.ids.isEmpty());
	}

	public void setSelection(String str) {
		GWT.log("setSelection(" + str + ")", null);
	}

	public void destroy() {
		marReg.removeHandler();
		mauReg.removeHandler();
		staReg.removeHandler();
		ustReg.removeHandler();
		ids = null;
		clp = null;
	}

}
