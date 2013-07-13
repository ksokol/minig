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

package fr.aliasource.webmail.client.conversations;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IFolderSelectionListener;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.QuotaInfo;

public class ConversationFolderQuota extends Widget implements
		IFolderSelectionListener {

	private static int quotaHigh = 80;
	private View ui;
	private QuotaInfo quotaInfo;
	private Element quotaBarShell;
	private Element barElement;

	public ConversationFolderQuota(View ui) {
		this.ui = ui;
		this.initConvToolbar();
		this.setVisible(false);
		WebmailController.get().getSelector().addListener(this);
		sinkEvents(Event.ONCLICK);
	}

	private void initConvToolbar() {
		// Create the outer shell
		quotaBarShell = DOM.createDiv();
		setElement(quotaBarShell);
		DOM.setStyleAttribute(quotaBarShell, "position", "relative");
		setStyleName("quotaBar-shell");

		// Create the bar element
		barElement = DOM.createDiv();
		DOM.appendChild(getElement(), barElement);
		DOM.setStyleAttribute(barElement, "height", "100%");
		DOM.setElementProperty(barElement, "className", "quotaBar-bar");
	}

	public void updateBar(String mailbox, QuotaInfo qi) {
		quotaInfo = qi;
		if (quotaInfo.isEnable()) {
			if (quotaInfo.getFilling() < ConversationFolderQuota.quotaHigh) {
				DOM.setElementProperty(barElement, "className",
						"quotaBar-bar-low");
			} else {
				DOM.setElementProperty(barElement, "className",
						"quotaBar-bar-high");
			}

			DOM.setStyleAttribute(barElement, "width", ""
					+ quotaInfo.getFilling());
			this.setVisible(true);
			int usage = quotaInfo.getUsage();
			int limit = quotaInfo.getLimit();
			setTitle(title(usage, limit));
		} else {
			this.setVisible(false);
			setTitle(null);
		}
	}

	public void folderSelected(Folder f) {
	}

	public void foldersChanged(Folder[] folders) {
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
	}

	private String title(int usage, int limit) {
		return I18N.strings.quotaInfo(usage + I18N.strings.sizeKilobyte(),
				Integer.toString(quotaInfo.getFilling()), limit
						+ I18N.strings.sizeKilobyte());
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (quotaInfo != null && quotaInfo.isEnable()) {
			int usage = quotaInfo.getUsage();
			int limit = quotaInfo.getLimit();
			ui.notifyUser(title(usage, limit));
		}
	}
}
