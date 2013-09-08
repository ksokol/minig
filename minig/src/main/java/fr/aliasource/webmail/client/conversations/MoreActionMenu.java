package fr.aliasource.webmail.client.conversations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.composer.MenuButton;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.ctrl.MinigEventBus;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

public class MoreActionMenu extends MenuButton {

	private static final int READ = 1;
	private static final int STAR = 2;

	private Set<String> ids;
	private ConversationListPanel clp;
	private HandlerRegistration marReg;
	private HandlerRegistration mauReg;
	private HandlerRegistration staReg;
	private HandlerRegistration ustReg;

	public MoreActionMenu(String lbl, ConversationListPanel clp, Position position) {
		super(lbl, position == Position.North ? PopupOrientation.DownRight : PopupOrientation.UpRight);
		setSelection(new HashSet<String>());
		this.clp = clp;

		createContent();
	}

	private void createContent() {
		FlexTable ft = new FlexTable();

		int idx = 0;
		Anchor markAsRead = new Anchor(I18N.strings.markAsRead());
		ft.setWidget(idx++, 0, markAsRead);
		marReg = markAsRead.addClickHandler(createChangeFlag(READ, true));

		Anchor markAsUnread = new Anchor(I18N.strings.markAsUnread());
		ft.setWidget(idx++, 0, markAsUnread);
		mauReg = markAsUnread.addClickHandler(createChangeFlag(READ, false));

		Anchor star = new Anchor(I18N.strings.addStar());
		ft.setWidget(idx++, 0, star);
		staReg = star.addClickHandler(createChangeFlag(STAR, true));

		Anchor unstar = new Anchor(I18N.strings.removeStar());
		ft.setWidget(idx++, 0, unstar);
		ustReg = unstar.addClickHandler(createChangeFlag(STAR, false));

		pp.add(ft);
	}

	private ClickHandler createChangeFlag(final int flag, final boolean add) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDown(false);
				pp.hide();

				setFlag(clp, flag, add, createSetFlagCB());
			}
		};
	}

	private void setFlag(final ConversationListPanel clp, int flag, final boolean set, AsyncCallback<Void> ac) {
		View ui = WebmailController.get().getView();
		ui.getSpinner().startSpinning();

		List<IClientMessage> l = new ArrayList<IClientMessage>();

		for (String id : ids) {
			IClientMessage m = BeanFactory.instance.clientMessage().as();
			m.setId(id);

			switch (flag) {
			case READ:
				m.setRead(set);
				break;
			case STAR:
				m.setStarred(set);
				break;
			default:
				break;
			}

			l.add(m);
		}

		Ajax<IClientMessageList> ajax = AjaxFactory.updateMessagesFlags();

		IClientMessageList ml = BeanFactory.instance.clientMessageList().as();
		ml.setMailList(l);

		try {
			ajax.send(ml, new AjaxCallback<IClientMessageList>() {

				@Override
				public void onSuccess(IClientMessageList object) {
					if (clp != null) {
						clp.selectNone();
						clp.showPage(clp.getCurrentPage());

						StarredChangedEvent event = new StarredChangedEvent(ids, set);
						MinigEventBus.getEventBus().fireEvent(event);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
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
			}
		};
	}

	public void setSelection(Set<String> selectedIds) {
		this.ids = selectedIds;
		setEnabled(this.ids != null && !this.ids.isEmpty());
	}

	public void setSelection(String str) {
		this.ids.add(str);
		setEnabled(true);
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
