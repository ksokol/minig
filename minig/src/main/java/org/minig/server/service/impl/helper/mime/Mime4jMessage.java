package org.minig.server.service.impl.helper.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

import javax.activation.DataSource;

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

public class Mime4jMessage {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final Map<String, String> CHARSET_UTF_8 = new HashMap<String, String>() {{
        put("charset", "UTF-8");
    }};

    private CompositeId id;
    private MessageImpl message;

    private Set<Address> to = new HashSet<Address>();

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
                Map<String, String> m = new HashMap<String, String>();
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

    public List<BodyPart> getAttachments() {
        List<BodyPart> attachments = new ArrayList<BodyPart>();

        if (message.isMultipart()) {
            List<BodyPart> getAttachments = getAttachments((Multipart) message.getBody());
            attachments.addAll(getAttachments);
        }

        return attachments;
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
        // TODO timezone
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

    //
    // public void addTo(String email, String name) {
    // AddressList to = message.getTo();
    //
    // if (to == null) {
    // to = new AddressList(new ArrayList<Address>(), true);
    // message.setTo(to);
    // }
    //
    // Mailbox mb = new Mailbox(name, email);
    // to.add(mb);
    // }

    public void addTo(String email) {
        AddressList to = message.getTo();

        List<Address> boxes = new ArrayList<Address>();

        if (to != null) {
            boxes.addAll(to.subList(0, to.size()));
        }

        // if (to == null) {
        // to = new AddressList(new ArrayList<Address>(), true);
        // message.setTo(to);
        // }

        String[] split = email.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);

        boxes.add(mb);

        message.setTo(boxes);

        // to.add(mb);
    }

    public void clearTo() {
        message.setTo(new ArrayList<Address>());
    }

    public void setHeader(String key, String value) {
        RawField f = new RawField(key, value);
        Header messageHheader = message.getHeader();
        messageHheader.addField(f);
    }

    private List<BodyPart> getAttachments(Multipart multipart) {
        List<BodyPart> attachments = new ArrayList<BodyPart>();

        for (Entity e : multipart.getBodyParts()) {
            BodyPart part = (BodyPart) e;

            if ("attachment".equalsIgnoreCase(part.getDispositionType())) {
                attachments.add(part);
            }

            if (part.isMultipart()) {
                List<BodyPart> getAttachments = getAttachments((Multipart) part.getBody());
                attachments.addAll(getAttachments);
            }
        }

        return attachments;
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

    public boolean isDispositionNotification() {
        Header header = message.getHeader();

        // reuse Mozilla's header field
        Field field = header.getField("X-Mozilla-Draft-Info");

        if (field != null) {
            String body = field.getBody();
            // TODO
            if (body != null && body.contains("DSN=1")) {
                return true;
            }
        }

        return false;
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

    public void setRecipient(String recipient) {
        String[] split = recipient.split("@");
        Mailbox mb = new Mailbox(split[0], split[1]);

        to.add(mb);

        message.setTo(to);
    }

    public String getSubject() {
        return message.getSubject();
    }

    private String parseBodyParts(Multipart multipart, String mimeType) {
        TextBody bodyPart = getBodyPart(multipart, mimeType);
        return getReadablePart(bodyPart);
        //
        // try {
        //
        // getBodyPart(multipart, mimeType);
        //
        // List<Entity> bodyParts = multipart.getBodyParts();
        //
        // for (Entity e : bodyParts) {
        // BodyPart part = (BodyPart) e;
        //
        // if (part.isMimeType(mimeType)) {
        // return getReadablePart(part.getBody());
        //
        // }
        //
        // if (part.isMultipart()) {
        // return parseBodyParts((Multipart) part.getBody(), mimeType);
        // }
        // }
        //
        // return "";
        // } catch (Exception e) {
        // throw new RuntimeException(e.getMessage(), e);
        // }
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
