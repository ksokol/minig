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

package fr.aliasource.webmail.client.reader;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.EmailAddress;

public class DispositionNotificationPanel extends HorizontalPanel {

	private ClientMessage cm;

	private Anchor accept;
	private Anchor later;
	private Anchor refuse;

	public DispositionNotificationPanel(View ui, ClientMessage cm) {
		this.cm = cm;
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(buildMessage());
		accept = new Anchor(I18N.strings.dispositionNotificationAccept());
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				accept();
			}
		});
		panel.add(accept);
		later = new Anchor(I18N.strings.dispositionNotificationLater());
		later.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				later();
			}
		});
		panel.add(later);
		refuse = new Anchor(I18N.strings.dispositionNotificationRefuse());
		refuse.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				refuse();
			}
		});
		panel.add(refuse);
		panel.addStyleName("content");
		this.add(panel);
		this.setVisible(true);
		this.addStyleName("dispositionNotificationPanel");
	}

	protected void refuse() {
		AjaxCall.dispositionNotification.declineNotification(cm.getUid(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void v) {
				setVisible(false);
			}
			
			@Override
			public void onFailure(Throwable t) {
				GWT.log("notification failed", t);
			}
		});
	}

	protected void later() {
		setVisible(false);
	}

	protected void accept() {
		AjaxCall.dispositionNotification.sendNotification(cm.getUid(), cm.getFolderName(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void v) {
				setVisible(false);
			}
			
			@Override
			public void onFailure(Throwable t) {
				GWT.log("notification failed", t);
			}
		});
	}

	private Label buildMessage() {
		List<EmailAddress> dispositionNotification = cm.getDispositionNotification();
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<EmailAddress> iterator = dispositionNotification.iterator();
		while (iterator.hasNext()) {
			stringBuilder.append(iterator.next().getDisplay());
			if (iterator.hasNext()) {
				stringBuilder.append(", ");
			}
		}
		String recipients = stringBuilder.toString();
		if (dispositionNotification.size() > 1) {
			return new Label(I18N.strings.dispositionNotificationMessagePlural(recipients));
		} else {
			return new Label(I18N.strings.dispositionNotificationMessage(recipients));
		}
	}
	
}
