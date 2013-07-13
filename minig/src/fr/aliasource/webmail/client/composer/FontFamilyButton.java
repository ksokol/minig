package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class FontFamilyButton extends MenuButton {

	public static final String[] FONTS = { "Times New Roman", "Arial",
			"Verdana", "Courier New" };

	private int selected;
	private Formatter basic;

	public FontFamilyButton(Formatter bf, ImageResource img, String tooltip) {
		super(new Image(img));
		setTitle(tooltip);
		this.basic = bf;
		selected = 0;

		final FlexTable ft = new FlexTable();
		for (int i = 0; i < FONTS.length; i++) {
			ft.setStyleName("composerPopup");
			HTML cross = new HTML(i == selected ? "<b>X</b>" : "&nbsp;");
			ft.setWidget(i, 0, cross);
			HTML font = new HTML("<span style=\"font-family: " + FONTS[i]
					+ "; font-size: 1.4em;\">" + FONTS[i] + "</span>");
			ft.setWidget(i, 1, font);

			final int fontIdx = i;
			ClickHandler ch = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setDown(false);
					pp.hide();
					GWT.log("Font selected: " + FONTS[fontIdx], null);
					HTML oldSel = (HTML) ft.getWidget(selected, 0);
					oldSel.setHTML("&nbsp;");
					selected = fontIdx;
					HTML newSel = (HTML) ft.getWidget(selected, 0);
					newSel.setHTML("<b>X</b>");
					basic.setFontName(FONTS[fontIdx]);
				}
			};
			cross.addClickHandler(ch);
			font.addClickHandler(ch);
		}
		pp.add(ft);
	}

}
