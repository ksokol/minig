package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI
 * for all rich text formatting, dynamically displayed only for the available
 * functionality.
 */
public class RichTextToolbar extends Composite {

	public interface ImagesND extends ClientBundle {
		@Source("bold.gif")
		ImageResource bold();

		@Source("hr.gif")
		ImageResource hr();

		@Source("indent.gif")
		ImageResource indent();

		@Source("italic.gif")
		ImageResource italic();

		@Source("justifyCenter.gif")
		ImageResource justifyCenter();

		@Source("justifyLeft.gif")
		ImageResource justifyLeft();

		@Source("justifyRight.gif")
		ImageResource justifyRight();

		@Source("ol.gif")
		ImageResource ol();

		@Source("outdent.gif")
		ImageResource outdent();

		@Source("removeFormat.gif")
		ImageResource removeFormat();

		@Source("strikeThrough.gif")
		ImageResource strikeThrough();

		@Source("ul.gif")
		ImageResource ul();

		@Source("underline.gif")
		ImageResource underline();

		@Source("fonts.gif")
		ImageResource fonts();

		@Source("fontSizes.gif")
		ImageResource fontSizes();

		@Source("foreColors.gif")
		ImageResource foreColors();

		@Source("backColors.gif")
		ImageResource backColors();

	}

	// /**
	// * This {@link ImageBundle} is used for all the button icons. Using an
	// image
	// * bundle allows all of these images to be packed into a single image,
	// which
	// * saves a lot of HTTP requests, drastically improving startup time.
	// */
	// public interface Images extends ImageBundle {
	//
	// AbstractImagePrototype createLink();
	//
	// AbstractImagePrototype hr();
	//
	// AbstractImagePrototype indent();
	//
	// AbstractImagePrototype insertImage();
	//
	// AbstractImagePrototype italic();
	//
	// AbstractImagePrototype justifyCenter();
	//
	// AbstractImagePrototype justifyLeft();
	//
	// AbstractImagePrototype justifyRight();
	//
	// AbstractImagePrototype ol();
	//
	// AbstractImagePrototype outdent();
	//
	// AbstractImagePrototype removeFormat();
	//
	// AbstractImagePrototype removeLink();
	//
	// AbstractImagePrototype strikeThrough();
	//
	// AbstractImagePrototype ul();
	//
	// AbstractImagePrototype underline();
	//
	// AbstractImagePrototype fonts();
	//
	// AbstractImagePrototype fontSizes();
	//
	// AbstractImagePrototype foreColors();
	//
	// AbstractImagePrototype backColors();
	// }

	/**
	 * This {@link Constants} interface is used to make the toolbar's strings
	 * internationalizable.
	 */
	public interface Strings extends Constants {

		String black();

		String blue();

		String bold();

		String color();

		String textColor();

		String bgColor();

		String font();

		String green();

		String hr();

		String indent();

		String italic();

		String justifyCenter();

		String justifyLeft();

		String justifyRight();

		String large();

		String medium();

		String normal();

		String ol();

		String outdent();

		String red();

		String removeFormat();

		String size();

		String small();

		String strikeThrough();

		String subscript();

		String superscript();

		String ul();

		String underline();

		String white();

		String xlarge();

		String xsmall();

		String xxlarge();

		String xxsmall();

		String yellow();
	}

	/**
	 * We use an inner EventListener class to avoid exposing event methods on
	 * the RichTextToolbar itself.
	 */
	private class EventListener implements ClickHandler, KeyUpHandler {

		@Override
		public void onClick(ClickEvent ev) {
			Widget sender = (Widget) ev.getSource();
			if (sender == bold) {
				formatter.toggleBold();
			} else if (sender == italic) {
				formatter.toggleItalic();
			} else if (sender == underline) {
				formatter.toggleUnderline();
			} else if (sender == strikethrough) {
				formatter.toggleStrikethrough();
			} else if (sender == indent) {
				formatter.rightIndent();
			} else if (sender == outdent) {
				formatter.leftIndent();
			} else if (sender == justifyLeft) {
				formatter.setJustification(RichTextArea.Justification.LEFT);
			} else if (sender == justifyCenter) {
				formatter.setJustification(RichTextArea.Justification.CENTER);
			} else if (sender == justifyRight) {
				formatter.setJustification(RichTextArea.Justification.RIGHT);
			} else if (sender == hr) {
				formatter.insertHorizontalRule();
			} else if (sender == ol) {
				formatter.insertOrderedList();
			} else if (sender == ul) {
				formatter.insertUnorderedList();
			} else if (sender == removeFormat) {
				formatter.removeFormat();
			} else if (sender == richText) {
				// We use the RichTextArea's onKeyUp event to update the toolbar
				// status.
				// This will catch any cases where the user moves the cursur
				// using the
				// keyboard, or uses one of the browser's built-in keyboard
				// shortcuts.
				updateStatus();
			}
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			Widget sender = (Widget) event.getSource();
			if (sender == richText) {
				// We use the RichTextArea's onKeyUp event to update the toolbar
				// status.
				// This will catch any cases where the user moves the cursur
				// using the
				// keyboard, or uses one of the browser's built-in keyboard
				// shortcuts.
				updateStatus();
			}
		}
	}

	public static final Strings strings = GWT.create(Strings.class);
	// private Images images = GWT.create(Images.class);
	private ImagesND images = GWT.create(ImagesND.class);
	private EventListener listener = new EventListener();

	private RichTextArea richText;

	private VerticalPanel outer = new VerticalPanel();
	private HorizontalPanel topPanel = new HorizontalPanel();
	private ToggleButton bold;
	private ToggleButton italic;
	private ToggleButton underline;
	private ToggleButton strikethrough;
	private PushButton indent;
	private PushButton outdent;
	private PushButton justifyLeft;
	private PushButton justifyCenter;
	private PushButton justifyRight;
	private PushButton hr;
	private PushButton ol;
	private PushButton ul;
	private PushButton removeFormat;
	private Formatter formatter;

	/**
	 * Creates a new toolbar that drives the given rich text area.
	 * 
	 * @param richText
	 *            the rich text area to be controlled
	 */
	public RichTextToolbar(RichTextArea richText) {
		this.richText = richText;
		this.formatter = richText.getFormatter();

		outer.add(topPanel);
		topPanel.setWidth("100%");

		initWidget(outer);

		topPanel.add(bold = createToggleButton(images.bold(), strings.bold()));
		topPanel.add(italic = createToggleButton(images.italic(), strings
				.italic()));
		topPanel.add(underline = createToggleButton(images.underline(), strings
				.underline()));

		topPanel.add(new HTML("&nbsp;"));

		FontFamilyButton fontFamilyButton = new FontFamilyButton(formatter,
				images.fonts(), strings.font());
		topPanel.add(fontFamilyButton);
		FontSizeButton fontSizeButton = new FontSizeButton(formatter, images
				.fontSizes(), strings.size());
		topPanel.add(fontSizeButton);

		FontColorButton fontTextColorButton = new FontColorButton(formatter,
				images.foreColors(), strings.textColor(), new String[] {
						"black", "#dbebff", "#ffbbbb", "#bdf4cb", "#f5f7c4",
						"#dfb0ff", "#ffd7b3", "#cfe7e2", }, true);
		topPanel.add(fontTextColorButton);

		FontColorButton fontBackColorButton = new FontColorButton(formatter,
				images.backColors(), strings.bgColor(), new String[] {
						"#eeeeee", "#dbebff", "#ffbbbb", "#bdf4cb", "#f5f7c4",
						"#dfb0ff", "#ffd7b3", "#cfe7e2", }, false);
		topPanel.add(fontBackColorButton);

		topPanel.add(new HTML("&nbsp;"));

		topPanel.add(justifyLeft = createPushButton(images.justifyLeft(),
				strings.justifyLeft()));
		topPanel.add(justifyCenter = createPushButton(images.justifyCenter(),
				strings.justifyCenter()));
		topPanel.add(justifyRight = createPushButton(images.justifyRight(),
				strings.justifyRight()));

		topPanel.add(strikethrough = createToggleButton(images.strikeThrough(),
				strings.strikeThrough()));
		topPanel.add(indent = createPushButton(images.indent(), strings
				.indent()));
		topPanel.add(outdent = createPushButton(images.outdent(), strings
				.outdent()));
		topPanel.add(hr = createPushButton(images.hr(), strings.hr()));
		topPanel.add(ol = createPushButton(images.ol(), strings.ol()));
		topPanel.add(ul = createPushButton(images.ul(), strings.ul()));
		topPanel.add(removeFormat = createPushButton(images.removeFormat(),
				strings.removeFormat()));

		// richText.addKeyUpHandler(listener);
		// richText.addClickHandler(listener);

		for (int i = 0; i < topPanel.getWidgetCount(); i++) {
			topPanel.setCellVerticalAlignment(topPanel.getWidget(i),
					HasVerticalAlignment.ALIGN_MIDDLE);
		}
	}

	private PushButton createPushButton(ImageResource img, String tip) {
		PushButton pb = new PushButton(new Image(img));
		pb.addClickHandler(listener);
		pb.setTitle(tip);
		return pb;
	}

	private ToggleButton createToggleButton(ImageResource img, String tip) {
		ToggleButton tb = new ToggleButton(new Image(img));
		tb.addClickHandler(listener);
		tb.setTitle(tip);
		return tb;
	}

	/**
	 * Updates the status of all the stateful buttons.
	 */
	private void updateStatus() {
		bold.setDown(formatter.isBold());
		italic.setDown(formatter.isItalic());
		underline.setDown(formatter.isUnderlined());

		strikethrough.setDown(formatter.isStrikethrough());
	}
}
