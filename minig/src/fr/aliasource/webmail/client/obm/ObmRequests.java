package fr.aliasource.webmail.client.obm;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;

public class ObmRequests {

	public static void fetchSectionsHtml(RequestCallback rc)
			throws RequestException {
		String url = GWT.getModuleBaseURL() + "obmProxy";
		GWT.log("fetch sections url: " + url);
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, url);
		rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String login = getLogin();
		rb.sendRequest("action=sections_html&login=" + login, rc);
	}

	private static String getLogin() {
		Map<String, String> settings = WebmailController.get().getSettings();
		String login = settings.get(GetSettings.CURRENT_LOGIN) + "@"
				+ settings.get(GetSettings.CURRENT_DOMAIN);
		return login;
	}

	public static void fetchGroupwareLinks(RequestCallback rc)
			throws RequestException {
		String url = GWT.getModuleBaseURL() + "obmProxy";
		GWT.log("fetch gw links url: " + url);
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, url);
		rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
		rb.sendRequest("action=gw_links&login=" + getLogin(), rc);
	}

}
