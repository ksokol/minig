package fr.aliasource.webmail.client.filter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.FilterDefinition;

public class TestFilterHandler implements ClickHandler {

	private CreateAFilterForm caff;

	public TestFilterHandler(CreateAFilterForm caff) {
		this.caff = caff;
	}

	public String getSearchQuery() {
		FilterDefinition fd = caff.getDefinition();
		StringBuilder q = new StringBuilder();

		String from = fd.getCriteria().get("from");
		if (from != null && from.length() > 0) {
			q.append("from:");
			q.append(from);
			q.append(" ");
		}

		String to = fd.getCriteria().get("to");
		if (to != null && to.length() > 0) {
			q.append("to:");
			q.append(to);
			q.append(" ");
		}

		String sub = fd.getCriteria().get("subject");
		if (sub != null && sub.length() > 0) {
			q.append("subject:");
			q.append(sub);
			q.append(" ");
		}

		q.append("in:inbox");

		return q.toString();
	}

	@Override
	public void onClick(ClickEvent event) {
		String s = getSearchQuery();
		if (s.trim().length() > 0) {
			WebmailController.get().getSelector().addSearchFolder(s);
			WebmailController.get().getView().setQuery(s);
		}
	}

}
