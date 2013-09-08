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

import java.util.HashSet;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.conversations.DataGrid;
import fr.aliasource.webmail.client.conversations.GripImage;
import fr.aliasource.webmail.client.conversations.IConversationSelectionChangedListener;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;

public class WebmailDragController extends PickupDragController implements
		IConversationSelectionChangedListener {

	private String selectTag;
	private Set<ConversationId> selectedIds;
	private DataGrid dataGrid;

	public WebmailDragController() {
		super(RootPanel.get(), false);
		setBehaviorDragProxy(true);
		setBehaviorDragStartSensitivity(2);
		selectedIds = new HashSet<ConversationId>();
	}

	@Override
	protected void restoreSelectedWidgetsLocation() {
		GWT.log("restore selected loc", null);
	}

	@Override
	protected void restoreSelectedWidgetsStyle() {
		GWT.log("restore selected style", null);
	}

	@Override
	protected void saveSelectedWidgetsLocationAndStyle() {
		GWT.log("save loc & style", null);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		GripImage p = (GripImage) context.draggable;
		HTML html = null;
		if (selectTag == null) {
			if (selectedIds.isEmpty()) {
				selectedIds.add(p.getConvId());
			}
			html = new HTML(selectedIds.size() == 1 ? I18N.strings
					.dndDropThis() : I18N.strings.dndDropThose(""
					+ selectedIds.size()));
		} else {
			html = new HTML(I18N.strings.dndDropAll());
		}
		html.addStyleName("draggedConversations");
		return html;
	}

	public void selectionChanged(Set<ConversationId> selectedIds) {
		this.selectTag = null;
		this.selectedIds.clear();
		this.selectedIds.addAll(selectedIds);
	}

	public void selectionChanged(String str) {
		this.selectTag = str;
		selectedIds.clear();
	}

	public Set<ConversationId> getSelectedIds() {
		return selectedIds;
	}

	public void setGrid(DataGrid dataGrid) {
		this.dataGrid = dataGrid;
	}

	public void runMove(Folder current, Folder target) {
		dataGrid
				.moveSomeConversations(current, target, selectedIds, true, null);
	}

}
