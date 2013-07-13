package fr.aliasource.webmail.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class TartifletteWarning extends PopupPanel {

	private View view;

	public TartifletteWarning(View view) {
		this.view = view;
		FlexTable ft = new FlexTable();
		add(ft);

		HTML l = new HTML(
				"<span style=\"padding: 3px; color: white; background-color: red; font-weight: bold;\">"
						+ I18N.strings.tartifletteTitle() + "</span>");
		ft.setWidget(0, 0, l);

		Anchor reload = new Anchor(I18N.strings.reloadMinig());
		reload.setHref(getReloadUrl());

		ft.setWidget(0, 1, reload);

		HTML desc = new HTML(I18N.strings.tartifletteDescription());
		ft.setWidget(1, 0, desc);
		ft.getFlexCellFormatter().setColSpan(1, 0, 2);
	}

	private String getReloadUrl() {
		String url = Window.Location.getHref();
		String qs = Window.Location.getQueryString();
		url = url.replace(qs, "");
		GWT.log("reload url: " + url, null);
		return url;
	}

	public void showWarning() {
		int x = view.getAbsoluteLeft() + 10;
		int y = view.getAbsoluteTop() + 10;
		setPopupPosition(x, y);
		show();
	}

}
