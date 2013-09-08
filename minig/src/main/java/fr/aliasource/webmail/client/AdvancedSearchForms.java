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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * Advanced search form
 * 
 * @author david
 * 
 */
public class AdvancedSearchForms extends HorizontalPanel implements
		IFolderSelectionListener {

	private View ui;
	private VerticalPanel forms;
	private VerticalPanel content;
	private TextBox fromQuery;
	private TextBox toQuery;
	private TextBox subjectQuery;
	private ListBox folderQuery;
	private TextBox hasWordsQuery;
	private TextBox doNotHaveQuery;
	private CheckBox attachements;
	private ListBox dateWithinQuery;
	private TextBox dateOfQuery;
	private Button searchButton;
	private Button cancelButton;
	private Map<Folder, com.google.gwt.dom.client.Element> fe;
	private Folder[] folders;

	private static final String ONE_DAY = I18N.strings.one_day();
	private static final String THREE_DAYS = I18N.strings.x_days("3");
	private static final String ONE_WEEK = I18N.strings.one_week();
	private static final String TWO_WEEKS = I18N.strings.x_weeks("2");
	private static final String ONE_MONTH = I18N.strings.one_month();
	private static final String TWO_MONTHS = I18N.strings.x_months("2");
	private static final String SIX_MONTHS = I18N.strings.x_months("6");
	private static final String ONE_YEAR = I18N.strings.one_year();

	private static final long ONE_DAY_MS = 3600 * 24 * 1000;
	private static final long ONE_WEEK_MS = ONE_DAY_MS * 7;
	private static final long ONE_MONTH_MS = ONE_WEEK_MS * 4;
	private static final long ONE_YEAR_MS = ONE_MONTH_MS * 12;

	private static final String[] ACCEPTED_FORMAT = { "yyyy-MM-dd",
			"dd/MM/yyyy", "dd/MM/yy", "EEEE", "dd MMM", "dd MMM yyyy",
			"dd MMMM", "dd MMMM yyyy" };

	public AdvancedSearchForms(View ui) {
		this.ui = ui;
		this.folders = new Folder[0];
		WebmailController.get().getSelector().addListener(this);
		fe = new HashMap<Folder, com.google.gwt.dom.client.Element>();

		forms = new VerticalPanel();
		forms.setWidth("100%");
		forms.add(createHeader());
		forms.add(createForm());

		setWidth("100%");
		add(forms);
	}

	private Widget createHeader() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().setAttribute("style", "width:100%; padding-left: 10em");

		DockPanel titleBar = new DockPanel();
		Label title = new Label(I18N.strings.searchOptions());
		title.setStyleName("bold");
		Anchor hideOptions = new Anchor(I18N.strings.hideSearchOptions());
		hideOptions.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				hideOptions();
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

	private VerticalPanel createForm() {

		FlexTable ft = new FlexTable();
		content = new VerticalPanel();

		fromQuery = new TextBox();
		fromQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		toQuery = new TextBox();
		toQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		subjectQuery = new TextBox();
		subjectQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		folderQuery = new ListBox();

		hasWordsQuery = new TextBox();
		hasWordsQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		doNotHaveQuery = new TextBox();
		doNotHaveQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		attachements = new CheckBox();
		attachements.setText(I18N.strings.hasAttachments());

		dateWithinQuery = new ListBox();
		String[] dateWithinLabel = { ONE_DAY, THREE_DAYS, ONE_WEEK, TWO_WEEKS,
				ONE_MONTH, TWO_MONTHS, SIX_MONTHS, ONE_YEAR };
		for (String d : dateWithinLabel) {
			dateWithinQuery.addItem(d);
		}
		dateOfQuery = new TextBox();
		dateOfQuery.addKeyPressHandler(addTextBoxKeyboardListener());

		HorizontalPanel hpDate = new HorizontalPanel();
		hpDate.add(dateWithinQuery);

		Label of = new Label(" " + I18N.strings.of() + " ");
		hpDate.add(of);

		hpDate.setCellVerticalAlignment(of, HorizontalPanel.ALIGN_MIDDLE);
		hpDate.add(dateOfQuery);

		Label dateLegend = new Label(I18N.strings.dateLegend());
		dateLegend.setStyleName("advancedSearchFormLegend");

		ft.setWidget(0, 0, new Label(I18N.strings.from() + ":"));
		ft.setWidget(0, 1, fromQuery);

		ft.setWidget(1, 0, new Label(I18N.strings.to() + ":"));
		ft.setWidget(1, 1, toQuery);

		ft.setWidget(2, 0, new Label(I18N.strings.subject() + ":"));
		ft.setWidget(2, 1, subjectQuery);

		ft.setWidget(3, 0, new Label(I18N.strings.search() + ":"));
		ft.setWidget(3, 1, folderQuery);

		ft.setWidget(0, 2, new Label(I18N.strings.hasTheWords() + ":"));
		ft.setWidget(0, 3, hasWordsQuery);

		ft.setWidget(1, 2, new Label(I18N.strings.doNotHave() + ":"));
		ft.setWidget(1, 3, doNotHaveQuery);

		ft.setWidget(2, 3, attachements);

		ft.setWidget(3, 2, new Label(I18N.strings.dateWithin() + ":"));
		ft.setWidget(3, 3, hpDate);
		ft.setWidget(4, 3, dateLegend);

		content.setStyleName("advancedSearchForm");
		content.setWidth("100%");
		content.add(ft);
		content.add(createButtons());

		return content;
	}

	private KeyPressHandler addTextBoxKeyboardListener() {
		return new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					doSearch();
				}
			}
		};
	}

	private HorizontalPanel createButtons() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		searchButton = new Button(I18N.strings.searchMail());
		searchButton.addStyleName("searchButton");
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				doSearch();
			}
		});
		cancelButton = new Button(I18N.strings.cancel());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				hideOptions();
			}
		});

		hp.add(searchButton);
		hp.add(cancelButton);

		return hp;
	}

	private void doSearch() {

		StringBuilder query = new StringBuilder(50000);

		String from = fromQuery.getText();
		if (!from.isEmpty()) {
			query.append(' ').append("from:(" + from + ")");
		}

		String to = toQuery.getText();
		if (!to.isEmpty()) {
			query.append(' ').append("to:(" + to + ")");
		}

		String subject = subjectQuery.getText();
		if (!subject.isEmpty()) {
			query.append(' ').append("subject:(" + subject + ")");
		}

		String hasWords = hasWordsQuery.getText();
		if (!hasWords.isEmpty()) {
			query.append(' ').append("body:(" + hasWords + ")");
		}

		String doNotHave = doNotHaveQuery.getText();
		if (!doNotHave.isEmpty()) {
			query.append(' ').append("-body:(" + doNotHave + ")");
		}

		if (attachements.getValue()) {
			query.append(' ').append("has:attachment");
		}

		String dateOf = dateOfQuery.getText();
		if (!dateOf.isEmpty()) {
			String dateWithin = dateWithinQuery.getItemText(dateWithinQuery
					.getSelectedIndex());

			Timestamp ts = null;

			if (dateOf.toLowerCase().equals(I18N.strings.today().toLowerCase())) {
				GWT.log("date format : today", null);
				ts = new Timestamp(new Date().getTime());
			} else {
				DateTimeFormat dtf = null;
				Timestamp tsTmp = null;

				for (String format : ACCEPTED_FORMAT) {
					dtf = DateTimeFormat.getFormat(format);
					try {
						tsTmp = new Timestamp(dtf.parse(dateOf).getTime());
						if (tsTmp != null) {
							ui.log("date format : " + format);
							ts = tsTmp;
							break;
						}
					} catch (Exception e) {

					}
				}

			}

			if (ts != null) {
				long before = 0;
				long after = 0;
				if (dateWithin.equals(ONE_DAY)) {
					before = ts.getTime() + ONE_DAY_MS;
					after = ts.getTime() - ONE_DAY_MS;
				} else if (dateWithin.equals(THREE_DAYS)) {
					before = ts.getTime() + ONE_DAY_MS * 3;
					after = ts.getTime() - ONE_DAY_MS * 3;
				} else if (dateWithin.equals(ONE_WEEK)) {
					before = ts.getTime() + ONE_WEEK_MS;
					after = ts.getTime() - ONE_WEEK_MS;
				} else if (dateWithin.equals(TWO_WEEKS)) {
					before = ts.getTime() + ONE_WEEK_MS * 2;
					after = ts.getTime() - ONE_WEEK_MS * 2;
				} else if (dateWithin.equals(ONE_MONTH)) {
					before = ts.getTime() + ONE_MONTH_MS;
					after = ts.getTime() - ONE_MONTH_MS;
				} else if (dateWithin.equals(TWO_MONTHS)) {
					before = ts.getTime() + ONE_MONTH_MS * 2;
					after = ts.getTime() - ONE_MONTH_MS * 2;
				} else if (dateWithin.equals(SIX_MONTHS)) {
					before = ts.getTime() + ONE_MONTH_MS * 6;
					after = ts.getTime() - ONE_MONTH_MS * 6;
				} else if (dateWithin.equals(ONE_YEAR)) {
					before = ts.getTime() + ONE_YEAR_MS;
					after = ts.getTime() - ONE_YEAR_MS;
				}

				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd");
				String date = "before:" + dtf.format(new Date(before))
						+ " after:" + dtf.format(new Date(after));
				query.append(' ').append(date);
			}
		}

		String in = folderQuery.getValue(folderQuery.getSelectedIndex());
		if (!in.isEmpty()) {
			query.append(' ').append(in);
		}

		if (query.toString() != null && query.length() > 0) {
			ui.log("Searching for '" + query.toString() + "'...");
		} else {
			ui.log("Empty query ...");
			ui.notifyUser(I18N.strings.invalidSearchQuery());
		}

		WebmailController.get().getSelector().addSearchFolder(
				query.toString().trim());
		ui.setQuery(query.toString().trim());

	}

	private void hideOptions() {
		GWT.log("Hide search options", null);
		ui.getToolbar().getSearchBox().setVisible(true);
		// #786, Just for you Michel !
		// ui.getToolbar().getSearchBox().setSearchMailQuery("");
		ui.getToolbar().getAdvancedSearchBox().setVisible(false);
	}

	public void folderSelected(Folder f) {
	}

	public void foldersChanged(Folder[] folders) {
		this.folders = folders;
		buildSelectBox();
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
	}

	private int depth(String dName) {
		int i = 0;
		while (dName.charAt(i) == ' ') {
			i++;
		}
		return i;
	}

	private void buildSelectBox() {
		folderQuery.clear();

		folderQuery.addItem(I18N.strings.allMail(), "-in:trash");

		for (Folder f : folders) {
			GWT.log("webmailctrl: " + WebmailController.get() + " f: " + f,
					null);
			String displayName = WebmailController.get().displayName(f);
			folderQuery.addItem(displayName, "in:\""
					+ f.getDisplayName().trim() + "\"");
		}

		folderQuery.addItem("----");
		folderQuery.addItem(I18N.strings.mailAndTrash(I18N.strings.trash()),
				"in:anywhere");
		folderQuery.addItem("----");
		folderQuery.addItem(I18N.strings.readMail(), "is:read");
		folderQuery.addItem(I18N.strings.unreadMail(), "is:unread");
		folderQuery.addItem("----");

		Element e = folderQuery.getElement();
		NodeList<com.google.gwt.dom.client.Element> toStyle = e
				.getElementsByTagName("option");
		// Disable separator items
		toStyle.getItem(folderQuery.getItemCount() - 6).setAttribute(
				"disabled", "disabled");
		toStyle.getItem(folderQuery.getItemCount() - 4).setAttribute(
				"disabled", "disabled");
		toStyle.getItem(folderQuery.getItemCount() - 1).setAttribute(
				"disabled", "disabled");

		for (int i = 1; i < toStyle.getLength() - 7; i++) {
			com.google.gwt.dom.client.Element opt = toStyle.getItem(i);
			Folder f = folders[i - 1];
			int margin = 10 * depth(f.getDisplayName());
			opt.setAttribute("style", "margin-left: " + margin + "px");
			fe.put(f, opt);
		}
	}

}