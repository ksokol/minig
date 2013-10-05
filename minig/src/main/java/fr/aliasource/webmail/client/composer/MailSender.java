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

package fr.aliasource.webmail.client.composer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SubmissionRequest;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * Handles the mail sending process in the composer
 * 
 * @author tom
 * 
 */
public class MailSender {

    private View ui;
    private MailComposer mc;

    public MailSender(View ui, MailComposer mc) {
        this.ui = ui;
        this.mc = mc;
    }

    public void forward(final IClientMessage cm, String forwardId, final TailCall tc) {
        if (!isValidMessage(cm)) {
            return;
        }

        ui.getSpinner().startSpinning();

        Ajax<IClientMessage> builder = AjaxFactory.forwardMessage(forwardId);

        try {
            builder.send(cm, new AjaxCallback<IClientMessage>() {

                @Override
                public void onSuccess(IClientMessage object) {
                    ui.getSpinner().stopSpinning();

                    mc.clearComposer();
                    tc.run();
                    ui.selectTab(View.CONVERSATIONS);

                    SentMailNotification smn = new SentMailNotification(ui);
                    ui.notifyUser(smn, 20);
                    IFolder folderName = WebmailController.get().getSelector().getCurrent();
                    ui.setCurrentFolder(folderName);

                    ui.fetchMessages(folderName.getId(), 1);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    ui.getSpinner().stopSpinning();

                    if (exception != null) {
                        ui.notifyUser(exception.getMessage());
                    } else {
                        ui.notifyUser("something goes wrong.");
                    }
                }
            });
        } catch (RequestException e) {
            ui.getSpinner().stopSpinning();
            ui.notifyUser("sendMessage failure: " + e.getMessage());
        }
    }

    public void sendMessage(final IClientMessage cm, ReplyInfo ri, final TailCall tc) {
        if (!isValidMessage(cm)) {
            return;
        }

        ui.getSpinner().startSpinning();

        SubmissionRequest submissionRequest = BeanFactory.instance.clientMessageSend().as();
        submissionRequest.setClientMessage(cm);

        if (ri != null) {
            submissionRequest.setReplyTo(ri.getId());
        }

        Ajax<SubmissionRequest> builder = AjaxFactory.submissionRequest();

        try {
            builder.send(submissionRequest, (new AjaxCallback<SubmissionRequest>() {

                @Override
                public void onSuccess(SubmissionRequest object) {
                    ui.getSpinner().stopSpinning();

                    mc.clearComposer();
                    tc.run();
                    ui.selectTab(View.CONVERSATIONS);

                    SentMailNotification smn = new SentMailNotification(ui);
                    ui.notifyUser(smn, 20);
                    IFolder folderName = WebmailController.get().getSelector().getCurrent();
                    ui.setCurrentFolder(folderName);

                    ui.fetchMessages(folderName.getId(), 1);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    ui.getSpinner().stopSpinning();

                    if (exception != null) {
                        ui.notifyUser(exception.getMessage());
                    } else {
                        ui.notifyUser("something goes wrong.");
                    }
                }
            }));
        } catch (RequestException e) {
            ui.getSpinner().stopSpinning();
            ui.notifyUser("sendMessage failure: " + e.getMessage());
        }
    }

    private boolean e(String s) {
        return s == null || s.trim().length() == 0;
    }

    private boolean isValidMessage(IClientMessage cm) {
        if (e(cm.getSubject())) {
            ui.notifyUser(I18N.strings.emptySubject());
            return false;
        }
        if (cm.getTo() == null || cm.getTo().isEmpty()) {
            ui.notifyUser(I18N.strings.emptyRecipient());
            return false;
        }

        return true;
    }
}
