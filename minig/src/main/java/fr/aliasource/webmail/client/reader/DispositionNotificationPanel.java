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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IEmailAddress;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

public class DispositionNotificationPanel extends HorizontalPanel {

    private IClientMessage cm;

    private Anchor accept;
    private Anchor later;

    public DispositionNotificationPanel(View ui, IClientMessage cm) {
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

        panel.addStyleName("content");
        this.add(panel);
        this.setVisible(true);
        this.addStyleName("dispositionNotificationPanel");
    }

    protected void later() {
        setVisible(false);
    }

    protected void accept() {
        Ajax<Void> dispositionRequest = AjaxFactory.dispositionRequest(cm.getId());

        try {
            dispositionRequest.send(new AjaxCallback<Void>() {

                @Override
                public void onSuccess(Void object) {
                    setVisible(false);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    if (exception != null) {
                        WebmailController.get().getView().notifyUser(exception.getMessage());
                    } else {
                        WebmailController.get().getView().notifyUser("something went wrong");
                    }
                }
            });
        } catch (RequestException e) {
            WebmailController.get().getView().notifyUser(e.getMessage());
        }
    }

    private Label buildMessage() {
        List<IEmailAddress> dispositionNotification = cm.getDispositionNotification();
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<IEmailAddress> iterator = dispositionNotification.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().getDisplayName());
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
