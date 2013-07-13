package fr.aliasource.webmail.client.addressbook;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.book.UiAddress;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.book.UiPhone;

/**
 * Widget used to display the "card" of one contact
 * 
 * @author tom
 * 
 */
public class ContactCardDisplay extends AbstractContactCard {

	private UiContact c;
	private View ui;

	public ContactCardDisplay(UiContact c, View ui) {
		this.c = c;
		this.ui = ui;

		createContactWidget();
	}

	private void createContactWidget() {
		FlexTable ft = new FlexTable();
		Map<Widget, Widget> md = new LinkedHashMap<Widget, Widget>();

		Label contact = new Label(c.getDisplayName());
		contact.setStyleName("label");

		VerticalPanel separator;
		VerticalPanel userInfo = new VerticalPanel();
		userInfo.add(contact);
		if (!empty(c.getTitle())) {
			userInfo.add(new Label(c.getTitle()));
		}
		if (!empty(c.getCompany())) {
			userInfo.add(new Label(c.getCompany()));
		}
		md.put(userInfo, new Image("minig/images/ico_nophoto.png"));

		separator = new VerticalPanel();
		separator.setHeight("5px");
		md.put(separator, new Label());

		for (String mailLabel : c.getEmails().keySet()) {
			final String id = c.getEmails().get(mailLabel).getEmail();
			Anchor email = new Anchor(id);
			email.setHref("#");
			email.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					ClientMessage cm = new ClientMessage();
					cm.setTo(Arrays.asList(new EmailAddress(c.getDisplayName(), id)));
					cm.setSubject("");
					cm.setBody(new Body("text/plain", ""));
					ui.selectTab(View.COMPOSER);
					ui.getComposer().loadDraft(cm, null);
				}
			});
			md.put(email, new Label(I18N.strings.email()));
		}

		separator = new VerticalPanel();
		separator.setHeight("5px");
		md.put(separator, new Label());

		separator = new VerticalPanel();
		separator.setHeight("5px");
		md.put(separator, new Label());

		for (String lbl : c.getPhones().keySet()) {
			String translated = LabelMappings.i18n(lbl);
			if (translated != null) {
				UiPhone a = c.getPhones().get(lbl);
				md.put(new Label(a.getNumber()), new Label(translated));
			} else {
				GWT.log("not displaying phone " + lbl
						+ " because I have not translation", null);
			}
		}

		for (String lbl : c.getAddresses().keySet()) {
			String translated = LabelMappings.i18n(lbl);
			if (translated != null) {
				UiAddress a = c.getAddresses().get(lbl);
				md.put(contactAddressDetail(a.getStreet(), a.getZipCode(), a
						.getTown()), new Label(translated));
			} else {
				GWT.log("not displaying address " + lbl
						+ " because I have not translation", null);
			}
		}

		HorizontalPanel hp = new HorizontalPanel();

		HTML recentConversations = new HTML(I18N.strings.recentConversations()
				+ "&nbsp;:&nbsp;");
		hp.add(recentConversations);

		Anchor showRecentConversations = new Anchor(I18N.strings
				.showRecentConversations());
		showRecentConversations.setHref("#");
		final StringBuilder query = new StringBuilder();
		int i = 0;
		for (String mailLabels : c.getEmails().keySet()) {
			if (i++ > 0) {
				query.append(" OR ");
			}
			String mail = c.getEmails().get(mailLabels).getEmail();
			query.append("(from:" + mail + " OR to:" + mail + ")");
		}
		showRecentConversations.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				String q = query.toString();
				ui.getToolbar().getSearchBox().setSearchMailQuery(q);
				WebmailController.get().getSelector().select(new Folder(q));
				WebmailController.get().getSelector().addSearchFolder(
						"search:" + q, q);
				ui.setQuery(query.toString());
			}
		});
		hp.add(showRecentConversations);
		md.put(hp, new Label());

		Set<Widget> keys = md.keySet();
		Iterator<Widget> it = keys.iterator();
		int j = 0;
		while (it.hasNext()) {
			Widget w = (Widget) (it.next());
			Widget label = md.get(w);
			ft.setWidget(j, 1, w);
			ft.setWidget(j, 0, label);
			ft.getCellFormatter().setStyleName(j, 0, "contactDetailKey");
			j++;
		}

		add(ft);
		add(hp);
	}

}
