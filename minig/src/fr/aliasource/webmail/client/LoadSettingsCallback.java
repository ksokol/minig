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

package fr.aliasource.webmail.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.login.LoginUI;
import fr.aliasource.webmail.client.rpc.GetSettings;

/**
 * Callback used for application startup
 * 
 * @author tom
 * 
 */
public class LoadSettingsCallback implements
		AsyncCallback<HashMap<String, String>> {

	private static Set<String> supportedLocales;

	static {
		supportedLocales = new HashSet<String>();
		supportedLocales.add("fr");
		supportedLocales.add("en");
	}

	private static boolean langRedirectDone = false;

	private RootPanel rp;
	private SplashScreen ss;

	public LoadSettingsCallback(SplashScreen ss, RootPanel rp) {
		this.ss = ss;
		this.rp = rp;
	}

	private String s(Map<String, String> allSettings, String settingKey) {
		return allSettings.get(settingKey);
	}

	private boolean booleanSetting(Map<String, String> allSettings,
			String settingKey) {
		return "true".equals(s(allSettings, settingKey));
	}

	public void onFailure(Throwable caught) {
		rp.clear();
		VerticalPanel vp = new VerticalPanel();
		vp.add(new Label("Error getting settings"));
		vp.add(new HTML(caught.getMessage()));
		rp.add(vp);
	}

	public void onSuccess(HashMap<String, String> m) {
		if (booleanSetting(m, GetSettings.AJAX_LOGIN)) {
			showAjaxLogin(m);
		} else {
			showWebmail(m);
		}
	}

	private void doLangRedirect(HashMap<String, String> m) {
		if (langRedirectDone) {
			return;
		}

		String lang = m.get(GetSettings.LANGUAGE);
		if (lang == null || !supportedLocales.contains(lang)) {
			lang = "en";
		}
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		if (locale.equals("default") || !supportedLocales.contains(locale)) {
			locale = "en";
		}
		if (!locale.equals(lang)) {
			GWT.log("will try to open: " + getHostPageLocation() + "?locale="
					+ lang, null);
			Window.open(getHostPageLocation() + "?locale=" + lang, "_self", "");
		}
		langRedirectDone = true;
	}

	private AsyncCallback<HashMap<String, String>> getAllSettingsCB(
			final HashMap<String, String> m) {
		AsyncCallback<HashMap<String, String>> ac = new AsyncCallback<HashMap<String, String>>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(HashMap<String, String> newSettings) {
				m.putAll(newSettings);
				doLangRedirect(m);
				displayMiniG(m);
			}
		};
		return ac;
	}

	private void showWebmail(final HashMap<String, String> m) {
		ss.setPercent(75);
		if (!m.containsKey(GetSettings.SERVER_SETTINGS_LOADED)) {
			AjaxCall.settings.getAllSettings(getAllSettingsCB(m));
		} else {
			doLangRedirect(m);
			displayMiniG(m);
		}
	}

	private void displayMiniG(HashMap<String, String> settings) {
		WebmailController.get().start(rp, settings);
	}

	private void showAjaxLogin(HashMap<String, String> m) {
		doLangRedirect(m);

		rp.clear();
		LoginUI loginUi = new LoginUI(rp, getAllSettingsCB(m), m);
		rp.add(loginUi);
	}

	private static native String getHostPageLocation()
	/*-{
		var s = $doc.location.href;

		// Pull off any hash.
		var i = s.indexOf('#');
		if (i != -1)
		  s = s.substring(0, i);

		// Pull off any query string.
		i = s.indexOf('?');
		if (i != -1)
		  s = s.substring(0, i);

		// Ensure a final slash if non-empty.
		return s;
	}-*/;

}
