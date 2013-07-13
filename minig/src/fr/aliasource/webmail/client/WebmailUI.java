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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.ctrl.AjaxCall;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebmailUI implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel rp = RootPanel.get("webmail_root");
		final SplashScreen ss = new SplashScreen();
		rp.clear();
		rp.add(ss);
		final RootPanel rPanel = rp;

		LoadSettingsCallback lsc = new LoadSettingsCallback(ss, rPanel);
		AjaxCall.settings.getAllSettings(lsc);
	}
}
