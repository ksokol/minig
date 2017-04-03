package org.minig.server;

import org.minig.server.service.impl.helper.mime.Mime4jMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

import static org.minig.MinigConstants.MDN_SENT;

/**
 * @author Kamill Sokol
 */
public class FullMailMessage extends PartialMailMessage {

    private final Mime4jMessage mime4jMessage;

    public FullMailMessage(MimeMessage mimeMessage) {
        super(mimeMessage);
        this.mime4jMessage = new Mime4jMessage(mimeMessage);
    }

    public List<MailMessageAddress> getBcc() {
        return mime4jMessage.getBcc().stream().map(MailMessageAddress::new).collect(Collectors.toList());
    }

    public List<MailMessageAddress> getCc() {
        return mime4jMessage.getCc().stream().map(MailMessageAddress::new).collect(Collectors.toList());
    }

    public List<MailMessageAddress> getTo() {
        return mime4jMessage.getTo().stream().map(MailMessageAddress::new).collect(Collectors.toList());
    }

    public List<MailMessageAddress> getReplyTo() {
        return mime4jMessage.getReplyTo().stream().map(MailMessageAddress::new).collect(Collectors.toList());
    }

    public String getText() {
        return mime4jMessage.getPlain();
    }

    public boolean isHtml() {
        return mime4jMessage.getHtml().length() > 0;
    }

    public List<MailAttachment> getAttachments() {
        return mime4jMessage.getAttachments().stream().map(MailAttachment::new).collect(Collectors.toList());
    }

    public String getForwardedMessageId() throws MessagingException {
        return mime4jMessage.getForwardedMessageId();
    }

    public String getInReplyTo() {
        return mime4jMessage.getInReplyTo();
    }

    public String getReferences() {
        return mime4jMessage.getReferences();
    }

    public boolean isMdnSent() throws MessagingException {
        return hasUserFlag(MDN_SENT);
    }

    public boolean isReceipt() {
        return mime4jMessage.isReturnReceipt();
    }

    public boolean isAskForDispositionNotification() {
        return mime4jMessage.hasDispositionNotifications();
    }

    public boolean isHighPriority() {
        return mime4jMessage.isHighPriority();
    }

    public String getMailer() {
        return mime4jMessage.getUserAgent();
    }

    public List<MailMessageAddress> getDispositionNotification() {
        return mime4jMessage.getDispositionNotificationTo().stream().map(MailMessageAddress::new).collect(Collectors.toList());
    }
}
