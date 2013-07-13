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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.chat.XmppController;
import fr.aliasource.webmail.client.ctrl.Features;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.shared.CloudyFolder;
import fr.aliasource.webmail.client.shared.Folder;

/**
 * GMail like left side bar, with default links, chat (soon...) & folder access
 * 
 * @author tom
 * 
 */
public class SideBar extends VerticalPanel implements IFolderSelectionListener {

	private View ui;
	private Anchor inbox;
	private Widget prevDefaultLink;
	private VerticalPanel defaultLinksPanel;
	public HashMap<String, Anchor> defaultLinks;
	public Folder previousFolder;
	private LabelsPanel labelsPanel;
	private Widget roster;

	public SideBar(View ui) {
		this.ui = ui;
		defaultLinks = new HashMap<String, Anchor>();
		defaultLinksPanel = new VerticalPanel();
		defaultLinksPanel.setWidth("100%");
		add(defaultLinksPanel);
		add(new HTML("&nbsp;"));

		addLabels();

		if (Features.XMPP) {
			GWT.runAsync(new RunAsyncCallback() {

				@Override
				public void onSuccess() {
					add(new HTML("&nbsp;"));
					addChat();
				}

				@Override
				public void onFailure(Throwable reason) {
				}
			});
		}

		// setSpacing(4);
		addTabPanelListener();
		addStyleName("leftPanel");
		setVerticalAlignment(ALIGN_MIDDLE);
		addDefaultLinks();

		WebmailController.get().getSelector().addListener(this);
	}

	private void addChat() {
		GWT.log("Add chat", null);
		roster = XmppController.get().getView().getRoster();
		DisclosurePanel w = createDisclosurePanel(I18N.strings.chat(), roster);
		w.setOpen(true);
	}

	private DisclosurePanel createDisclosurePanel(String title, Widget widget) {
		DisclosurePanel box = new DisclosurePanel();
		DisclosurePanelHeader hpHeader = new DisclosurePanelHeader(
				title + " :", box);
		box.setHeader(hpHeader);
		box.addStyleName("sidebarBox");
		box.add(widget);
		box.setOpen(false);
		add(box);
		return box;
	}

	protected void addTabPanelListener() {
		SelectionHandler<Integer> tl = new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() != View.CONVERSATIONS) {
					if (prevDefaultLink != null) {
						prevDefaultLink
								.removeStyleName("currentDefaultLinkSelected");
					}
				}
			}

		};
		ui.getTabPanel().addSelectionHandler(tl);
	}

	private void addLabels() {
		labelsPanel = new LabelsPanel();
		DisclosurePanel w = createDisclosurePanel(I18N.strings.folders(),
				labelsPanel);
		w.setOpen(true);
	}

	private void addDefaultLinks() {

		defaultLinksPanel.add(getComposeLink());

		inbox = getInboxLink();
		prevDefaultLink = inbox;
		previousFolder = new Folder(I18N.strings.inbox());
		defaultLinksPanel.add(inbox);
		defaultLinksPanel.add(getUnreadLink());
		defaultLinksPanel.add(getStarredLink());

		Anchor hl = null;

		hl = createDefaultLink(I18N.strings.drafts(), WebmailController.get()
				.getSetting(GetSettings.DRAFTS_FOLDER));
		if (hl != null) {
			defaultLinksPanel.add(hl);
		}

		hl = createDefaultLink(I18N.strings.templates(), WebmailController
				.get().getSetting(GetSettings.TEMPLATES_FOLDER));
		if (hl != null) {
			defaultLinksPanel.add(hl);
		}

		hl = createDefaultLink(I18N.strings.sent(), WebmailController.get()
				.getSetting(GetSettings.SENT_FOLDER));
		if (hl != null) {
			defaultLinksPanel.add(hl);
		}
		hl = createDefaultLink(I18N.strings.spam(), WebmailController.get()
				.getSetting(GetSettings.SPAM_FOLDER));
		if (hl != null) {
			defaultLinksPanel.add(hl);
		}
		hl = createDefaultLink(I18N.strings.trash(), WebmailController.get()
				.getSetting(GetSettings.TRASH_FOLDER));
		if (hl != null) {
			defaultLinksPanel.add(hl);
		}

		defaultLinksPanel.add(new HTML("&nbsp;"));
		if (Features.CALENDAR) {
			defaultLinksPanel.add(getCalendarLink());
		}
		defaultLinksPanel.add(getContactsLink());
	}

	/**
	 * Create a folder link in the sidebar given the complete imap folder name.
	 * 
	 * @param folderName
	 *            the imap folder name, with %d authorized for imap separator.
	 * @return
	 */
	private Anchor createDefaultLink(final String label, final String folderName) {
		if (folderName == null) {
			GWT.log("null folderName, cannot create default link", null);
			return null;
		}

		String fName = folderName;
		int pdLastIdx = folderName.lastIndexOf("%d");
		if (pdLastIdx > 0) {
			fName = fName.substring(pdLastIdx + 2);
		}
		final Anchor hl = new Anchor(label);
		hl.addStyleName("folderLink");
		hl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				WebmailController.get().getSelector().select(
						new Folder(folderName));
				setCurrentDefaultLinkStyle(hl);
			}
		});
		defaultLinks.put(fName.toLowerCase(), hl);
		return hl;
	}

	private Anchor getStarredLink() {
		Anchor starred = new VirtualFolder(ui, this, I18N.strings.starred(),
				"is:starred");
		starred.addStyleName("folderLink");
		defaultLinks.put("search:is:starred", starred);
		return starred;
	}

	private Anchor getUnreadLink() {
		Anchor starred = new VirtualFolder(ui, this, I18N.strings.unread(),
				"is:unread -in:trash");
		starred.setTitle(I18N.strings.unread());
		starred.addStyleName("folderLink");
		defaultLinks.put("search:is:unread -in:trash", starred);
		return starred;
	}

	public Widget getComposeLink() {

		Image img = new Image("minig/images/envelope.png");
		final Anchor hl = new Anchor(I18N.strings.compose());
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (ui.getCurrentTab() != View.COMPOSER) {
					ui.selectTab(View.COMPOSER);
				}
			}
		};
		return getDecorativeButton(img, hl, cl);
	}

	public Anchor getInboxLink() {
		final Anchor hl = new Anchor(I18N.strings.inbox());
		hl.addStyleName("folderLink");
		hl.addStyleName("currentDefaultLinkSelected");
		hl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				WebmailController.get().getSelector().select(
						new Folder("INBOX"));
				setCurrentDefaultLinkStyle(hl);
			}
		});
		defaultLinks.put("inbox", hl);
		return hl;
	}

	private Widget getContactsLink() {
		Image img = new Image("minig/images/contacts.png");

		final Anchor hl = new Anchor(I18N.strings.contacts());
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.selectTab(View.ADDRESSBOOK);
			}
		};
		return getDecorativeButton(img, hl, cl);
	}

	private Widget getCalendarLink() {
		Image img = new Image("minig/images/agenda.png");

		final Anchor hl = new Anchor("Calendar");
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.selectTab(View.CALENDAR);
			}
		};
		return getDecorativeButton(img, hl, cl);
	}

	/**
	 * Create a decorative button
	 * 
	 * @return
	 */
	private Widget getDecorativeButton(Image image, Anchor hl,
			ClickHandler clickListener) {
		Grid hp = new Grid(1, 2);
		hp.setCellPadding(0);
		hp.setCellSpacing(0);
		hp.getCellFormatter().setStyleName(0, 0, "image");
		hp.setWidget(0, 0, image);

		hp.addStyleName("decorativeButton");
		hl.addStyleName("noWrap");
		hl.addStyleName("bold");

		hl.addClickHandler(clickListener);
		hp.setWidget(0, 1, hl);

		return hp;
	}

	public void foldersChanged(Folder[] folders) {
	}

	public void folderSelected(Folder f) {
		if (prevDefaultLink != null) {
			prevDefaultLink.removeStyleName("currentDefaultLinkSelected");
		}
	}

	public void unreadCountChanged(CloudyFolder f) {
		if (defaultLinks.containsKey(f.getName().toLowerCase())) {
			Anchor hl = defaultLinks.get(f.getName().toLowerCase());
			String name = WebmailController.get().displayName(f.getName());
			if (f.getUnreadCount() > 0) {
				if (!hl.getText().contains("(")) {
					hl.addStyleName("bold");
				}
				hl.setText(name + " (" + f.getUnreadCount() + ")");
			} else {
				hl.setText(name);
				hl.removeStyleName("bold");
			}
		}
	}

	public void setCurrentDefaultLinkStyle(Widget current) {
		if (current != null) {
			current.addStyleName("currentDefaultLinkSelected");
			prevDefaultLink = current;
		}
	}
}
