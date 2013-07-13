package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;

public class ComposerToolbarSwitcher {

	private DeckPanel tabPanel;
	private MinigRichTextArea mta;
	private boolean plainOnly;

	public ComposerToolbarSwitcher(MinigRichTextArea mta) {
		this.mta = mta;
		tabPanel = new DeckPanel();

		createPlainTab();
		createRichTab();
		tabPanel.setWidth("100%");
		tabPanel.setHeight("35px");
		tabPanel.showWidget(0);
		GWT.log("ComposerToolbarSwitcher created", null);
		plainOnly = true;
	}

	private void createPlainTab() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("panelActions");
		Anchor r = new Anchor(I18N.strings.richFormatting() + " »");
		r.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				switchToRich();
			}
		});
		hp.add(r);
		hp.setCellVerticalAlignment(r, HasVerticalAlignment.ALIGN_MIDDLE);
		tabPanel.add(hp);
	}

	private void createRichTab() {
		FlexTable ft = new FlexTable();
		RichTextToolbar rtt = mta.getRichTextToolbar();
		ft.setWidget(0, 0, rtt);
		ft.getCellFormatter().setWidth(0, 0, "100%");
		ft.addStyleName("panelActions");

		Anchor r = new Anchor(("« " + I18N.strings.plainText()).replace(" ",
				"&nbsp;"), true);
		r.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				switchToPlain();
			}
		});
		ft.setWidget(0, 1, r);
		ft.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		tabPanel.add(ft);
	}

	public Widget getWidget() {
		return tabPanel;
	}

	public void switchToPlain() {
		GWT.log("Switch to plain", null);
		tabPanel.showWidget(0);
	}

	public void switchToRich() {
		GWT.log("Switch to rich", null);
		plainOnly = false;
		tabPanel.showWidget(1);
	}

	public boolean isPlainOnly() {
		return plainOnly;
	}

	public void reset() {
		plainOnly = true;
	}

}
