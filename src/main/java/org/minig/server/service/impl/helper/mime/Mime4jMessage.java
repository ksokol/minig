package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.message.BasicBodyFactory;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MultipartImpl;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.RawField;
import org.minig.server.service.CompositeId;
import org.springframework.util.StringUtils;

import javax.activation.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kamill Sokol
 */
public class Mime4jMessage {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final Map<String, String> CHARSET_UTF_8 = new HashMap<String, String>() {{
        put("charset", "UTF-8");
    }

        private static final long serialVersionUID = 1439034073160479080L;
    };

    private static final String X_DRAFT_INFO = "X-Mozilla-Draft-Info";
    private static final String MDN_SENT = "$MDNSent";
    private static final String X_PRIORITY = "X-PRIORITY";
    private static final String IN_REPLY_TO = "In-Reply-To";
    private static final String REFERENCES = "References";
    private static final String FORWARDED_MESSAGE_ID = "X-Forwarded-Message-Id";

    private CompositeId id;
    private MessageImpl message;
    private boolean receipt;
    private boolean askForDispositionNotification;

    private Set<Address> to = new HashSet<>();
    private Set<Address> cc = new HashSet<>();
    private Set<Address> bcc = new HashSet<>();

    public Mime4jMessage(MessageImpl message) {
        this.message = message;
    }

    public Mime4jMessage(MessageImpl message, CompositeId id) {
        this.message = message;
        this.id = id;
    }

    public MessageImpl getMessage() {
        return message;
    }

    public void setMessage(MessageImpl message) {
        this.message = message;
    }

    public CompositeId getId() {
        return id;
    }

    public void setId(CompositeId id) {
        this.id = id;
    }

    public String getPlain() {
        if (message.isMimeType("text/plain")) {
            return getReadablePart(message.getBody());
        } else if (message.isMultipart()) {
            Multipart multipart = (Multipart) message.getBody();
            return parseBodyParts(multipart, "text/plain");
        }

        return "";
    }

    public String getHtml() {
        if (message.isMimeType("text/html")) {
            return getReadablePart(message.getBody());
        } else if (message.isMultipart()) {
            Multipart multipart = (Multipart) message.getBody();
            return parseBodyParts(multipart, "text/html");
        }

        return "";
    }

    public void setPlain(String plain) {
        if (plain == null) {
            return;
        }

        if (message.isMimeType("text/plain")) {
            message.removeBody();

            TextBody newbody = new BasicBodyFactory().textBody(plain, UTF_8);
            message.setBody(newbody);

        } else if (message.isMultipart()) {
            Multipart multipart = (Multipart) message.getBody();
            IndexOfResult indexOf = getIndexOf(multipart.getBodyParts(), "text/plain");

            TextBody txtBody = new BasicBodyFactory().textBody(plain, UTF_8);
            BodyPart textBodyPart = new BodyPart();

            textBodyPart.setText(txtBody);

            if (indexOf != null) {
                // Multipart part = (Multipart) indexOf.part;

                multipart.replaceBodyPart(textBodyPart, indexOf.indexOf);
            } else {
                multipart.addBodyPart(textBodyPart);
            }
        } else if (message.isMimeType("text/html")) {
            TextBody removeBody = (TextBody) message.removeBody();

            TextBody txtBody = new BasicBodyFactory().textBody(plain, UTF_8);

            BodyPart htmlBodyPart = new BodyPart();
            BodyPart textBodyPart = new BodyPart();

            htmlBodyPart.setText(removeBody, "html");
            textBodyPart.setText(txtBody);

            Multipart newBody = new MultipartImpl("related");

            newBody.addBodyPart(htmlBodyPart);
            newBody.addBodyPart(textBodyPart);

            message.setMultipart(newBody, CHARSET_UTF_8);
        }
    }

    public void setHtml(String html) {
        if (html == null) {
            return;
        }

        if (message.isMimeType("text/html")) {
            message.removeBody();

            TextBody newbody = new BasicBodyFactory().textBody(html, UTF_8);
            message.setBody(newbody);
        } else if (message.isMultipart()) {
            Multipart multipart = (Multipart) message.getBody();
            TextBody txtBody = new BasicBodyFactory().textBody(html, UTF_8);
            BodyPart htmlBodyPart = new BodyPart();

            htmlBodyPart.setText(txtBody, "html");

            IndexOfResult indexOf = getIndexOf(multipart.getBodyParts(), "text/html");

            if (indexOf != null) {
                Multipart part = (Multipart) indexOf.part.getParent().getBody();

                part.replaceBodyPart(htmlBodyPart, indexOf.indexOf);
            } else {
                multipart.addBodyPart(htmlBodyPart);
            }
        } else if (message.isMimeType("text/plain")) {
            TextBody textBody = (TextBody) message.removeBody();
            TextBody htmlBody;

            htmlBody = new BasicBodyFactory().textBody(html, UTF_8);

            if (textBody == null) {
                Map<String, String> m = new HashMap<>();
                m.put("charset", "UTF-8");
                message.setBody(htmlBody, "text/html", m);
            } else {

                BodyPart htmlBodyPart = new BodyPart();
                BodyPart textBodyPart = new BodyPart();

                htmlBodyPart.setText(htmlBody, "html");

                Multipart newBody = new MultipartImpl("related");

                textBodyPart.setText(textBody);

                newBody.addBodyPart(htmlBodyPart);
                newBody.addBodyPart(textBodyPart);

                message.setMultipart(newBody, CHARSET_UTF_8);
            }
        }
    }

	public Mime4jAttachment getAttachment(String filename) {
		List<Mime4jAttachment> attachments = getAttachments();
		for (Mime4jAttachment attachment : attachments) {
			if(attachment.getId().getFileName().equals(filename)) {
				return attachment;
			}
		}
		return null;
	}

	public List<Mime4jAttachment> getAttachments() {
		List<Mime4jAttachment> mime4jAttachments = Mime4jAttachmentExtractor.extract(message);
		for (Mime4jAttachment mime4jAttachmentMetadata : mime4jAttachments) {
            mime4jAttachmentMetadata.setId(id);
		}
		return mime4jAttachments;
	}

    public void addAttachment(DataSource dataSource) {
        BodyPart attachPart = new BodyPart();

        try {
            Body body1 = new BasicBodyFactory().binaryBody(dataSource.getInputStream());
            attachPart.setBody(body1, dataSource.getContentType());
            attachPart.setContentTransferEncoding("base64");
            attachPart.setContentDisposition("attachment");
            attachPart.setFilename(dataSource.getName());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (message.isMimeType("text/html")) {
            TextBody removeBody = (TextBody) message.removeBody();
            BodyPart htmlBodyPart = new BodyPart();
            htmlBodyPart.setText(removeBody, "html");

            Multipart newBody = new MultipartImpl("mixed");

            newBody.addBodyPart(htmlBodyPart);
            message.setMultipart(newBody);

        } else if (message.isMimeType("text/plain")) {
            TextBody removeBody = (TextBody) message.removeBody();
            BodyPart htmlBodyPart = new BodyPart();
            htmlBodyPart.setText(removeBody, "html");

            Multipart newBody = new MultipartImpl("mixed");

            newBody.addBodyPart(htmlBodyPart);
            message.setMultipart(newBody);
        }

        Multipart multipart = (Multipart) message.getBody();

        multipart.addBodyPart(attachPart);
    }

    public void deleteAttachment(String filename) {
        if (message.isMultipart()) {
            deleteAttachment((Multipart) message.getBody(), filename);
        }
    }

    public void setFrom(String email) {
        String[] split = email.split("@");
        Mailbox from = new Mailbox(split[0], split[1]);
        message.setFrom(from);
    }

    public void setSubject(String subject) {
        message.setSubject(subject);
    }

    public void setDate(Date date) {
        message.setDate(date);
    }

    public void addCc(String email, String name) {
        AddressList cc = message.getCc();

        if (cc == null) {
            cc = new AddressList(new ArrayList<Address>(), true);
            message.setCc(cc);
        }

        Mailbox mb = new Mailbox(name, email);
        cc.add(mb);
    }

    public void addBcc(String email, String name) {
        AddressList bcc = message.getBcc();

        if (bcc == null) {
            bcc = new AddressList(new ArrayList<Address>(), true);
            message.setBcc(bcc);
        }

        Mailbox mb = new Mailbox(name, email);
        bcc.add(mb);
    }

    public void addTo(String email) {
        AddressList to = message.getTo();
        List<Address> boxes = new ArrayList<Address>();

        if (to != null) {
            boxes.addAll(to.subList(0, to.size()));
        }

        String[] split = email.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);

        boxes.add(mb);
        message.setTo(boxes);
    }

    public void clearTo() {
        message.setTo(new ArrayList<Address>());
    }

    public void setHeader(String key, String value) {
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        RawField f = new RawField(key, value);
        Header messageHeader = message.getHeader();
        messageHeader.setField(f);
    }

    private void deleteAttachment(Multipart multipart, String filename) {
        List<Entity> e = multipart.getBodyParts();

        for (int i = 0; i < e.size(); i++) {
            BodyPart part = (BodyPart) e.get(i);

            if ("attachment".equalsIgnoreCase(part.getDispositionType()) && filename.equals(part.getFilename())) {
                multipart.removeBodyPart(i);
            }

            if (part.isMultipart()) {
                deleteAttachment((Multipart) part.getBody(), filename);
            }
        }
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

    private String getDraftInfo() {
        Header header = message.getHeader();
        Field field = header.getField(X_DRAFT_INFO);
        if (field == null) {
            return "";
        }
        String body = field.getBody();
        if (body == null) {
            return "";
        }
        return body;
    }

    public void setSender(String sender) {
        String[] split = sender.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);
        message.setSender(mb);
        message.setFrom(mb);
    }

    public String getSender() {
        MailboxList from = message.getFrom();
        Mailbox sender = from.get(0);
        return sender.getAddress();
    }

    public void clearRecipients() {
        to.clear();
        message.setTo(to);
    }

    public void clearCc() {
        cc.clear();
        message.setCc(cc);
    }

    public void clearBcc() {
        bcc.clear();
        message.setBcc(bcc);
    }

    public void addRecipient(String recipient) {
        String[] split = recipient.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);

        to.add(mb);

        message.setTo(to);
    }

    public void addCc(String recipient) {
        String[] split = recipient.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);
        cc.add(mb);
        message.setCc(cc);
    }

    public void addBcc(String recipient) {
        String[] split = recipient.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);
        bcc.add(mb);
        message.setBcc(bcc);
    }

    public void setAskForDispositionNotification(Boolean askForDispositionNotification) {
        this.askForDispositionNotification = askForDispositionNotification == null ? false : askForDispositionNotification;
        updateXPriority();
    }

    public void setHighPriority(Boolean highPriority) {
        if(highPriority != null && highPriority) {
            setHeader(X_PRIORITY, "1");
            return;
        }
        message.getHeader().removeFields(X_PRIORITY);
    }

    public void setReceipt(Boolean receipt) {
        this.receipt = receipt == null ? false : receipt;
        updateXPriority();
    }

    public String getSubject() {
        return message.getSubject();
    }

    public void setInReplyTo(String inReplyTo) {
        setHeader(IN_REPLY_TO, inReplyTo);
        setHeader(REFERENCES, inReplyTo);
    }

    public String getInReplyTo() {
        Field field = getMessage().getHeader().getField(IN_REPLY_TO);
        if(field == null) {
            return null;
        }
        return field.getBody();
    }

    public String getForwardedMessageId() {
        Field field = getMessage().getHeader().getField(FORWARDED_MESSAGE_ID);
        if(field == null) {
            return null;
        }
        return field.getBody();
    }

    public void setForwardedMessageId(String forwardedMessageId) {
        setHeader(FORWARDED_MESSAGE_ID, forwardedMessageId);
    }

    private String parseBodyParts(Multipart multipart, String mimeType) {
        TextBody bodyPart = getBodyPart(multipart, mimeType);
        return getReadablePart(bodyPart);
    }

    private IndexOfResult getIndexOf(List<Entity> bodyParts, String mimeType) {
        for (int i = 0; i < bodyParts.size(); i++) {
            BodyPart part = (BodyPart) bodyParts.get(i);

            if (part.isMimeType(mimeType)) {
                IndexOfResult result = new IndexOfResult();
                result.indexOf = i;
                result.part = part;

                return result;
            } else if (part.isMultipart()) {
                Multipart multipart = (Multipart) part.getBody();

                IndexOfResult indexOf = getIndexOf(multipart.getBodyParts(), mimeType);

                if (indexOf != null) {
                    return indexOf;
                }
            }
        }

        return null;
    }

    private void updateXPriority() {
        String value = null;

        if(this.receipt) {
            value = "receipt=1";
        }

        if(this.askForDispositionNotification) {
            if(value == null) {
                value += "DSN=1";
            } else {
                value += "; DSN=1";
            }
        }

        Header header = message.getHeader();
        if(value == null) {
            header.removeFields(X_DRAFT_INFO);
        }

        setHeader(X_DRAFT_INFO, value);
    }

    // TODO
    private class IndexOfResult {
        public int indexOf;
        public BodyPart part;
    }

    private TextBody getBodyPart(Multipart multipart, String mimeType) {
        try {
            List<Entity> bodyParts = multipart.getBodyParts();

            for (Entity e : bodyParts) {
                BodyPart part = (BodyPart) e;

                if (part.isMimeType(mimeType)) {
                    return (TextBody) part.getBody();

                }

                if (part.isMultipart()) {
                    return getBodyPart((Multipart) part.getBody(), mimeType);
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getReadablePart(Body part) {
        TextBody tb = (TextBody) part;

        if (tb == null) {
            return "";
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String mimeCharset = tb.getMimeCharset() == null ? "UTF-8" : tb.getMimeCharset();
            try {
                tb.writeTo(baos);
                return new String(baos.toByteArray(), mimeCharset);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
