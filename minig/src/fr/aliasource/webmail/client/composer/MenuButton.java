package fr.aliasource.webmail.client.composer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;

public abstract class MenuButton extends ToggleButton {

	public enum PopupOrientation {
		UpRight,
		UpLeft,
		DownRight,
		DownLeft
	}
	
	protected PopupPanel pp;
	protected boolean mouseInPopup;
	private final PopupOrientation popupOrientation;
	
	public MenuButton(Image createImage) {
		this(createImage, PopupOrientation.DownRight);
	}

	public MenuButton(String text, PopupOrientation popupOrientation) {
		super(text);
		this.popupOrientation = popupOrientation;
		addStyleName("dropDownArrowButton");
		addStyleName("noWrap");
		createPopup();
	}

	public MenuButton(Image createImage, final PopupOrientation popupOrientation) {
		super(createImage);
		this.popupOrientation = popupOrientation;
		createPopup();
	}

	private void createPopup() {
		pp = new PopupPanel() {
			@Override
			public void onBrowserEvent(Event event) {
				Element related = event.getRelatedEventTarget().cast();
				switch (DOM.eventGetType(event)) {
				case Event.ONMOUSEOVER:
					if (related != null && getElement().isOrHasChild(related)) {
						return;
					}
					GWT.log("onMouseOver", null);
					mouseInPopup = true;
					break;
				case Event.ONMOUSEOUT:
					if (related != null && getElement().isOrHasChild(related)) {
						return;
					}
					GWT.log("onMouseOut", null);
					mouseInPopup = false;
					break;
				}
				DomEvent.fireNativeEvent(event, this, this.getElement());
			}
		};
		pp.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER);

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (pp.isShowing()) {
					pp.hide();
					setDown(false);
				} else {
					pp.show();
					Element re = event.getRelativeElement();
					pp.setPopupPosition(computeXPosition(re), computeYPosition(re));
				}
			}
		});

		addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				GWT.log("onBlur: inPopup" + mouseInPopup + " jsShowing: "
						+ pp.isShowing(), null);
				if (pp.isShowing() && !mouseInPopup) {
					pp.hide();
					setDown(false);
				}
			}
		});
	}

	private int computeXPosition(Element reference) {
		switch (popupOrientation) {
		case DownLeft:
		case UpLeft:
			return reference.getAbsoluteLeft()
			- pp.getElement().getOffsetWidth()
			+ reference.getOffsetWidth();
		case DownRight:
		case UpRight:
			return reference.getAbsoluteLeft();
		}
		throw new RuntimeException("unexpected value");
	}
	
	private int computeYPosition(Element reference) {
		switch (popupOrientation) {
		case UpLeft:
		case UpRight:
			return reference.getAbsoluteTop() - pp.getElement().getOffsetHeight();
		case DownLeft:
		case DownRight:
			return reference.getAbsoluteTop() + reference.getOffsetHeight();
		}
		throw new RuntimeException("unexpected value");
	}
}
