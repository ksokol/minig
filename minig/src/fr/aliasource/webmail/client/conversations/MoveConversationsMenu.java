package fr.aliasource.webmail.client.conversations;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;

import fr.aliasource.webmail.client.IFolderClickHandlerFactory;
import fr.aliasource.webmail.client.LabelsPanel;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.composer.MenuButton;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

public class MoveConversationsMenu extends MenuButton {

	private boolean isMove;
	private Set<ConversationId> selectedIds;
	private ConversationListPanel clp;
	private String selectionString;
	private TailCall onSuccess;
	private LabelsPanel lp;

	public MoveConversationsMenu(String lbl, ConversationListPanel clp,
			boolean isMove, Position position) {
		this(lbl, clp, isMove, null, position);
	}

	public MoveConversationsMenu(String lbl, ConversationListPanel clp,
			boolean isMove, TailCall onSuccess, Position position) {
		super(lbl, position == Position.North ? PopupOrientation.DownRight: PopupOrientation.UpRight);
		this.isMove = isMove;
		this.clp = clp;
		setSelection(new HashSet<ConversationId>());
		this.onSuccess = onSuccess;

		createContent();
	}

	private void createContent() {
		FlexTable ft = new FlexTable();
		IFolderClickHandlerFactory chf = new IFolderClickHandlerFactory() {
			@Override
			public ClickHandler createHandler(final Folder f) {
				ClickHandler ch = new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						setDown(false);
						pp.hide();
						if (!selectedIds.isEmpty()) {
							clp.selectSome(selectedIds);
						} else if (selectionString != null) {
							// clp.moveConversation handles this case
							GWT
									.log("selectionString: " + selectionString,
											null);
						} else {
							GWT.log("no selection, returning", null);
						}
						clp.moveConversation(WebmailController.get()
								.getSelector().getCurrent(), f, isMove,
								onSuccess);
					}
				};
				return ch;
			}
		};
		lp = new LabelsPanel(false, false, false, chf);
		ft.setWidget(0, 0, lp);

		pp.add(ft);
	}

	public void setSelection(Set<ConversationId> selectedIds) {
		this.selectedIds = selectedIds;
		setEnabled(this.selectedIds != null && !this.selectedIds.isEmpty());
		GWT.log("selectedIds.size " + selectedIds.size(), null);
	}

	public void setSelection(String str) {
		this.selectionString = str;
		GWT.log("selectionString: " + selectionString, null);
		// TODO finish impl
	}

	public void destroy() {
		selectedIds.clear();
		lp.destroy();
		lp = null;
		clp = null;
	}

}
