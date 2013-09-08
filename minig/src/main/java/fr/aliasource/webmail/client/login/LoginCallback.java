/**
 * 
 */
package fr.aliasource.webmail.client.login;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.ctrl.AjaxCall;

/**
 * AJAX callback used when the login button is clicked
 * 
 * @author tom
 * 
 */
class LoginCallback implements AsyncCallback<Boolean> {

	/**
	 * 
	 */
	private final LoginUI loginUi;
	private final RootPanel rp;
	private AsyncCallback<HashMap<String, String>> showMinigCB;

	LoginCallback(LoginUI loginUI, String login, String domain,
			Map<String, String> settings, RootPanel rp,
			AsyncCallback<HashMap<String, String>> showMinigCB) {
		this.loginUi = loginUI;
		this.rp = rp;
		this.showMinigCB = showMinigCB;
	}

	public void onFailure(Throwable caught) {
		loginUi.stopSpinning();
		loginUi.setStatus("Login failed (server problem).");
	}

	public void onSuccess(Boolean valid) {
		loginUi.stopSpinning();
		if (valid.booleanValue()) {
			rp.clear();
			AjaxCall.settings.getAllSettings(showMinigCB);
		} else {
			loginUi.setStatus("Login failed (check login & password).");
		}
	}
}