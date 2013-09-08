package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class FontSizeButton extends MenuButton {

	public static final FontSize[] SIZES = { FontSize.SMALL, FontSize.MEDIUM,
			FontSize.LARGE };
	public static final String[] EM_SIZES = { "1em", "1.4em", "1.8em" };
	public static final String[] LABELS = { RichTextToolbar.strings.small(),
			RichTextToolbar.strings.medium(), RichTextToolbar.strings.large(), };

	private int selected;
	private Formatter basic;

	public FontSizeButton(Formatter bf, ImageResource img, String tooltip) {
		super(new Image(img));
		setTitle(tooltip);
		this.basic = bf;
		selected = 1;

		final FlexTable ft = new FlexTable();
		for (int i = 0; i < SIZES.length; i++) {
			ft.setStyleName("composerPopup");
			HTML cross = new HTML(i == selected ? "<b>X</b>" : "&nbsp;");
			ft.setWidget(i, 0, cross);
			HTML font = new HTML("<span style=\"font-size: " + EM_SIZES[i]
					+ ";\">" + LABELS[i] + "</span>");
			ft.setWidget(i, 1, font);

			final int fontIdx = i;
			ClickHandler ch = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setDown(false);
					pp.hide();
					GWT.log("Font size selected: " + LABELS[fontIdx], null);
					HTML oldSel = (HTML) ft.getWidget(selected, 0);
					oldSel.setHTML("&nbsp;");
					selected = fontIdx;
					HTML newSel = (HTML) ft.getWidget(selected, 0);
					newSel.setHTML("<b>X</b>");
					basic.setFontSize(SIZES[fontIdx]);
				}
			};
			cross.addClickHandler(ch);
			font.addClickHandler(ch);
		}
		pp.add(ft);

	}
}
