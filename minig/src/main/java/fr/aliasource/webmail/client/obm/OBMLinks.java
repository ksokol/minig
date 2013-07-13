package fr.aliasource.webmail.client.obm;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OBMLinks extends HorizontalPanel {

	public OBMLinks() {

		SectionsMenu sections = new SectionsMenu();
		add(sections);
		DOM.setStyleAttribute(sections.getElement(), "marginLeft", "100px");
		DOM.setStyleAttribute(sections.getElement(), "paddingRight", "6px");

		final HorizontalPanel links = new HorizontalPanel();

		RequestCallback fetchLinks = new RequestCallback() {

			@Override
			public void onResponseReceived(Request arg0, Response arg1) {
				GWT.log("*****************\n" + arg1.getText()
						+ "\n*******************");
				String[] hrefs = arg1.getText().split("\n");
				if (hrefs.length == 0) {
					GWT.log("no hrefs found", new Throwable("no hrefs found"));
				}
				for (String href : hrefs) {
					links.add(new HTML(href));
				}
				addWebmailLink();
			}

			@Override
			public void onError(Request arg0, Throwable arg1) {
				// TODO Auto-generated method stub

			}
		};
		try {
			ObmRequests.fetchGroupwareLinks(fetchLinks);
		} catch (RequestException e) {
			GWT.log("Error fetching groupware links", e);
		}

		add(links);
		links.addStyleName("headingStdLinks");
		Iterator<Widget> it = links.iterator();
		while (it.hasNext()) {
			links.setCellVerticalAlignment(it.next(),
					HasVerticalAlignment.ALIGN_MIDDLE);
		}

		it = iterator();
		while (it.hasNext()) {
			setCellVerticalAlignment(it.next(),
					HasVerticalAlignment.ALIGN_MIDDLE);
		}

	}

	private void addWebmailLink() {

	}

}
