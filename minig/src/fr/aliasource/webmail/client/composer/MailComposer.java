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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.shared.SendParameters;

/**
 * Mail compose widget
 * 
 * @author tom
 * 
 */
public class MailComposer extends DockPanel {

	private BodyEditor textArea;
	private IdentitiesPanel identities;
	private RecipientsPanel to;
	private RecipientsPanel cc;
	private RecipientsPanel bcc;
	private SubjectField subject;
	private VerticalPanel enveloppe;
	private ConversationId draftConvId;
	private Timer timer;
	private boolean timerStarted;
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

		if (WebmailController.get().getSetting("identities/nb_identities") != null) {
			if (identities == null && WebmailController.get().hasIdentities()) {
				identities = new IdentitiesPanel();
				enveloppe.add(identities);
			}
		}

		attach = new AttachmentsPanel();
		// crp = new CannedResponsePanel(ui, this);

		enveloppe.add(to);
		enveloppe.add(cc);
		cc.setVisible(false);
		enveloppe.add(bcc);
		bcc.setVisible(false);
		enveloppe.add(enveloppeActions);
		enveloppe.add(subject);

		// enveloppe.add(crp);
		enveloppe.add(attach);

		HorizontalPanel sendParams = new HorizontalPanel();
		sendParams.add(new Label());
		highPriority = new CheckBox(I18N.strings.importantMessage());
		sendParams.add(highPriority);
		askForDispositionNotification = new CheckBox(I18N.strings.askForDispositionNotification());
		sendParams.add(askForDispositionNotification);
		enveloppe.add(sendParams);
		sendParams.setCellVerticalAlignment(highPriority,
				HasVerticalAlignment.ALIGN_MIDDLE);
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
		setTimerStarted(false);
		setEnableSaveButtons(false);
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

	protected ClientMessage clearComposer() {
		ClientMessage ret = getMessage();
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
		ClientMessage cm = new ClientMessage();
		List<EmailAddress> a = Arrays.asList(new EmailAddress(recip, recip));
		cm.setTo(a);
		cm.setBody(new Body());
		cm.setSubject("");
		loadDraft(cm, null);
	}

	public void loadDraft(ClientMessage cm, ConversationId convId) {
		loadDraft(cm, convId, true);
	}
	
	public void loadDraft(ClientMessage cm, ConversationId convId, boolean withSignature) {
		if (identities != null && cm.getSender() != null) {
			EmailAddress sender = cm.getSender();
			identities.getIdentititesSelectionBox().setSelectedAddress(sender);
		}

		draftConvId = convId;
		subject.setText(cm.getSubject());
		textArea.update(cm.getBody(), withSignature);
		to.setRecipients(cm.getTo());
		if (!cm.getCc().isEmpty()) {
			cc.setRecipients(cm.getCc());
			cc.setVisible(true);
			addCcLink.setVisible(false);
		}
		if (!cm.getBcc().isEmpty()) {
			bcc.setRecipients(cm.getBcc());
			bcc.setVisible(true);
			addBccLink.setVisible(false);
		}
		if (cm.getAttachments() != null && cm.getAttachments().length > 0) {
			GWT.log("should display " + cm.getAttachments().length
					+ " attachments", null);
			for (String attachId : cm.getAttachments()) {
				attach.showAttach(attachId);
			}
		}

		ui.getSidebar().setCurrentDefaultLinkStyle(
				ui.getSidebar().defaultLinks.get(I18N.strings.compose()));
	}

	public void sendMessage(final IQuickReplyListener listener) {
		stopAutoSaveDraftTimer();
		MailSender ms = new MailSender(ui, this);
		TailCall tc = new TailCall() {

			@Override
			public void run() {
				GWT.log("sendMessage(quickRListener)", null);
				if (listener != null) {
					listener.discard();
				}
				if (draftConvId != null) {
					AjaxCall.store.deleteConversation(Arrays.asList(draftConvId), 
							removeDraftCallback(draftConvId, null));
				} else {
					GWT.log("draftConvId is null, not removing", null);
				}
			}
		};
		ms.sendMessage(getMessage(), getReplyInfo(), sendParams(), tc);
	}

	private SendParameters sendParams() {
		SendParameters sp = new SendParameters();
		sp.setSendPlainText(textArea.shouldSendInPlain());
		sp.setHighPriority(highPriority.getValue());
		sp.setAskForDispositionNotification(askForDispositionNotification.getValue());
		return sp;
	}

	protected ReplyInfo getReplyInfo() {
		return null;
	}

	private ClientMessage getMessage() {
		EmailAddress sender = null;
		if (WebmailController.get().hasIdentities()) {
			sender = identities.getIdentititesSelectionBox()
					.getSelectedAddress();
		} else {
			sender = WebmailController.get().getIdentity();
		}
		List<EmailAddress> tos = to.getRecipients();

		ClientMessage cm = new ClientMessage(sender, tos, subject.getText(),
				textArea.getMailBody(), attach.getAttachementIds(), new Date(),
				"MiniG Webmail", null);
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

	public void saveDraft() {
		// FIXME : stupid code

		final ConversationId prevConvId = draftConvId;
		ui.getSpinner().startSpinning();
		if (prevConvId != null) {
			TailCall tc = new TailCall() {
				public void run() {
					AjaxCall.store.storeDraftMessage(getMessage(), sendParams(), saveDraftCallback());
				}
			};
			AjaxCall.store.deleteConversation(Arrays.asList(prevConvId), removeDraftCallback(prevConvId, tc));
		} else {
			AjaxCall.store.storeDraftMessage(getMessage(), sendParams(), saveDraftCallback());
		}
	}

	public void saveTemplate() {
		ui.getSpinner().startSpinning();
		AjaxCall.store.storeTemplateMessage(getMessage(), sendParams(), saveTemplateCallback());
	}

	private AsyncCallback<Void> removeDraftCallback(final ConversationId convId,
			final TailCall tc) {
		return new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				ui.log("Failed to remove draft.");
				ui.getSpinner().stopSpinning();
			}

			public void onSuccess(Void result) {
				ui.log("Previous draft conversation removed : " + convId);
				if (tc != null) {
					tc.run();
				}
			}
		};
	}

	private AsyncCallback<ConversationId> saveDraftCallback() {
		return new AsyncCallback<ConversationId>() {
			public void onFailure(Throwable caught) {
				ui.notifyUser(I18N.strings.failedToSaveDraft());
				ui.getSpinner().stopSpinning();
			}

			public void onSuccess(ConversationId result) {
				ui.notifyUser(I18N.strings.draftSaved());
				draftConvId = result;
				ui.getSpinner().stopSpinning();
				stopAutoSaveDraftTimer();
				setEnableSaveButtons(false);
			}
		};
	}

	private AsyncCallback<ConversationId> saveTemplateCallback() {
		return new AsyncCallback<ConversationId>() {
			public void onFailure(Throwable caught) {
				ui.notifyUser(I18N.strings.failedToSaveTemplate());
				ui.getSpinner().stopSpinning();
			}

			public void onSuccess(ConversationId result) {
				ui.notifyUser(I18N.strings.templateSaved());
				stopAutoSaveDraftTimer();
				setEnableSaveButtons(false);
				ui.getSpinner().stopSpinning();
			}
		};
	}

	protected ClickHandler undoDiscardListener(final ClientMessage cm,
			final Widget notification, final boolean switchTab) {
		return new ClickHandler() {
			public void onClick(ClickEvent sender) {
				ui.clearNotification(notification);
				if (switchTab) {
					ui.selectTab(View.COMPOSER);
				}
				loadDraft(cm, null);
			}
		};
	}

	public void discard() {
		GWT.log("composer.discard() called.", null);
		discard(true);
	}

	protected void discard(boolean switchTab) {
		final ClientMessage cm = clearComposer();
		if (switchTab) {
			ui.selectTab(View.CONVERSATIONS);
		}
		notifyUndoDiscard(switchTab, cm);
	}

	private void notifyUndoDiscard(boolean switchTab, final ClientMessage cm) {
		final HorizontalPanel notif = new HorizontalPanel();
		stopAutoSaveDraftTimer();
		notif.setSpacing(2);
		notif.add(new Label(I18N.strings.messageDiscarded()));
		Anchor undo = new Anchor(I18N.strings.undoDiscard());
		undo.addClickHandler(undoDiscardListener(cm, notif, switchTab));
		notif.add(undo);
		ui.notifyUser(notif, 15);
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
		setEnableSaveButtons(false);
		to.focus();
	}

	protected void addWindowResizeHandler() {
		Window.addResizeHandler(textArea.getResizeListener());
	}

	private boolean emptyString(String s) {
		return s == null || s.length() == 0;
	}

	protected boolean isEmpty() {
		return emptyString(subject.getText())
			&& textArea.isEmpty()
			&& to.getRecipients().isEmpty()
			&& cc.getRecipients().isEmpty()
			&& bcc.getRecipients().isEmpty() 
			&& attach.isEmpty();
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

	private void setTimerStarted(boolean timerStarted) {
		this.timerStarted = timerStarted;
	}

	public boolean isTimerStarted() {
		return timerStarted;
	}

	public void startAutoSaveDraftTimer() {
		setTimerStarted(true);
		setEnableSaveButtons(true);
		ui.log("startAutoSaveDraftTimer");
		timer = new Timer() {
			@Override
			public void run() {
				if (!emptyString(textArea.getMailBody().getHtml())) {
					ui.log("auto save draft");
					saveDraft();
					DateFormatter df = new DateFormatter(new Date());
					String date = df.formatSmall(new Date());
					String autosaveddate = I18N.strings.draftAutoSavedAt(date);
					setSavedDate(autosaveddate);
				}
			}
		};
		timer.scheduleRepeating(30000);
	}

	private void stopAutoSaveDraftTimer() {
		GWT.log("stopAutoSaveDraftTimer", null);
		if (timer != null) {
			timer.cancel();
		}
		setTimerStarted(false);
	}

	public void setEnableSaveButtons(boolean b) {
		northActions.getSaveNowButton().setEnabled(b);
		southActions.getSaveNowButton().setEnabled(b);
		northActions.getSaveTemplateButton().setEnabled(b);
		southActions.getSaveTemplateButton().setEnabled(b);
	}

	private void setSavedDate(String s) {
		northActions.getSavedDate().setText(s);
		southActions.getSavedDate().setText(s);
	}

	public IdentitiesPanel getIdentities() {
		return identities;
	}

	public void resize() {
		int height = Window.getClientHeight();
		textArea.resize(height);
	}

}
