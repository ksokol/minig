package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.util.PercentEscaper;
import org.springframework.web.util.UriComponentsBuilder;

import javax.activation.DataSource;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.minig.MinigConstants.BCC;
import static org.minig.MinigConstants.CC;
import static org.minig.MinigConstants.DISPOSITION_NOTIFICATION_TO;
import static org.minig.MinigConstants.FORWARDED_MESSAGE_ID;
import static org.minig.MinigConstants.IN_REPLY_TO;
import static org.minig.MinigConstants.REFERENCES;
import static org.minig.MinigConstants.REPLY_TO;
import static org.minig.MinigConstants.TO;
import static org.minig.MinigConstants.USER_AGENT;
import static org.minig.MinigConstants.X_DRAFT_INFO;
import static org.minig.MinigConstants.X_PRIORITY;
import static org.minig.server.util.ExceptionUtils.rethrowCheckedAsUnchecked;

/**
 * @author Kamill Sokol
 */
public class Mime4jMessage {

    private MessageTransformer messageTransformer;
    private boolean receipt;
    private boolean askForDispositionNotification;

    public Mime4jMessage(CompositeId compositeId) {
        this.messageTransformer = new MessageTransformer(compositeId);
    }

    public Mime4jMessage(javax.mail.Message msg) {
        this.messageTransformer = new MessageTransformer(msg);
    }

    public javax.mail.internet.MimeMessage toMessage() {
        return messageTransformer.toMessage();
    }

    @Deprecated
    public CompositeId getId() {
        return messageTransformer.getCompositeId();
    }

    public List<Mime4jAddress> getTo() {
        return getAddresses(TO);
    }

    public List<Mime4jAddress> getCc() {
        return getAddresses(CC);
    }

    public List<Mime4jAddress> getBcc() {
        return getAddresses(BCC);
    }

    public List<Mime4jAddress> getDispositionNotificationTo() {
        return getAddresses(DISPOSITION_NOTIFICATION_TO);
    }

    public List<Mime4jAddress> getReplyTo() {
        return getAddresses(REPLY_TO);
    }

    public String getPlain() {
        return messageTransformer.getText();
    }

    public String getHtml() {
        return messageTransformer.getHtml();
    }

    public String getHtml(UriComponentsBuilder uriComponentsBuilder) {
        String sanitizedHtmlBody = messageTransformer.getHtml();
        for (Mime4jAttachment attachment : getInlineAttachments()) {
            sanitizedHtmlBody = sanitize(sanitizedHtmlBody, attachment, uriComponentsBuilder.cloneBuilder());
        }
        return sanitizedHtmlBody;
    }

    public void setPlain(String plain) {
        messageTransformer.setText(plain);
    }

    public void setHtml(String html) {
        messageTransformer.setHtml(html);
    }

    public void addAttachment(DataSource dataSource) {
        messageTransformer.addAttachment(dataSource);
    }

    public Optional<Mime4jAttachment> getAttachment(CompositeAttachmentId id) {
        return messageTransformer.getAllAttachments().stream().filter(mime4jAttachment -> mime4jAttachment.getId().equals(id)).findFirst();
    }

    /**
     * @deprecated Use {@link #getAttachment(CompositeAttachmentId)} instead.
     */
    @Deprecated
    public Mime4jAttachment getAttachment(String filename) {
        List<Mime4jAttachment> attachments = getAttachments();
        for (Mime4jAttachment attachment : attachments) {
            if (attachment.getId().getFileName().equals(filename)) {
                return attachment;
            }
        }
        return null;
    }

    public List<Mime4jAttachment> getAttachments() {
        return messageTransformer.getAttachments();
    }

    public void deleteAttachment(String filename) {
        messageTransformer.deleteAttachment(filename);
    }

    public void setFrom(String email) {
        messageTransformer.setFrom(email);
    }

    public void setSubject(String subject) {
        messageTransformer.setSubject(subject);
    }

    public void setDate(Date date) {
        messageTransformer.setDate(date);
    }

    public void addCc(String email, String name) {
        messageTransformer.addCc(email, name);
    }

    public void addBcc(String email, String name) {
        messageTransformer.addBcc(email, name);
    }

    public void addTo(String email) {
        messageTransformer.addTo(email);
    }

    public boolean hasDispositionNotifications() {
        return isDSN() || isReturnReceipt();
    }

    public boolean isDSN() {
        return getDraftInfo().contains("DSN=1");
    }

    public boolean isReturnReceipt() {
        return getDraftInfo().contains("receipt=1");
    }

    public String getSender() {
        return messageTransformer.getFrom();
    }

    public void clearRecipients() {
        messageTransformer.clearTo();
    }

    public void clearCc() {
        messageTransformer.clearCc();
    }

    public void clearBcc() {
        messageTransformer.clearBcc();
    }

    public void addRecipient(String address) {
        messageTransformer.addTo(address);
    }

    public void addCc(String address) {
        messageTransformer.addCc(address, null);
    }

    public void addBcc(String address) {
        messageTransformer.addBcc(address, null);
    }

    public void setAskForDispositionNotification(Boolean askForDispositionNotification) {
        this.askForDispositionNotification = askForDispositionNotification == null ? false : askForDispositionNotification;
        updateDraftInfo();
    }

    public void setHighPriority(Boolean highPriority) {
        if (highPriority != null && highPriority) {
            messageTransformer.setHeader(X_PRIORITY, "1");
            return;
        }
        messageTransformer.removeHeader(X_PRIORITY);
    }

    public void setReceipt(Boolean receipt) {
        this.receipt = receipt == null ? false : receipt;
        updateDraftInfo();
    }

    public String getSubject() {
        return messageTransformer.getSubject();
    }

    public void setInReplyTo(String inReplyTo) {
        messageTransformer.setHeader(IN_REPLY_TO, inReplyTo);
        messageTransformer.setHeader(REFERENCES, inReplyTo);
    }

    public String getInReplyTo() {
        return messageTransformer.getHeader(IN_REPLY_TO);
    }

    public String getReferences() {
        return getInReplyTo();
    }

    public String getForwardedMessageId() {
        return messageTransformer.getHeader(FORWARDED_MESSAGE_ID);
    }

    public void setForwardedMessageId(String forwardedMessageId) {
        messageTransformer.setHeader(FORWARDED_MESSAGE_ID, forwardedMessageId);
    }

    public void setDraftInfo(String value) {
        messageTransformer.setHeader(X_DRAFT_INFO, value);
    }

    public void setHighPriority() {
        messageTransformer.setHeader(X_PRIORITY, "1");
    }

    public boolean isHighPriority() {
        return getHeader(X_PRIORITY).filter(value -> value.startsWith("1")).isPresent();
    }

    public String getUserAgent() {
        return messageTransformer.getHeader(USER_AGENT);
    }

    protected List<Mime4jAttachment> getInlineAttachments() {
        return messageTransformer.getInlineAttachments();
    }

    private Optional<String> getHeader(String key) {
        return Optional.ofNullable(messageTransformer.getHeader(key));
    }

    private List<Mime4jAddress> getAddresses(String addressType) {
        String header = getHeader(addressType).orElse("");
        String[] addresses = header.split("\\s*,\\s*");
        return Arrays.stream(addresses)
                .filter(address -> !address.isEmpty())
                .map(Mime4jAddress::new)
                .collect(Collectors.toList());
    }

    private String getDraftInfo() {
        String value = messageTransformer.getHeader(X_DRAFT_INFO);
        return value == null ? "" : value;
    }

    private String sanitize(String htmlBody, Mime4jAttachment attachment, UriComponentsBuilder uriComponentsBuilder) {
        String contentUrl = uriComponentsBuilder.pathSegment(escape(attachment.getId())).build().toUriString();
        String replacedCid = htmlBody.replaceAll("cid:" + attachment.getContentId(), contentUrl);
        return replacedCid.replaceAll("mid:" + attachment.getContentId(), contentUrl);
    }

    private void updateDraftInfo() {
        String value = null;

        if (this.receipt) {
            value = "receipt=1";
        }

        if (this.askForDispositionNotification) {
            if (value == null) {
                value = "DSN=1";
            } else {
                value += "; DSN=1";
            }
        }

        if (value != null) {
            messageTransformer.setHeader(X_DRAFT_INFO, value);
        }
    }

    private static String escape(CompositeAttachmentId id) {
        return rethrowCheckedAsUnchecked(() -> URLEncoder.encode(new PercentEscaper("-.*", true).escape(id.toString()), UTF_8.name()));
    }
}
