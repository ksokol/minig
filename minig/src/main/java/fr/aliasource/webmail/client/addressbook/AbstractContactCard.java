package fr.aliasource.webmail.client.addressbook;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractContactCard extends VerticalPanel {

	protected boolean empty(String s) {
		return s == null || s.trim().length() == 0;
	}

	protected VerticalPanel contactAddressDetail(String street, String zip,
			String location) {
		VerticalPanel vp = new VerticalPanel();

		if (!street.isEmpty()) {
			vp.add(new Label(street));
		}

		if (!zip.isEmpty() || !location.isEmpty()) {
			HorizontalPanel address = new HorizontalPanel();
			address.add(new Label(zip));
			address.add(new HTML("&nbsp;"));
			address.add(new Label(location));
			vp.add(address);
		}

		return vp;
	}

}
