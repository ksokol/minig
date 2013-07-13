package fr.aliasource.webmail.client.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.Presence.Type;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class JabberRoster extends FlexTable {

	private TextBox filterField;
	private FlexTable main;

	private Map<Friend, FriendPresenter> friends;
	private List<Friend> sortList;

	public JabberRoster(IJabberListener jl) {
		friends = new HashMap<Friend, FriendPresenter>();
		sortList = new LinkedList<Friend>();
		createUI();

		main.setCellPadding(0);
		main.setCellSpacing(0);
	}

	private void createUI() {
		addStyleName("whiteBackground");
		setWidth("100%");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName("labelsPanel");
		filterField = new TextBox();
		filterField.setWidth("100%");
		createFilterHandlers();
		hp.add(filterField);
		hp.setCellWidth(filterField, "100%");

		class ClearButton extends CustomButton {
			public ClearButton() {
				super(new Image("minig/images/x.gif"));
				DOM.setStyleAttribute(getElement(), "padding", "2px");
			}
		}

		ClearButton clear = new ClearButton();
		clear.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				filterField.setText("");
				applyFilter();
			}
		});
		hp.add(clear);
		hp.setCellVerticalAlignment(clear, HasVerticalAlignment.ALIGN_MIDDLE);
		try {
			DOM.setStyleAttribute(hp.getElement(), "tableLayout", "auto");
		} catch (Throwable t) {
			GWT.log("tableLayout inherit fails on ie :/", null);
		}

		setWidget(0, 0, hp);
		getFlexCellFormatter().setColSpan(0, 0, 2);

		main = new FlexTable();
		ScrollPanel sp = new ScrollPanel(main);
		sp.setHeight("100px");
		DOM.setStyleAttribute(sp.getElement(), "overflowY", "scroll");
		DOM.setStyleAttribute(sp.getElement(), "overflowX", "hidden");
		setWidget(1, 0, sp);
		getFlexCellFormatter().setColSpan(1, 0, 2);

		HorizontalPanel bottomActions = new HorizontalPanel();
		bottomActions.setStyleName("bottomActions");

		// breaks IE
		try {
			DOM.setStyleAttribute(bottomActions.getElement(), "tableLayout",
					"auto");
		} catch (Throwable t) {
			GWT.log("fails on ie", null);
		}
		setWidget(2, 0, bottomActions);
		getFlexCellFormatter().setColSpan(2, 0, 2);
	}

	private void applyFilter() {
		String f = filterField.getText();
		if (f == null || f.trim().length() == 0) {
			// clear filter
			for (FriendPresenter fp : friends.values()) {
				fp.setVisible(true);
			}
		} else {
			for (Friend fp : friends.keySet()) {
				FriendPresenter presenter = friends.get(fp);
				if (!fp.getJabberId().startsWith(f)) {
					presenter.setVisible(false);
				} else {
					presenter.setVisible(true);
				}
			}
		}
	}

	private void createFilterHandlers() {
		class FilterFieldHandlers extends HandlesAllKeyEvents implements
				ValueChangeHandler<String> {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				applyFilter();
			}

			@Override
			public void onKeyDown(KeyDownEvent event) {
				applyFilter();
			}

			@Override
			public void onKeyUp(KeyUpEvent event) {
				applyFilter();
			}

			@Override
			public void onKeyPress(KeyPressEvent event) {
				applyFilter();
			}

		}
		;
		FilterFieldHandlers ffh = new FilterFieldHandlers();
		ffh.addKeyHandlersTo(filterField);
		filterField.addValueChangeHandler(ffh);
	}

	public void onPresenceChange(Presence p) {
		GWT.log("onPresenceChange: from: " + p.getFromAsString() + " status: "
				+ p.getShow().toString() + " t: " + p.getType(), null);
		Friend f = new Friend(p.getFromAsString(), p.getShow());
		updateFriend(f, p.getType());
	}

	private void updateFriend(Friend f, Type t) {
		FriendPresenter presenter = friends.get(f);
		if (presenter == null) {
			// add
			FriendPresenter fp = new FriendPresenter(f);
			sortList.add(f);
			Collections.sort(sortList);
			int idx = sortList.indexOf(f);
			main.insertRow(idx);
			main.setWidget(idx, 0, fp);
			fp.setWidth("100%");
			friends.put(f, fp);
		} else if (t == Type.unavailable) {
			int idx = sortList.indexOf(f);
			sortList.remove(idx);
			presenter.destroy();
			friends.remove(f);
			main.removeRow(idx);
		} else {
			GWT.log("Should update " + f.getJabberId(), null);
			// update
			presenter.update(f);
		}
	}

}
