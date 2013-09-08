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

package fr.aliasource.webmail.client.rpc;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * GetSettings AJAX class interface
 * 
 * @author tom
 * 
 */
@RemoteServiceRelativePath("settings")
public interface GetSettings extends RemoteService {

	public static final String AJAX_LOGIN = "frontend.ajaxLogin";
	public static final String LOGOUT_URL = "frontend.logoutUrl";
	public static final String CURRENT_LOGIN = "frontend.login";
	public static final String CURRENT_DOMAIN = "frontend.domain";
	public static final String SSO_PROVIDER = "frontend.ssoProvider";
	public static final String DEFAULT_DOMAIN = "backend/backend.defaultDomain";
	public static final String SERVER_SETTINGS_LOADED = "backend.settingsLoaded";

	/* BACKEND PROVIDED SETTINGS */

	public static final String SENT_FOLDER = "account_configuration/account_folders_sent";
	public static final String TRASH_FOLDER = "account_configuration/account_folders_trash";
	public static final String DRAFTS_FOLDER = "account_configuration/account_folders_drafts";
	public static final String TEMPLATES_FOLDER = "account_configuration/account_folders_templates";
	public static final String SPAM_FOLDER = "account_configuration/account_folders_spam";

	public static final String XMPP_PASSWORD = "xmpp_configuration/account_password";
	public static final String XMPP_BIND_URL = "xmpp.http-bind.url";

	/* OBM PROVIDED SETTINGS */

	public static final String LANGUAGE = "obm/set_lang";
	public static final String TIME_ZONE = "obm/set_timezone";

	/**
	 * Returns frontend & backend settings to AJAX ui
	 * 
	 * @return all settings
	 */
	HashMap<String, String> getAllSettings();

}
