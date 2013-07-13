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

package fr.aliasource.webmail.client.settings;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.FilterDefinition;

/**
 * 
 * @author tom
 * 
 */
public class FilterSettingsTab extends DockPanel implements ISettingsPage,
		ISettingChangeListener {

	private View ui;
	private Grid dataGrid;

	public FilterSettingsTab(View ui) {
		this.ui = ui;
		setWidth("100%");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");

		dataGrid = new Grid(1, 4);
		dataGrid.setWidth("100%");
		dataGrid.getCellFormatter().setWidth(0, 0, "90%");
		dataGrid.setStyleName("settingsTable");
		verticalPanel.add(dataGrid);
		add(verticalPanel, DockPanel.CENTER);
	}

	public void addChangeListener(IChangeListener changeListener) {
		changeListener.addChangeListener(this);
	}

	public void init() {
		ui.getSpinner().startSpinning();
		reloadGridData();
	}

	private void reloadGridData() {
		dataGrid.clear();
		AjaxCall.filters
				.listFilters(new AsyncCallback<List<FilterDefinition>>() {
					public void onSuccess(List<FilterDefinition> result) {
						ui.getSpinner().stopSpinning();
						int i = 0;
						dataGrid.resizeRows(result.size());
						for (FilterDefinition fd : result) {
							createFDWidget(fd, i++);
						}
					}

					public void onFailure(Throwable caught) {
						ui.getSpinner().stopSpinning();
						GWT.log("listFilter failure", caught);
					}
				});
	}

	private void createFDWidget(final FilterDefinition fd, final int row) {
		StringBuilder crits = new StringBuilder();
		crits.append(I18N.strings.filterMatches());
		int i = 0;
		for (String s : fd.getCriteria().keySet()) {
			if (i++ > 0) {
				crits.append(", ");
			}
			crits.append(s);
			crits.append(": ");
			crits.append(fd.getCriteria().get(s));
		}
		crits.append(I18N.strings.filterDoThis());
		i = 0;
		if (fd.getDeliverInto() != null) {
			crits.append(I18N.strings.filterDeliverInto(fd.getDeliverInto()));
			i++;
		} else if (fd.isDelete()) {
			crits.append(I18N.strings.filterDeleteIt());
			i++;
		}
		if (fd.isStarIt()) {
			if (i++ > 0) {
				crits.append(", ");
			}
			crits.append(I18N.strings.filterStarIt());
		}
		if (fd.isMarkAsRead()) {
			if (i++ > 0) {
				crits.append(", ");
			}
			crits.append(I18N.strings.filterMarkItAsRead());
		}
		if (fd.getForwardTo() != null) {
			if (i++ > 0) {
				crits.append(", ");
			}
			crits.append(I18N.strings.filterForwardItTo(fd.getForwardTo()));
		}

		HTML l = new HTML(crits.toString());
		dataGrid.setWidget(row, 0, l);
		Anchor delLnk = new Anchor(I18N.strings.delete());
		dataGrid.setWidget(row, 3, delLnk);
		delLnk.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				AjaxCall.filters.removeFilter(fd, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						GWT.log("error removing filter", caught);
						ui.notifyUser(I18N.strings.filterRemoveError());
					}

					public void onSuccess(Void v) {
						reloadGridData();
					}
				});
			}
		});

		if (row % 2 == 0) {
			dataGrid.getRowFormatter().setStyleName(row, "odd");
		} else {
			dataGrid.getRowFormatter().setStyleName(row, "even");
		}

		dataGrid.getCellFormatter().setStyleName(row, 0, "settingsCell");
		dataGrid.getCellFormatter().setStyleName(row, 1, "settingsCell");
		dataGrid.getCellFormatter().setStyleName(row, 2, "settingsCell");
		dataGrid.getCellFormatter().setStyleName(row, 3, "settingsCell");

	}

	public void notifySettingChanged() {
	}

	@Override
	public void shutdown() {
		GWT.log("shutdown should be implemented", null);
	}
}
