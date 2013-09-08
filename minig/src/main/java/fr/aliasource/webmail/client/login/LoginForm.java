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

import java.util.Map;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import fr.aliasource.webmail.client.rpc.GetSettings;

public class LoginForm extends Grid {

	private TextBox login;
	private PasswordTextBox password;
	private LoginUI loginUI;

	public LoginForm(Map<String, String> settings, LoginUI loginUI) {
		super(3, 2);

		this.loginUI = loginUI;

		if (settings.containsKey(GetSettings.DEFAULT_DOMAIN)) {
			setText(0, 0, "Username: ");
		} else {
			setText(0, 0, "Username@domain: ");
		}
		setText(1, 0, "Password: ");
		login = new TextBox();
		login.setWidth("20em");
		password = new PasswordTextBox();
		password.setWidth("20em");
		setWidget(0, 1, login);
		setWidget(1, 1, password);

		addKeyListeners();
	}

	private void addKeyListeners() {
		password.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					loginUI.doLogin();
				}
			}
		});
	}

	public String getLogin() {
		return login.getText();
	}

	public String getPassword() {
		return password.getText();
	}

}
