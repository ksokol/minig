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

import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IFolderSelectionListener;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.QuotaInfo;

/**
 * Action buttons at the top of the conversation list. Controls page switching.
 * 
 * @author tom
 * 
 */
public class ConversationListActionsPanel extends DockPanel implements
		IConversationSelectionChangedListener, IFolderSelectionListener {

	public enum Position {
		North,
		South
	}
	
	private Button newest;
	private Button newer;
	private Label countLabel;
	private Button older;
	private Button oldest;
	private Button delete;
	private Button reportSpam;
	private Button notSpam;
	private HorizontalPanel actions;
	private ConversationFolderQuota convFolderQuota;
	private MoreActionMenu moreActions;
	private View ui;
	private MoveConversationsMenu moveToButton;
	private MoveConversationsMenu copyToButton;
	private final Position position;

	public ConversationListActionsPanel(ConversationListPanel clp, View ui, Position position) {
		this.ui = ui;
		this.position = position;
		initConvToolbar(this, clp);
		WebmailController.get().getSelector().addListener(this);
	}

	private void initConvToolbar(DockPanel convToolbar,
			final ConversationListPanel clp) {

		DockPanel leftActions = new DockPanel();

		actions = new HorizontalPanel();
		delete = new Button(I18N.strings.delete());
		delete.addStyleName("deleteButton");
		delete.setEnabled(false);
		actions.add(delete);
		actions.setCellVerticalAlignment(delete, HorizontalPanel.ALIGN_MIDDLE);
		delete.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				clp.deleteConversation();
			}
		});

		// FIXME: merge reportSpam and notSpam in spamActions
		reportSpam = new Button(I18N.strings.markAsSpam());
		reportSpam.addStyleName("noWrap");
		reportSpam.setEnabled(false);
		actions.add(reportSpam);
		actions.setCellVerticalAlignment(reportSpam,
				HorizontalPanel.ALIGN_MIDDLE);
		reportSpam.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.moveConversation(WebmailController.get().getSelector()
						.getCurrent(), new Folder(WebmailController.get()
						.getSetting(GetSettings.SPAM_FOLDER)), true, null);
			}
		});

		notSpam = new Button(I18N.strings.notSpam());
		notSpam.addStyleName("noWrap");
		notSpam.setEnabled(false);
		notSpam.setVisible(false);

		actions.add(notSpam);
		actions.setCellVerticalAlignment(notSpam, HorizontalPanel.ALIGN_MIDDLE);

		notSpam.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.moveConversation(WebmailController.get().getSelector()
						.getCurrent(), new Folder("INBOX"), true, null);
			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(0);
		moveToButton = new MoveConversationsMenu(I18N.strings.moveTo(), clp,
				true, position);
		hp.add(moveToButton);
		copyToButton = new MoveConversationsMenu(I18N.strings.copyTo(), clp,
				false, position);
		hp.add(copyToButton);

		actions.add(hp);
		actions.setCellVerticalAlignment(hp, HasVerticalAlignment.ALIGN_MIDDLE);

		moreActions = new MoreActionMenu(I18N.strings.moreActions(), clp, position);
		actions.add(moreActions);
		actions.setCellVerticalAlignment(moreActions,
				HorizontalPanel.ALIGN_MIDDLE);

		actions.setSpacing(3);

		actions.addStyleName("actionBox");

		leftActions.add(actions, DockPanel.NORTH);

		HorizontalPanel selection = new HorizontalPanel();
		selection.addStyleName("panelActions");
		selection.add(new Label(I18N.strings.select() + ": "));

		createSelectors(clp, selection);
		selection.addStyleName("selectionBox");
		leftActions.add(selection, DockPanel.SOUTH);

		convToolbar.add(leftActions, DockPanel.WEST);

		newest = new Button("« " + I18N.strings.newest());
		newest.addStyleName("noWrap");
		newest.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.showPage(1);
			}
		});
		newer = new Button("‹ " + I18N.strings.newer());
		newer.addStyleName("noWrap");
		newer.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.showPage(clp.getCurrentPage() - 1);
			}
		});

		countLabel = new Label();
		countLabel.addStyleName("noWrap");
		older = new Button(I18N.strings.older() + " ›");
		older.addStyleName("noWrap");
		older.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.showPage(clp.getCurrentPage() + 1);
			}
		});

		oldest = new Button(I18N.strings.oldest() + " »");
		oldest.addStyleName("noWrap");
		oldest.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.showPage(clp.getLastPage());
			}
		});

		VerticalPanel rightVrt = new VerticalPanel();

		HorizontalPanel right = new HorizontalPanel();
		right.add(newest);
		right.add(newer);
		right.add(countLabel);
		right
				.setCellVerticalAlignment(countLabel,
						HorizontalPanel.ALIGN_MIDDLE);
		right.add(older);
		right.add(oldest);
		right.setSpacing(3);

		rightVrt.add(right);
		convFolderQuota = new ConversationFolderQuota(this.ui);
		rightVrt.add(convFolderQuota);
		rightVrt.setHorizontalAlignment(ALIGN_RIGHT);
		rightVrt.setCellHorizontalAlignment(convFolderQuota,
				VerticalPanel.ALIGN_RIGHT);

		right.setCellVerticalAlignment(right, VerticalPanel.ALIGN_MIDDLE);

		convToolbar.add(rightVrt, DockPanel.EAST);
		convToolbar.setCellHorizontalAlignment(rightVrt, DockPanel.ALIGN_RIGHT);

		newest.setVisible(false);
		newer.setVisible(false);
		older.setVisible(false);
		oldest.setVisible(false);
	}

	private void createSelectors(final ConversationListPanel clp,
			HorizontalPanel selection) {
		Anchor all = new Anchor(I18N.strings.all());
		all.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.selectAll();
			}
		});
		selection.add(all);

		Anchor read = new Anchor(I18N.strings.read());
		read.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.selectRead();
			}
		});
		selection.add(read);

		Anchor unread = new Anchor(I18N.strings.unread());
		unread.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.selectUnread();
			}
		});
		selection.add(unread);

		Anchor none = new Anchor(I18N.strings.none());
		none.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				clp.selectNone();
			}
		});
		selection.add(none);

		selection.setSpacing(3);
	}

	public void updateButtonStates(int dLen, int lastPage, int currentPage) {
		if (currentPage == 1) {
			newer.setVisible(false);
			newest.setVisible(false);
			older.setVisible(true);
			oldest.setVisible(true);
		} else if (currentPage < lastPage) {
			newer.setVisible(true);
			newest.setVisible(true);
			older.setVisible(true);
			oldest.setVisible(true);
		}

		if (currentPage >= lastPage) {
			if (currentPage != 1) {
				newer.setVisible(true);
				newest.setVisible(true);
			}

			older.setVisible(false);
			oldest.setVisible(false);
		}
	}

	public void setCountLabel(String string) {
		countLabel.setText(string);
	}

	public void selectionChanged(Set<ConversationId> selectedIds) {
		delete.setEnabled(!selectedIds.isEmpty());
		reportSpam.setEnabled(!selectedIds.isEmpty());
		notSpam.setEnabled(!selectedIds.isEmpty());
		moreActions.setSelection(selectedIds);
		moveToButton.setSelection(selectedIds);
		copyToButton.setSelection(selectedIds);
	}

	public void selectionChanged(String str) {
		moreActions.setSelection(str);
		moveToButton.setSelection(str);
		copyToButton.setSelection(str);
	}

	public void folderSelected(Folder f) {
		String folderName = f.getName();
		String trash = WebmailController.get().getSetting(
				GetSettings.TRASH_FOLDER);
		String spam = WebmailController.get().getSetting(
				GetSettings.SPAM_FOLDER);
		String draft = WebmailController.get().getSetting(
				GetSettings.DRAFTS_FOLDER);
		String template = WebmailController.get().getSetting(
				GetSettings.TEMPLATES_FOLDER);
		String sent = WebmailController.get().getSetting(
				GetSettings.SENT_FOLDER);
		if (folderName.equals(trash) || folderName.equals(spam)) {
			delete.setText(I18N.strings.deleteForever());
		} else {
			delete.setText(I18N.strings.delete());
		}

		if (folderName.equals(trash) || folderName.equals(spam)
				|| folderName.equals(sent) || folderName.equals(template)
				|| folderName.equals(draft)) {
			reportSpam.setVisible(false);
		} else {
			reportSpam.setVisible(true);
		}

		notSpam.setVisible(folderName.equals(spam));

	}

	public void foldersChanged(Folder[] folders) {
	}

	public void unreadCountChanged(CloudyFolder cloudyFolder) {
	}

	public void updateQuotaBar(String mailBox, QuotaInfo quotaInfo) {
		convFolderQuota.updateBar(mailBox, quotaInfo);
	}
}
