package fr.aliasource.webmail.client.obm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

import fr.aliasource.webmail.client.reader.ReaderImages;

public class SectionsMenu extends Image {

	protected PopupPanel pp;
	protected boolean mouseInPopup;

	public SectionsMenu() {
		setUrl(ReaderImages.imgs.dropDown().getURL());
		setTitle("Sélectionner un module...");

		createPopup(true);

		try {
			ObmRequests.fetchSectionsHtml(new RequestCallback() {
				@Override
				public void onResponseReceived(Request arg0, Response arg1) {
					String html = arg1.getText();
					pp.add(new HTML(html));
				}

				@Override
				public void onError(Request arg0, Throwable arg1) {
					GWT.log("Error fetching html", arg1);
					pp.clear();
					pp.add(new HTML("<b>Unable to fetch sections data</b>"));
				}
			});
		} catch (RequestException e) {
			GWT.log("error", e);
			pp.clear();
			pp.add(new HTML("<b>Unable to fetch sections data</b>"));
		}
	}

	private void createPopup(final boolean popRight) {
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
				} else {
					Element re = event.getRelativeElement();
					pp.show();
					if (popRight) {
						int x = re.getAbsoluteLeft();
						int y = re.getAbsoluteTop() + re.getOffsetHeight();
						pp.setPopupPosition(x, y);
					} else {
						int x = re.getAbsoluteLeft()
								- pp.getElement().getOffsetWidth()
								+ re.getOffsetWidth();
						int y = event.getRelativeElement().getAbsoluteTop()
								+ event.getRelativeElement().getOffsetHeight();
						pp.setPopupPosition(x, y);
					}
				}
			}
		});

	}

}
