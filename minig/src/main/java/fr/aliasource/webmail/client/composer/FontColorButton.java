package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class FontColorButton extends MenuButton {

	private Formatter basic;

	public FontColorButton(Formatter bf, ImageResource img, String tooltip,
			final String[] COLORS, final boolean front) {
		super(new Image(img));
		setTitle(tooltip);
		this.basic = bf;

		final FlexTable ft = new FlexTable();
		for (int i = 0; i < COLORS.length; i++) {
			HTML font = new HTML(
					"<span style=\"cursor:pointer; border: 1px solid #ccc; background-color: "
							+ COLORS[i]
							+ "; font-size: 1em;\">&nbsp;&nbsp;&nbsp;&nbsp;</span>");
			ft.setWidget(i / 4, i % 4, font);

			final int fontIdx = i;
			ClickHandler ch = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setDown(false);
					pp.hide();
					GWT.log("Color selected: " + COLORS[fontIdx], null);
					if (front) {
						basic.setForeColor(COLORS[fontIdx]);
					} else {
						basic.setBackColor(COLORS[fontIdx]);
					}
				}
			};
			font.addClickHandler(ch);
		}
		pp.add(ft);
	}

}
