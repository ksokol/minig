/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package fr.aliasource.webmail.client.login;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.ISpinner;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.rpc.GetSettings;

/**
 * The AJAX login page. This page is displayed when ajaxLogin is set to true in
 * frontend config file, or when the login filter failed to authentificate the
 * user.
 * 
 * @author tom
 * 
 */
public class LoginUI extends DockPanel implements ISpinner {

	private HorizontalPanel status;
	private DockPanel loginForm;
	private LoginForm creds;
	private RootPanel rp;
	private HashMap<String, String> settings;
	private AsyncCallback<HashMap<String, String>> showMinigCB;

	public LoginUI(RootPanel rp,
			AsyncCallback<HashMap<String, String>> showMinigCB,
			HashMap<String, String> settings) {
		loginForm = new DockPanel();
		this.rp = rp;
		this.settings = settings;
		this.showMinigCB = showMinigCB;

		Image minigLogo = new Image("minig/images/logo_minig_big.png");
		loginForm.add(minigLogo, DockPanel.NORTH);
		loginForm.setCellHorizontalAlignment(minigLogo, DockPanel.ALIGN_CENTER);

		HTML minigLabel = new HTML("&nbsp;");
		minigLabel.setHeight("30px");
		loginForm.add(minigLabel, DockPanel.NORTH);
		loginForm
				.setCellHorizontalAlignment(minigLabel, DockPanel.ALIGN_CENTER);

		creds = new LoginForm(settings, this);
		loginForm.add(creds, DockPanel.CENTER);
		loginForm.setCellHorizontalAlignment(creds, DockPanel.ALIGN_CENTER);

		Button loginButton = new Button("Login");
		loginForm.add(loginButton, DockPanel.SOUTH);
		status = new HorizontalPanel();
		status.setHeight("2em");
		loginForm.add(status, DockPanel.SOUTH);

		add(loginForm, DockPanel.CENTER);
		setCellVerticalAlignment(loginForm, DockPanel.ALIGN_MIDDLE);
		setCellHorizontalAlignment(loginForm, DockPanel.ALIGN_CENTER);

		loginForm.setCellHorizontalAlignment(loginButton,
				DockPanel.ALIGN_CENTER);
		loginForm.setCellHorizontalAlignment(status, DockPanel.ALIGN_CENTER);

		setWidth("100%");
		setHeight("100%");
		setSpacing(4);

		noSSLWarn();

		loginButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				doLogin();
			}
		});
	}

	void doLogin() {
		startSpinning();
		final String typedLogin = getLogin(creds.getLogin());
		final String typedPass = creds.getPassword();
		final String domain = getDomain(creds.getLogin(), settings);

		AsyncCallback<Boolean> auth = new LoginCallback(LoginUI.this,
				typedLogin, domain, settings, rp, showMinigCB);
		AjaxCall.login.doLogin(typedLogin, typedPass, domain, auth);
	}

	private void noSSLWarn() {
		if (!GWT.getModuleBaseURL().startsWith("https://")) {
			setStatus("Warning: Your password will transit in clear text.");
		}
	}

	private String getDomain(String typedLogin, Map<String, String> settings) {
		String[] loginParts = typedLogin.split("@");
		if (loginParts.length == 2) {
			return loginParts[1].trim();
		} else if (settings.containsKey(GetSettings.DEFAULT_DOMAIN)) {
			return (String) settings.get(GetSettings.DEFAULT_DOMAIN);
		} else {
			// return "nodefaultdomain.def";
			return "";
		}
	}

	private String getLogin(String typedLogin) {
		String[] loginParts = typedLogin.split("@");
		if (loginParts.length == 2) {
			return loginParts[0].trim();
		} else {
			return typedLogin;
		}
	}

	public void setStatus(String txt) {
		setStatus(new Label(txt));
	}

	public void startSpinning() {
		setStatus(new Image("minig/images/spinner_moz.gif"));
	}

	public void stopSpinning() {
		status.clear();
	}

	private void setStatus(Widget w) {
		status.clear();
		status.add(w);
		status.setCellVerticalAlignment(w, HorizontalPanel.ALIGN_MIDDLE);
	}

}
