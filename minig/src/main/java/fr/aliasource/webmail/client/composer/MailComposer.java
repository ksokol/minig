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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.IAttachmentMetadataList;
import fr.aliasource.webmail.client.shared.IBody;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IEmailAddress;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * Mail compose widget
 * 
 * @author tom
 * 
 */
public class MailComposer extends DockPanel {

    private BodyEditor textArea;
    private RecipientsPanel to;
    private RecipientsPanel cc;
    private RecipientsPanel bcc;
    private SubjectField subject;
    private VerticalPanel enveloppe;
    private String draftConvId;
    private ComposerActions northActions;
    private ComposerActions southActions;
    private Anchor addBccLink;
    private Anchor addCcLink;
    private Anchor editSubjectLink;
    private HorizontalPanel enveloppeActions;

    private AttachmentsPanel attach;

    protected View ui;
    private CheckBox highPriority;
    private CheckBox askForDispositionNotification;
    private CheckBox receipt;

    protected IClientMessage messageToForward;

    public MailComposer(View ui) {
        this.ui = ui;
        setWidth("100%");
        northActions = new ComposerActions(ui, this);
        add(northActions, DockPanel.NORTH);
        setCellHorizontalAlignment(northActions, DockPanel.ALIGN_LEFT);

        enveloppe = new VerticalPanel();
        enveloppeActions = new HorizontalPanel();
        to = new RecipientsPanel(ui, I18N.strings.to() + ": ");
        cc = new RecipientsPanel(ui, I18N.strings.cc() + ": ");
        bcc = new RecipientsPanel(ui, I18N.strings.bcc() + ": ");
        subject = new SubjectField(ui);

        attach = new AttachmentsPanel(this);

        enveloppe.add(to);
        enveloppe.add(cc);
        cc.setVisible(false);
        enveloppe.add(bcc);
        bcc.setVisible(false);
        enveloppe.add(enveloppeActions);
        enveloppe.add(subject);

        enveloppe.add(attach);

        HorizontalPanel sendParams = new HorizontalPanel();
        sendParams.add(new Label());
        highPriority = new CheckBox(I18N.strings.importantMessage());
        sendParams.add(highPriority);
        askForDispositionNotification = new CheckBox(I18N.strings.askForDispositionNotification());
        sendParams.add(askForDispositionNotification);
        receipt = new CheckBox(I18N.strings.receipt());
        sendParams.add(receipt);

        enveloppe.add(sendParams);
        sendParams.setCellVerticalAlignment(highPriority, HasVerticalAlignment.ALIGN_MIDDLE);

        highPriority.setStyleName("enveloppeField");

        enveloppe.setStyleName("enveloppe");

        createEnveloppeActions();

        add(enveloppe, DockPanel.NORTH);

        VerticalPanel vp = createBodyEditor(ui);
        add(vp, DockPanel.CENTER);

        southActions = new ComposerActions(ui, this);
        add(southActions, DockPanel.SOUTH);
        setCellHorizontalAlignment(southActions, DockPanel.ALIGN_LEFT);

        attach.registerUploadListener(northActions);
        attach.registerUploadListener(southActions);

        addTabPanelListener();
        focusTo();
        northActions.getSaveNowButton().setEnabled(true);
        southActions.getSaveNowButton().setEnabled(true);
        addWindowResizeHandler();
    }

    private void createEnveloppeActions() {
        Label l = new Label("");
        enveloppeActions.add(l);
        HorizontalPanel hp = new HorizontalPanel();
        hp.addStyleName("panelActions");

        addCcLink = new Anchor(I18N.strings.addCc());
        addCcLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent sender) {
                cc.setVisible(true);
                addCcLink.setVisible(false);
            }
        });
        hp.add(addCcLink);
        addBccLink = new Anchor(I18N.strings.addBcc());
        addBccLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent sender) {
                bcc.setVisible(true);
                addBccLink.setVisible(false);
            }
        });
        hp.add(addBccLink);
        editSubjectLink = new Anchor(I18N.strings.editSubject());
        editSubjectLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent sender) {
                subject.setVisible(true);
                editSubjectLink.setVisible(false);
            }
        });
        hp.add(editSubjectLink);
        hp.setSpacing(2);

        enveloppeActions.add(hp);
    }

    protected IClientMessage clearComposer() {
        IClientMessage ret = getMessage();
        subject.clearText();

        textArea.reset();
        to.clearText();
        cc.clearText();
        bcc.clearText();
        attach.reset();
        return ret;
    }

    protected void destroy() {
        textArea.destroy();
    }

    public void mailto(String recip) {
        IClientMessage cm = BeanFactory.instance.clientMessage().as();
        List<IEmailAddress> a = new ArrayList<IEmailAddress>();

        IEmailAddress emailAddress = BeanFactory.instance.emailAddress().as();
        emailAddress.setDisplayName(recip);
        emailAddress.setEmail(recip);

        a.add(emailAddress);
        cm.setTo(a);
        cm.setBody(BeanFactory.instance.body().as());
        cm.setSubject("");
        loadDraft(cm);
    }

    public void loadDraft(IClientMessage cm) {
        draftConvId = cm.getId();
        attach.setMessageId(cm.getId());
        subject.setText(cm.getSubject());
        textArea.update(cm.getBody());
        to.setRecipients(cm.getTo());
        if (cm.getCc() != null && !cm.getCc().isEmpty()) {
            cc.setRecipients(cm.getCc());
            cc.setVisible(true);
            addCcLink.setVisible(false);
        }
        if (cm.getBcc() != null && !cm.getBcc().isEmpty()) {
            bcc.setRecipients(cm.getBcc());
            bcc.setVisible(true);
            addBccLink.setVisible(false);
        }

        if (cm.getAttachments() != null && !cm.getAttachments().isEmpty()) {
            Ajax<IAttachmentMetadataList> builder = AjaxFactory.attachmentMetadataList(cm.getId());

            try {
                builder.send(new AjaxCallback<IAttachmentMetadataList>() {

                    @Override
                    public void onSuccess(IAttachmentMetadataList object) {
                        if (object != null && object.getAttachmentMetadata() != null) {
                            attach.setAttachments(object);
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        if (exception != null) {
                            ui.notifyUser(exception.getMessage());
                        } else {
                            ui.notifyUser("something went wrong");
                        }
                    }
                });
            } catch (RequestException e) {
                ui.notifyUser("something went wrong");
            }
        }

    }

    public void sendMessage(final IQuickReplyListener listener) {
        MailSender ms = new MailSender(ui, this);

        TailCall tc = new TailCall() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.discard();
                }
            }
        };

        if (this.messageToForward == null) {
            ms.sendMessage(getMessage(), getReplyInfo(), tc);
        } else {
            ms.forward(getMessage(), this.messageToForward.getId(), tc);
        }
    }

    protected ReplyInfo getReplyInfo() {
        return null;
    }

    private IClientMessage getMessage() {
        IClientMessage cm = BeanFactory.instance.clientMessage().as();

        List<IEmailAddress> tos = to.getRecipients();

        cm.setId(draftConvId);
        cm.setTo(tos);
        cm.setSubject(subject.getText());
        cm.setHighPriority(highPriority.getValue());
        cm.setAskForDispositionNotification(askForDispositionNotification.getValue());
        cm.setReceipt(receipt.getValue());

        IBody body = BeanFactory.instance.body().as();

        body.setHtml(textArea.getMailBody().getHtml());
        body.setPlain(textArea.getMailBody().getPlain());

        cm.setBody(body);
        cm.setDate(new Date());
        cm.setMailer("MiniG Webmail");

        if (cc != null && cc.getRecipients() != null && !cc.getRecipients().isEmpty()) {
            cm.setCc(cc.getRecipients());
        }
        if (bcc != null && bcc.getRecipients() != null && !bcc.getRecipients().isEmpty()) {
            cm.setBcc(bcc.getRecipients());
        }

        return cm;
    }

    private VerticalPanel createBodyEditor(View ui) {
        VerticalPanel vp = new VerticalPanel();
        textArea = new BodyEditor(this, ui);
        vp.add(textArea);
        vp.setWidth("100%");
        return vp;
    }

    public void takeSnapshotFromDraft(TailCall tc) {
        IClientMessage cm = getMessage();

        if (draftConvId == null) {
            saveDraft(cm, tc);
        } else {
            updateDraft(cm, tc);
        }
    }

    private void saveDraft(IClientMessage cm, final TailCall tc) {
        ui.getSpinner().startSpinning();

        Ajax<IClientMessage> saveDrafMessage = AjaxFactory.saveDraftMessage();

        try {
            saveDrafMessage.send(cm, new AjaxCallback<IClientMessage>() {

                @Override
                public void onSuccess(IClientMessage object) {
                    ui.getSpinner().stopSpinning();
                    ui.notifyUser(I18N.strings.draftSaved());
                    loadDraft(object);

                    if (tc != null) {
                        tc.run();
                    }
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
            ui.notifyUser(e.getMessage());
        }
    }

    private void updateDraft(IClientMessage cm, final TailCall tc) {
        ui.getSpinner().startSpinning();

        Ajax<IClientMessage> updateDrafMessage = AjaxFactory.updateDraftMessage(draftConvId);

        try {
            updateDrafMessage.send(cm, new AjaxCallback<IClientMessage>() {

                @Override
                public void onSuccess(IClientMessage object) {
                    ui.getSpinner().stopSpinning();
                    ui.notifyUser(I18N.strings.draftSaved());
                    loadDraft(object);

                    if (tc != null) {
                        tc.run();
                    }
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
            ui.notifyUser(e.getMessage());
        }
    }

    protected ClickHandler undoDiscardListener(final IClientMessage cm, final Widget notification, final boolean switchTab) {
        return new ClickHandler() {
            public void onClick(ClickEvent sender) {
                ui.clearNotification(notification);
                if (switchTab) {
                    ui.selectTab(View.COMPOSER);
                }
                loadDraft(cm);
            }
        };
    }

    public void discard() {
        GWT.log("composer.discard() called.", null);
        discard(true);
    }

    protected void discard(boolean switchTab) {
        clearComposer();
        if (switchTab) {
            ui.selectTab(View.CONVERSATIONS);
        }
    }

    public void focusComposer() {
        // QuickReply mode
        draftConvId = null;
        subject.setVisible(false);
        editSubjectLink.setVisible(true);
        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                textArea.focus();
            }
        });
    }

    public void focusTo() {
        // Composer mode
        draftConvId = null;
        cc.setVisible(false);
        addCcLink.setVisible(true);
        bcc.setVisible(false);
        addBccLink.setVisible(true);
        subject.setVisible(true);
        editSubjectLink.setVisible(false);
        to.focus();
    }

    protected void addWindowResizeHandler() {
        Window.addResizeHandler(textArea.getResizeListener());
    }

    private boolean emptyString(String s) {
        return s == null || s.length() == 0;
    }

    protected boolean isEmpty() {
        return emptyString(subject.getText()) && textArea.isEmpty() && to.getRecipients().isEmpty() && cc.getRecipients().isEmpty()
                && bcc.getRecipients().isEmpty() && attach.isEmpty();
    }

    /**
     * Creates listeners on the webmail tabpanel, for exemple to prevent tab
     * switch when composing an email
     */
    protected void addTabPanelListener() {
        ComposerTabListener ctl = new ComposerTabListener(this, ui);
        ui.getTabPanel().addBeforeSelectionHandler(ctl);
        ui.getTabPanel().addSelectionHandler(ctl);
    }

    public void resize() {
        int height = Window.getClientHeight();
        textArea.resize(height);
    }

    public void setMessageId(String id) {
        this.draftConvId = id;
    }

}
