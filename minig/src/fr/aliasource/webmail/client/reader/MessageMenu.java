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
 *   minig.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package fr.aliasource.webmail.client.reader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.composer.MenuButton;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.book.UiEmail;

public class MessageMenu extends MenuButton {

	private ConversationDisplay convDisp;
	private ClientMessage cm;

	public MessageMenu(ClientMessage cliM, ConversationDisplay mw) {
		super(new Image(ReaderImages.imgs.dropDown()), PopupOrientation.DownLeft);
		this.convDisp = mw;
		this.cm = cliM;
		final FlexTable ft = new FlexTable();
		int row = 0;
		ft.setWidget(row++, 0, getPrint());
		ft.setWidget(row++, 0, getExport());
		ft.setWidget(row++, 0, getDelete());

		ContactGroup cg = new ContactGroup("all", "All");
		AjaxCall.contacts.getContacts(cg, cm.getSender().getEmail(),
				new AsyncCallback<UiContact[]>() {
					@Override
					public void onFailure(Throwable arg0) {
					}

					@Override
					public void onSuccess(UiContact[] contacts) {
						GWT.log("found " + contacts.length
								+ " contacts matching "
								+ cm.getSender().getEmail());
						if (contacts.length == 0) {
							ft.setWidget(ft.getRowCount(), 0, getAddContact());
						}
					}
				});

		pp.add(ft);
	}

	private Anchor getPrint() {
		Anchor ret = new Anchor(I18N.strings.printOne());
		ret.setTarget("_blank");
		String url = "export/conv/"
				+ URL.encode(cm.getConvId() + "|" + cm.getUid()) + ".html";
		ret.setHref(url);

		ret.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pp.hide();
				setDown(false);
			}
		});
		return ret;
	}

	private Anchor getExport() {
		Anchor ret = new Anchor(I18N.strings.exportOne());
		ret.setTarget("_blank");
		String url = "export/conv/"
				+ URL.encode(cm.getConvId() + "|" + cm.getUid()) + ".pdf";
		ret.setHref(url);

		ret.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pp.hide();
				setDown(false);
			}
		});
		return ret;
	}

	private Anchor getAddContact() {
		final Anchor ret = new Anchor(I18N.strings.addToContactList(cm
				.getSender().getDisplay()));
		ret.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UiContact c = getContact();
				AjaxCall.contacts.createContact(c, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable arg0) {
						GWT.log("error adding contact", arg0);
					}

					@Override
					public void onSuccess(Void arg0) {
						ret.removeFromParent();
						pp.hide();
						setDown(false);
					}
				});
			}
		});
		return ret;
	}

	private UiContact getContact() {
		EmailAddress m = cm.getSender();
		UiContact contact = new UiContact();
		contact.setLastname(m.getDisplay());
		contact.addEmail("INTERNET;X-OBM-Ref1", new UiEmail(m.getEmail()));
		return contact;
	}

	public Anchor getDelete() {
		Anchor ret = new Anchor(I18N.strings.deleteThisMessage());
		ret.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				convDisp.deleteMessage(cm.getConvId(), cm.getUid());
				pp.hide();
				setDown(false);
			}
		});
		return ret;
	}

}
