package fr.aliasource.webmail.client.addressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.book.UiContact;

public class MultiContactDisplay extends AbstractContactCard {

	private View ui;

	public MultiContactDisplay(Set<UiContact> contacts, View ui) {
		update(contacts);
		this.ui = ui;
	}

	public void update(Set<UiContact> contacts) {
		clear();
		Label l = new Label(contacts.size() + " "
				+ I18N.strings.contactsSelected());
		l.setStyleName("label");

		final List<String> query = new ArrayList<String>(contacts.size());
		final List<EmailAddress> ads = new ArrayList<EmailAddress>(contacts.size());
		for (UiContact c : contacts) {
			if (c.getEmails().size() > 0) {
				String mail = c.getEmails().values().iterator().next()
						.getEmail();
				ads.add(new EmailAddress(c.getDisplayName(), mail));
				query.add("(from:" + mail + " OR to:" + mail + ")");
			}
		}

		// Construct search query
		final StringBuilder any = new StringBuilder("(");
		final StringBuilder all = new StringBuilder("(");
		for (int j = 0; j < query.size(); j++) {
			if (j > 0) {
				any.append(" OR ");
				all.append(" AND ");
			}
			any.append(query.get(j));
			all.append(query.get(j));
		}
		any.append(")");
		all.append(")");

		Anchor email = new Anchor("Email");
		email.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ClientMessage cm = new ClientMessage();
				cm.setTo(ads);
				cm.setSubject("");
				cm.setBody(new Body("text/plain", ""));
				ui.selectTab(View.COMPOSER);
				ui.getComposer().loadDraft(cm, null);
			}
		});

		HTML recentConversations = new HTML(I18N.strings.recentConversations()
				+ "&nbsp;:&nbsp;");
		Anchor showRecentAnyConversations = new Anchor(I18N.strings
				.showAnyContactsRecentConversations());
		showRecentAnyConversations.setHref("#");
		showRecentAnyConversations.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				String q = any.toString();
				ui.getToolbar().getSearchBox().setSearchMailQuery(q);
				WebmailController.get().getSelector().select(new Folder(q));
				WebmailController.get().getSelector().addSearchFolder(
						"search:" + q, q);
				ui.setQuery(q);
			}
		});

		Anchor showRecentAllConversations = new Anchor(I18N.strings
				.showAllContactsRecentConversations());
		showRecentAllConversations.setHref("#");
		showRecentAllConversations.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				String q = all.toString();
				ui.getToolbar().getSearchBox().setSearchMailQuery(q);
				WebmailController.get().getSelector().select(new Folder(q));
				WebmailController.get().getSelector().addSearchFolder(
						"search:" + q, q);
				ui.setQuery(q);
			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(recentConversations);
		hp.add(showRecentAnyConversations);
		hp.add(new HTML(",&nbsp;"));
		hp.add(showRecentAllConversations);

		add(l);
		add(email);
		add(hp);

		setSpacing(4);
	}

}
