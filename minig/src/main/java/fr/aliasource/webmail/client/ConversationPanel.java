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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.composer.MailComposer;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.reader.ConversationDisplay;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;

/**
 * Widget handling the switch between conversation list and reader display
 * 
 * @author tom
 * 
 */
public class ConversationPanel extends VerticalPanel {

    private View ui;
    private ConversationListPanel listPanel;
    private ConversationDisplay convPanel;

    public ConversationPanel(View ui) {
        super();
        this.ui = ui;
        listPanel = new ConversationListPanel(this, ui);
    }

    private void setContent(Widget w) {
        clear();
        add(w);
    }

    public void showConversations(IClientMessageList convs, int page) {
        setContent(listPanel);
        if (convPanel != null) {
            convPanel.shutdown();
            convPanel = null;
        }
        listPanel.showConversations(convs, page);
    }

    public void showConversation(String convId, int page) {
        ui.getSpinner().startSpinning();

        if (convPanel != null) {
            convPanel.shutdown();
        }
        convPanel = new ConversationDisplay(ui, listPanel, page);
        setContent(convPanel);

        Ajax<IClientMessage> builder = AjaxFactory.fetchMessage(convId);

        try {
            builder.send(new AjaxCallback<IClientMessage>() {

                @Override
                public void onSuccess(IClientMessage object) {
                    ui.getSpinner().stopSpinning();
                    convPanel.setConversationContent(object);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    ui.getSpinner().stopSpinning();
                    if (exception != null) {
                        ui.notifyUser(exception.getMessage());
                    } else {
                        ui.notifyUser("something went wrong");
                    }
                }
            });
        } catch (RequestException e) {
            ui.getSpinner().stopSpinning();
            ui.notifyUser(e.getMessage());
        }
    }

    public void clearTimers() {
        listPanel.clearTimers();
        if (convPanel != null) {
            convPanel.shutdown();
        }
    }

    public void showComposer(final String messageId) {
        Ajax<IClientMessage> builder = AjaxFactory.fetchMessage(messageId);

        try {
            builder.send(new AjaxCallback<IClientMessage>() {

                @Override
                public void onSuccess(IClientMessage object) {
                    ui.getSpinner().stopSpinning();

                    if (ui.getCurrentTab() != View.COMPOSER) {
                        ui.selectTab(View.COMPOSER);
                    }

                    MailComposer mp = ui.getComposer();
                    mp.loadDraft(object);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    ui.getSpinner().stopSpinning();
                    if (exception != null) {
                        ui.notifyUser(exception.getMessage());
                    } else {
                        ui.notifyUser("something went wrong");
                    }
                }
            });
        } catch (RequestException e) {
            ui.getSpinner().stopSpinning();
            ui.notifyUser(e.getMessage());
        }
    }
}
