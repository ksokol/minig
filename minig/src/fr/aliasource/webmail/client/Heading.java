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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.Features;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.obm.OBMLinks;

/**
 * Webmail heading with logo, logout link, etc.
 * 
 * @author tom
 * 
 */
public class Heading extends DockPanel implements ISpinner {

	private Anchor logout;
	private Anchor settings;
	private View ui;
	private Image spinner;
	private int spinState = 0;

	public Heading(final View ui) {
		this.ui = ui;

		Image logo = new Image("minig/images/logo_minig.png");
		logo.setStyleName("obmLogo");
		add(logo, DockPanel.WEST);
		
		final String extUrl = WebmailController.get().getSetting("obm/external_url");
		if (extUrl != null) {
			logo.addStyleName("addressBookItem"); // cursor:pointer...
			logo.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					Window.Location.assign(extUrl);
				}
			});
		}

		// add heading with obm links
		if (Features.OBM_SECTIONS) {
			OBMLinks ol = new OBMLinks();
			add(ol, DockPanel.CENTER);
			setCellVerticalAlignment(ol, HasVerticalAlignment.ALIGN_MIDDLE);
		}

		HorizontalPanel links = new HorizontalPanel();
		links.addStyleName("headingStdLinks");
		add(links, DockPanel.EAST);
		links.setSpacing(4);
		setCellHorizontalAlignment(links, DockPanel.ALIGN_RIGHT);
		setWidth("100%");

		spinner = new Image("minig/images/spinner_moz.gif");
		links.add(spinner);
		links.setCellVerticalAlignment(spinner, DockPanel.ALIGN_MIDDLE);
		spinner.setVisible(false);

		if (RootPanel.get("mainPanel") != null) {
			return;
		}

		String userLbl = ui.getUserName();
		if (userLbl.contains("@")) {
			userLbl = userLbl.replace("@", " (")+")";
		}
		
		Label user = new Label(userLbl);
		user.addStyleName("userNameLabel");
		links.add(user);

		settings = new Anchor(I18N.strings.settings());
		settings.addStyleName("settingsLabel");
		links.add(settings);

		logout = new Anchor(I18N.strings.signOut());
		logout.addStyleName("logoutLabel");
		links.add(logout);

		setStyleName("heading");

		logout.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				ui.log("logging out...");
				logout();
			}
		});

		settings.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.log("Show settings");
				ui.showGlobalSettings();
			}
		});

	}

	private void logout() {
		AsyncCallback<Void> ac = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				GWT.log("logout failure", caught);
			}

			public void onSuccess(Void result) {
				ui.logout();
			}
		};
		AjaxCall.logout.logout(ac);
	}

	public void startSpinning() {
		spinState++;
		updateSpinner();
	}

	public void stopSpinning() {
		spinState--;
		if (spinState < 0) {
			spinState = 0;
		}
		updateSpinner();
	}

	private void updateSpinner() {
		boolean visible = spinState > 0;
		spinner.setVisible(visible);
		if (visible) {
			RootPanel.get("page_body").addStyleName("cursorWait");
		} else {
			RootPanel.get("page_body").removeStyleName("cursorWait");
		}
	}

}
