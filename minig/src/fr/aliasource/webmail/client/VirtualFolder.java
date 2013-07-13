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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Create a virtual folder
 * 
 * @author david
 * 
 */
public class VirtualFolder extends Anchor {
	public VirtualFolder(final View ui, final SideBar sideBar,
			final String label, final String query) {
		super(label);
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				WebmailController.get().getSelector().select(
						new Folder("search:" + query));
				// ui.getSelector().addSearchFolder(label, query);
				ui.setQuery(query);
				sideBar.setCurrentDefaultLinkStyle(VirtualFolder.this);
			}
		});
	}
}
