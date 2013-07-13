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

package fr.aliasource.webmail.client.filter;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IFolderSelectionListener;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

public class DeliverIntoWidget extends HorizontalPanel implements
		IFolderSelectionListener {

	private CheckBox cb;
	private ListBox folderQuery;

	public DeliverIntoWidget() {
		cb = new CheckBox(I18N.strings.actionDeliverInto());
		folderQuery = new ListBox(false);
		folderQuery.addItem(I18N.strings.deliverFolderPlaceholder(), null);

		add(cb);
		add(new HTML("&nbsp;"));
		add(folderQuery);
	}

	private void buildSelectBox(Folder[] folders) {
		folderQuery.clear();

		folderQuery.addItem("Choose folder...", null);

		for (Folder f : folders) {
			String displayName = WebmailController.get().displayName(f);
			folderQuery.addItem(displayName, f.getName());
		}

		Element e = folderQuery.getElement();
		NodeList<com.google.gwt.dom.client.Element> toStyle = e
				.getElementsByTagName("option");

		for (int i = 1; i < toStyle.getLength(); i++) {
			com.google.gwt.dom.client.Element opt = toStyle.getItem(i);
			Folder f = folders[i - 1];
			int margin = 10 * depth(f.getDisplayName());
			opt.setAttribute("style", "margin-left: " + margin + "px");
		}
	}

	private int depth(String dName) {
		int i = 0;
		while (dName.charAt(i) == ' ') {
			i++;
		}
		return i;
	}

	public void startListeners() {
		WebmailController.get().getSelector().addListener(this);
	}

	public void folderSelected(Folder f) {
		// TODO Auto-generated method stub
	}

	public void foldersChanged(Folder[] folders) {
		// TODO Auto-generated method stub
		buildSelectBox(folders);
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
		// TODO Auto-generated method stub

	}

	public String getFolder() {
		return folderQuery.getValue(folderQuery.getSelectedIndex());
	}

}
