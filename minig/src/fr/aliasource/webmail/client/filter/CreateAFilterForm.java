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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.FilterDefinition;

public class CreateAFilterForm extends HorizontalPanel {

	private TextBox from;
	private TextBox to;
	private TextBox subject;
	private TextBox hasTheWords;
	private TextBox notTheWords;
	private CheckBox hasAttachment;

	private VerticalPanel content;
	private DeliverIntoWidget deliver;
	private ForwardToWidget forwardTo;
	private CheckBox starIt;
	private CheckBox markAsRead;
	private CheckBox deleteIt;
	private CheckBox applyToMailbox;

	public CreateAFilterForm(final View ui) {

		VerticalPanel forms = new VerticalPanel();
		forms.setWidth("100%");
		forms.add(createHeader(ui));
		content = new VerticalPanel();
		createSearchCriteriaForm(ui);
		forms.add(content);

		setWidth("100%");
		add(forms);

	}

	private void createSearchCriteriaForm(final View ui) {

		FlexTable ft = new FlexTable();

		if (from == null) {
			from = new TextBox();
			to = new TextBox();
			subject = new TextBox();
			hasTheWords = new TextBox();
			hasTheWords.setEnabled(false);
			notTheWords = new TextBox();
			notTheWords.setEnabled(false);
			hasAttachment = new CheckBox(I18N.strings.hasAttachments());
			hasAttachment.setEnabled(false);
		}

		ft.setWidget(0, 0, new Label(I18N.strings.from() + ":"));
		ft.setWidget(0, 1, from);
		ft.setWidget(1, 0, new Label(I18N.strings.to() + ":"));
		ft.setWidget(1, 1, to);
		ft.setWidget(2, 0, new Label(I18N.strings.subject() + ":"));
		ft.setWidget(2, 1, subject);

		/*ft.setWidget(0, 2, new Label(I18N.strings.hasTheWords()));
		ft.setWidget(0, 3, hasTheWords);
		ft.setWidget(1, 2, new Label(I18N.strings.doNotHave()));
		ft.setWidget(1, 3, notTheWords);
		ft.setWidget(2, 3, hasAttachment);*/

		content.setStyleName("advancedSearchForm");
		content.setWidth("100%");
		HTML header = new HTML(I18N.strings.filterCriteriaHeader());
		content.add(header);
		content.add(ft);
		Widget w = createSearchCriteriaButtons(ui);
		content.add(w);
		content.setCellHorizontalAlignment(ft, VerticalPanel.ALIGN_CENTER);
		content.setCellHorizontalAlignment(w, VerticalPanel.ALIGN_CENTER);
	}

	private void createChooseActionForm(final View ui) {
		FlexTable ft = new FlexTable();

		if (markAsRead == null) {
			markAsRead = new CheckBox(I18N.strings.filterMarkItAsRead());
			starIt = new CheckBox(I18N.strings.filterStarIt());
			forwardTo = new ForwardToWidget();
			deliver = new DeliverIntoWidget();
			deleteIt = new CheckBox(I18N.strings.filterDeleteIt());
		}

		ft.setWidget(0, 0, markAsRead);
		ft.setWidget(1, 0, starIt);
		ft.setWidget(2, 0, forwardTo);
		ft.setWidget(3, 0, deliver);
		ft.setWidget(4, 0, deleteIt);

		content.setStyleName("advancedSearchForm");
		content.setWidth("100%");
		HTML header = new HTML(I18N.strings.filterActionHeader());
		content.add(header);
		content.add(ft);
		Widget w = createChooseActionsButtons(ui);
		content.add(w);
		content.setCellHorizontalAlignment(ft, VerticalPanel.ALIGN_CENTER);
		content.setCellHorizontalAlignment(w, VerticalPanel.ALIGN_CENTER);

		deliver.startListeners();
	}

	private Widget createSearchCriteriaButtons(final View ui) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		Button cancelButton = new Button(I18N.strings.cancel());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				cancelFilterCreation(ui);
			}
		});

		hp.add(cancelButton);

		Button testSearch = new Button(I18N.strings.filterTestSearch());
		testSearch.addClickHandler(new TestFilterHandler(this));
		hp.add(testSearch);

		Button nextStep = new Button(I18N.strings.filterNextStep() + " »");
		nextStep.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (!getDefinition().getCriteria().isEmpty()) {
					for (String s : getDefinition().getCriteria().keySet()) {
						GWT.log("filter criteria '" + s + "': '"
								+ getDefinition().getCriteria().get(s) + "'",
								null);
					}
					content.clear();
					createChooseActionForm(ui);
				}
			}
		});
		hp.add(nextStep);
		return hp;
	}

	FilterDefinition getDefinition() {
		FilterDefinition ret = new FilterDefinition();

		Map<String, String> crits = new HashMap<String, String>();
		if (!isEmpty(from)) {
			crits.put("from", from.getText());
		}
		if (!isEmpty(to)) {
			crits.put("to", to.getText());
		}
		if (!isEmpty(subject)) {
			crits.put("subject", subject.getText());
		}
		if (!isEmpty(hasTheWords)) {
			crits.put("hasTheWords", hasTheWords.getText());
		}
		if (!isEmpty(notTheWords)) {
			crits.put("notTheWords", notTheWords.getText());
		}
		ret.setCriteria(crits);

		ret.setMarkAsRead(markAsRead != null && markAsRead.getValue());
		ret.setDelete(deleteIt != null && deleteIt.getValue());
		ret.setStarIt(starIt != null && starIt.getValue());
		if (deliver != null) {
			ret.setDeliverInto(deliver.getFolder());
		}
		if (forwardTo != null) {
			ret.setForwardTo(forwardTo.getEmail());
		}
		return ret;
	}

	private boolean isEmpty(TextBox tb) {
		return !(tb != null && tb.getText() != null && tb.getText().trim()
				.length() > 0);
	}

	private Widget createChooseActionsButtons(final View ui) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		Anchor showFilter = new Anchor(I18N.strings.showCurrentFilters());
		showFilter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WebmailController.get().getView().showFilterSettings();
			}
		});
		hp.add(showFilter);

		Button cancelButton = new Button(I18N.strings.cancel());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				cancelFilterCreation(ui);
			}
		});

		hp.add(cancelButton);

		Button back = new Button("« " + I18N.strings.filterBack());
		back.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				content.clear();
				createSearchCriteriaForm(ui);
			}
		});
		hp.add(back);

		Button createFilter = new Button(I18N.strings.createFilter());
		hp.add(createFilter);
		createFilter.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				FilterDefinition fd = getDefinition();
				ui.getSpinner().startSpinning();
				AjaxCall.filters.storeFilter(fd, new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						ui.getSpinner().stopSpinning();
						cancelFilterCreation(ui);
						ui.notifyUser(I18N.strings.filterAddSuccess());
					}

					public void onFailure(Throwable caught) {
						GWT.log("error creating filter", caught);
						ui.getSpinner().stopSpinning();
					}
				});
			}
		});

		applyToMailbox = new CheckBox(I18N.strings.filterAlsoApplyNow());
		applyToMailbox.setEnabled(false); // TODO
		hp.add(applyToMailbox);

		for (int i = 0; i < hp.getWidgetCount(); i++) {
			hp.setCellVerticalAlignment(hp.getWidget(i),
					HorizontalPanel.ALIGN_MIDDLE);
		}

		return hp;
	}

	private Widget createHeader(final View ui) {

		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().setAttribute("style", "width:100%; padding-left: 10em");

		DockPanel titleBar = new DockPanel();
		Label title = new Label(I18N.strings.createAFilter());
		title.setStyleName("bold");
		Anchor hideOptions = new Anchor(I18N.strings.hideFilterOptions());
		hideOptions.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.getToolbar().getSearchBox().setVisible(true);
				content.clear();
				createSearchCriteriaForm(ui);
				setVisible(false);
			}
		});

		titleBar.add(title, DockPanel.WEST);

		titleBar.add(hideOptions, DockPanel.EAST);
		titleBar.setCellHorizontalAlignment(hideOptions, DockPanel.ALIGN_RIGHT);
		titleBar.setStyleName("advancedSearchHeader");
		titleBar.setWidth("100%");

		hp.add(titleBar);
		return hp;

	}

	private void cancelFilterCreation(final View ui) {
		from = null;
		markAsRead = null;
		ui.getToolbar().getSearchBox().setVisible(true);
		content.clear();
		createSearchCriteriaForm(ui);
		setVisible(false);
	}

}