package fr.aliasource.webmail.client.chat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;

public class FriendPresenter extends FlexTable {

	private Friend f;
	private Label name;
	private Label status;
	private Image icon;
	private HandlerRegistration reg;

	private static final PresenceIcons icons = GWT.create(PresenceIcons.class);

	public FriendPresenter(Friend fr) {
		addStyleName("friendPresenter");
		this.f = fr;
		name = new Label();
		status = new Label();
		status.addStyleName("statusText");
		icon = new Image();

		setWidget(0, 0, icon);
		setWidget(0, 1, name);
		setWidget(1, 0, status);

		getFlexCellFormatter().setRowSpan(0, 0, 2);
		getFlexCellFormatter().setWidth(0, 0, "18px");

		update(f);

		this.reg = addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				XmppController.get().getView().onConversationStart(
						f.getJabberId());
			}
		});

		setCellPadding(0);
		setCellSpacing(0);

		DOM.setStyleAttribute(getElement(), "padding", "3px");
		DOM.setStyleAttribute(getElement(), "borderBottom",
				"1px dotted lightgray");
	}

	public void update(Friend friend) {
		this.f = friend;

		name.setText(f.getJabberId());

		if (f.getJabberId().equals("sylvaing@aliasource.fr")) {
			status.setText(I18N.strings.jabberSylvain());
			icon.setUrl(icons.sylvain().getURL());
			return;
		}

		switch (f.getStatus()) {
		case away:
			status.setText(I18N.strings.jabberAway());
			icon.setUrl(icons.away().getURL());
			break;
		case chat:
			status.setText(I18N.strings.jabberChat());
			icon.setUrl(icons.chat().getURL());
			break;
		case dnd:
			status.setText(I18N.strings.jabberDnd());
			icon.setUrl(icons.dnd().getURL());
			break;
		case notSpecified:
			status.setText(I18N.strings.jabberOnline());
			icon.setUrl(icons.online().getURL());
			break;
		case unknown:
			status.setText(I18N.strings.jabberUnknown());
			icon.setUrl(icons.unknown().getURL());
			break;
		case xa:
			status.setText(I18N.strings.jabberXa());
			icon.setUrl(icons.xa().getURL());
			break;
		}
	}

	public void destroy() {
		reg.removeHandler();
	}

}
