package org.minig.server.service.impl.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.MessageWriter;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.MailMessageBody;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.helper.BodyConverter.BodyType;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    private static final String X_PRIORITY = "X-PRIORITY";
    private static final String X_DRAFT_INFO = "X-Mozilla-Draft-Info";
    private static final Pattern RECEIPT = Pattern.compile(".*receipt=(1);?.*");
    private static final Pattern DSN = Pattern.compile(".*DSN=(1);?.*");
    private static final String MDN_SENT = "$MDNSent";
    private static final String FORWARDED = "$Forwarded";

    // TODO
    MessageServiceFactoryImpl messageServiceFactory = new MessageServiceFactoryImpl();

    @Autowired
    private MailContext mailContext;

    public MailMessage convertShort(Message msg) {
        if (msg == null) {
            return new MailMessage();
        }

        try {
            MailMessage cm = new MailMessage();

            setMessageId(cm, (MimeMessage) msg);
            setFolder(cm, msg);
            setSender(cm, msg);
            setSubject(cm, msg);
            setDate(cm, msg);
            setHighPriority(cm, msg);
            setFlags(cm, msg);
            setAttachmentId(cm, msg);
            setReceipt(cm, msg);
            setAskForDispositionNotification(cm, msg);
            setForwarded(cm, msg);
            setMdnSent(cm, msg);

            return cm;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MailMessage convertAttachments(Message msg) {
        if (msg == null) {
            return new MailMessage();
        }

        try {
            MailMessage cm = new MailMessage();

            setMessageId(cm, (MimeMessage) msg);
            setFolder(cm, msg);
            setAttachmentId(cm, msg);

            return cm;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MailMessage convertFull(Message msg) {
        if (msg == null) {
            return new MailMessage();
        }

        try {
            MailMessage cm = new MailMessage();

            setMessageId(cm, (MimeMessage) msg);
            setFolder(cm, msg);
            setSender(cm, msg);
            setSubject(cm, msg);
            setDate(cm, msg);
            setMailer(cm, msg);
            setHighPriority(cm, msg);
            setFlags(cm, msg);
            setRecipients(cm, msg);
            setDispositionNotification(cm, msg);
            setBody(cm, msg);
            setAttachmentId(cm, msg);
            setReceipt(cm, msg);
            setAskForDispositionNotification(cm, msg);
            setForwarded(cm, msg);
            setMdnSent(cm, msg);

            return cm;
        } catch (Exception e) {
            // TODO
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // TODO
	@Deprecated
    public Mime4jMessage toMessageImpl(Message msg) {
        try {
            // TODO
            MessageBuilder builder = messageServiceFactory.newMessageBuilder();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            msg.writeTo(output);

            InputStream decodedInput = new ByteArrayInputStream((output).toByteArray());

            MessageImpl parseMessage = (MessageImpl) builder.parseMessage(decodedInput);

            return new Mime4jMessage(parseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // TODO
    public MimeMessage toMimeMessage(Mime4jMessage msg) {
        try {
            // TODO
            MessageWriter newMessageWriter = messageServiceFactory.newMessageWriter();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            newMessageWriter.writeMessage(msg.getMessage(), out);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

            MimeMessage mimeMessage = new MimeMessage(mailContext.getSession(), byteArrayInputStream);

            return mimeMessage;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MailMessage convertId(Message msg) {
        if (msg == null) {
            return new MailMessage();
        }

        try {
            MailMessage cm = new MailMessage();

            setMessageId(cm, (MimeMessage) msg);
            setFolder(cm, msg);

            return cm;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setMessageId(MailMessage cm, MimeMessage msg) throws MessagingException {
        cm.setMessageId(msg.getMessageID());
    }

    private void setFolder(MailMessage cm, Message msg) throws MessagingException {
        if (msg.getFolder() != null) {
            cm.setFolder(msg.getFolder().getFullName());
        }
    }

    private void setMailer(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader("User-Agent");

        if (header != null && header.length == 1) {
            cm.setMailer(header[0]);
        }
    }

    private void setHighPriority(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader("X-Priority");

        if (header != null && header[0].startsWith("1")) {
            cm.setHighPriority(true);
        } else {
            cm.setHighPriority(false);
        }
    }

    private void setReceipt(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader(X_DRAFT_INFO);
        cm.setReceipt(false);

        if (header != null && header.length == 1 && header[0] != null) {
            Matcher matcher = RECEIPT.matcher(header[0]);

            if (matcher.matches()) {
                cm.setReceipt(true);
            }
        }
    }

    private void setAskForDispositionNotification(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader(X_DRAFT_INFO);
        cm.setAskForDispositionNotification(false);

        if (msg.getFlags().getUserFlags() != null) {
            for (String flag : msg.getFlags().getUserFlags()) {
                if (MDN_SENT.equals(flag)) {
                    return;
                }
            }
        }

        if (header != null && header.length == 1 && header[0] != null) {
            Matcher matcher = DSN.matcher(header[0]);

            if (matcher.matches()) {
                cm.setAskForDispositionNotification(true);
            }
        }
    }

    private void setFlags(MailMessage cm, Message msg) throws MessagingException {
        Flags.Flag[] sf = msg.getFlags().getSystemFlags();

        for (int i = 0; i < sf.length; i++) {
            if (sf[i] == Flags.Flag.SEEN) {
                cm.setRead(Boolean.TRUE);
            } else if (sf[i] == Flags.Flag.ANSWERED) {
                cm.setAnswered(Boolean.TRUE);
            } else if (sf[i] == Flags.Flag.FLAGGED) {
                cm.setStarred(Boolean.TRUE);
            } else if (sf[i] == Flags.Flag.DELETED) {
                cm.setDeleted(Boolean.TRUE);
            }
        }

        if (cm.getAnswered() == null) {
            cm.setAnswered(Boolean.FALSE);
        }

        if (cm.getRead() == null) {
            cm.setRead(Boolean.FALSE);
        }

        if (cm.getStarred() == null) {
            cm.setStarred(Boolean.FALSE);
        }

        if (cm.getDeleted() == null) {
            cm.setDeleted(Boolean.FALSE);
        }
    }

    private void setSubject(MailMessage cm, Message msg) throws MessagingException {
        cm.setSubject(msg.getSubject());
    }

    private void setDate(MailMessage cm, Message msg) throws MessagingException {
        if (msg.getSentDate() != null) {
            cm.setDate(msg.getSentDate());
        }
    }

    private void setSender(MailMessage cm, Message msg) throws MessagingException {
        Address[] fromAddress = msg.getFrom();

        if (fromAddress == null || fromAddress.length != 1) {
            cm.setSender(new MailMessageAddress());
        } else {
            MailMessageAddress convertToMailMessageAddress = convertToMailMessageAddress(fromAddress[0]);
            cm.setSender(convertToMailMessageAddress);
        }
    }

    private void setRecipients(MailMessage cm, Message msg) throws MessagingException {
        cm.setTo(getRecipients(msg, RecipientType.TO));
        cm.setCc(getRecipients(msg, RecipientType.CC));
        cm.setBcc(getRecipients(msg, RecipientType.BCC));
    }

    private void setDispositionNotification(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader("Disposition-Notification-To");
        List<MailMessageAddress> recipients = new ArrayList<MailMessageAddress>();

        if (header != null) {
            Scanner s = null;

            try {
                s = new Scanner(header[0]);
                s.useDelimiter("\\s*,\\s*");

                while (s.hasNext()) {
                    String next = s.next();
                    int startIndex = 0;
                    int endIndex = next.length();

                    for (int i = 0; i < next.length(); i++) {
                        switch(next.charAt(i)) {
                        case '<':
                            startIndex = i + 1;
                            break;
                        case '>':
                            endIndex = i;
                        }
                    }

                    MailMessageAddress ea = new MailMessageAddress(next.substring(startIndex, endIndex));
                    recipients.add(ea);
                }
            } catch (Exception e) {
                if (s != null) {
                    s.close();
                }
            }
        }

        cm.setDispositionNotification(recipients);
    }

    private void setBody(MailMessage cm, Message msg) throws MessagingException, IOException {
        String text = (String) BodyConverter.get(msg, BodyType.TEXT);
        String html = (String) BodyConverter.get(msg, BodyType.HTML);
        MailMessageBody b = new MailMessageBody();

        b.setPlain(text);
        b.setHtml(html);
        cm.setBody(b);
    }

    private void setAttachmentId(MailMessage cm, Message msg) throws MessagingException, IOException {
        List<String> attachmentIdList = getAttachmentIds(msg);
        List<CompositeAttachmentId> idList = new ArrayList<CompositeAttachmentId>();

        for (String attachmentId : attachmentIdList) {
            CompositeAttachmentId id = new CompositeAttachmentId();

            id.setMessageId(msg.getHeader("Message-ID")[0]);
            id.setFolder(msg.getFolder().getFullName());
            id.setFileName(attachmentId);

            idList.add(id);
        }

        cm.setAttachments(idList);
    }

    private void setForwarded(MailMessage cm, Message msg) throws MessagingException {
        cm.setForwarded(Boolean.FALSE);

        if (msg.getFlags().getUserFlags() != null) {
            for (String flag : msg.getFlags().getUserFlags()) {
                if (FORWARDED.equals(flag)) {
                    cm.setForwarded(Boolean.TRUE);
                    return;
                }
            }
        }
    }

    private void setMdnSent(MailMessage cm, Message msg) throws MessagingException {
        cm.setMdnSent(Boolean.FALSE);

        if (msg.getFlags().getUserFlags() != null) {
            for (String flag : msg.getFlags().getUserFlags()) {
                if (MDN_SENT.equals(flag)) {
                    cm.setMdnSent(Boolean.TRUE);
                    return;
                }
            }
        }
    }

    private List<MailMessageAddress> getRecipients(Message msg, RecipientType type) throws MessagingException {
        List<MailMessageAddress> recipientList = new ArrayList<MailMessageAddress>();

        Address[] recipients = msg.getRecipients(type);
        if (recipients != null) {
            for (Address address : recipients) {
                recipientList.add(convertToMailMessageAddress(address));
            }
        }

        return recipientList;
    }

    private MailMessageAddress convertToMailMessageAddress(Address address) {
        if (address == null) {
            return new MailMessageAddress();
        } else if (address instanceof InternetAddress) {
            InternetAddress ia = (InternetAddress) address;
            String personal = ia.getPersonal();

            if (personal != null) {
                return new MailMessageAddress(personal, ia.getAddress());
            } else {
                return new MailMessageAddress(address.toString());
            }
        } else {
            return new MailMessageAddress(address.toString());
        }
    }

    public MimeMessage toMimeMessage(MailMessage source) {
        try {

            // TODO
            JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();

            MimeMessage message = javaMailSenderImpl.createMimeMessage();
            MimeMessageHelper target = null;

            if (source.getBody() != null) {
                if (source.getBody().getPlain() != null && source.getBody().getHtml() != null) {
                    target = new MimeMessageHelper(message, true, "UTF-8");
                    target.setText(source.getBody().getPlain(), source.getBody().getHtml());
                } else if (source.getBody().getPlain() != null && source.getBody().getHtml() == null) {
                    target = new MimeMessageHelper(message, "UTF-8");
                    target.setText(source.getBody().getPlain());
                } else {
                    target = new MimeMessageHelper(message, "UTF-8");
                    target.setText(source.getBody().getHtml(), true);
                }
            }

            if (source.getSender() != null) {
                target.setFrom(source.getSender().getEmail(), source.getSender().getDisplay());
            }

            if (source.getSubject() != null) {
                target.setSubject(source.getSubject());
            }

            if (source.getDate() != null) {
                target.setSentDate(source.getDate());
            }

            if (source.getCc() != null) {
                for (MailMessageAddress address : source.getCc()) {
                    target.addCc(address.getEmail(), address.getDisplayName());
                }
            }

            if (source.getBcc() != null) {
                for (MailMessageAddress address : source.getBcc()) {
                    target.addCc(address.getEmail(), address.getDisplayName());
                }
            }

            if (source.getTo() != null) {
                for (MailMessageAddress address : source.getTo()) {
                    target.addTo(address.getEmail(), address.getDisplayName());
                }
            }

            String draftInfo = null;

            if (source.getReceipt() != null && source.getReceipt()) {
                draftInfo = "receipt=1";
            }

            if (source.getAskForDispositionNotification() != null && source.getAskForDispositionNotification()) {
                if (draftInfo != null) {
                    draftInfo += "; DSN=1";
                } else {
                    draftInfo = "DSN=1";
                }
            }

            if (source.getHighPriority() != null && source.getHighPriority()) {
                target.setPriority(1);
            }

            // source.getAttachments();
            // source.getDispositionNotification()
            // source.getFwdMessages()

            MimeMessage mimeMessage = target.getMimeMessage();

            mimeMessage.setHeader(X_DRAFT_INFO, draftInfo);

            return mimeMessage;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Message toMessage(MailMessage source) {
        return this.toMimeMessage(source);

        // try {
        // MimeMessage message = mailHelper.createMimeMessage();
        // MimeMessageHelper target = null;
        //
        // if (source.getBody() != null) {
        // if (source.getBody().getPlain() != null && source.getBody().getHtml()
        // != null) {
        // target = new MimeMessageHelper(message, true, "UTF-8");
        // target.setText(source.getBody().getPlain(),
        // source.getBody().getHtml());
        // } else if (source.getBody().getPlain() != null &&
        // source.getBody().getHtml() == null) {
        // target = new MimeMessageHelper(message, "UTF-8");
        // target.setText(source.getBody().getPlain());
        // } else {
        // target = new MimeMessageHelper(message, "UTF-8");
        // target.setText(source.getBody().getHtml(), true);
        // }
        // }
        //
        // if (source.getSender() != null) {
        // target.setFrom(source.getSender().getEmail(),
        // source.getSender().getEmail());
        // }
        //
        // if (source.getSubject() != null) {
        // target.setSubject(source.getSubject());
        // }
        //
        // if (source.getDate() != null) {
        // target.setSentDate(source.getDate());
        // }
        //
        // if (source.getCc() != null) {
        // for (MailMessageAddress address : source.getCc()) {
        // target.addCc(address.getEmail(), address.getDisplayName());
        // }
        // }
        //
        // if (source.getBcc() != null) {
        // for (MailMessageAddress address : source.getBcc()) {
        // target.addCc(address.getEmail(), address.getDisplayName());
        // }
        // }
        //
        // if (source.getTo() != null) {
        // for (MailMessageAddress address : source.getTo()) {
        // target.addTo(address.getEmail(), address.getDisplayName());
        // }
        // }
        //
        // // source.getAttachments();
        // // source.getDispositionNotification()
        // // source.getFwdMessages()
        //
        // return target.getMimeMessage();
        // } catch (Exception e) {
        // throw new MailException(e);
        // }
    }

    public Mime4jMessage toMime4jMessage(MailMessage source) {
        MessageBuilder newMessageBuilder = messageServiceFactory.newMessageBuilder();
        MessageImpl message = (MessageImpl) newMessageBuilder.newMessage();
        Mime4jMessage target = new Mime4jMessage(message);

        target.setId(source);

        if (source.getBody() != null) {
            target.setHtml(source.getBody().getHtml());
            target.setPlain(source.getBody().getPlain());
        } else {
            target.setPlain("");
        }

        if (source.getSender() != null) {
            // TODO , source.getSender().getDisplay()
            target.setFrom(source.getSender().getEmail());
        }

        if (source.getSubject() != null) {
            target.setSubject(source.getSubject());
        }

        if (source.getDate() != null) {
            target.setDate(source.getDate());
        }

        if (source.getCc() != null) {
            for (MailMessageAddress address : source.getCc()) {
                target.addCc(address.getEmail(), address.getDisplayName());
            }
        }

        if (source.getBcc() != null) {
            for (MailMessageAddress address : source.getBcc()) {
                target.addBcc(address.getEmail(), address.getDisplayName());
            }
        }

        if (source.getTo() != null) {
            for (MailMessageAddress address : source.getTo()) {
                if(address.getEmail() != null) {
                    target.addTo(address.getEmail());
                }
            }
        }

        String draftInfo = null;

        if (source.getReceipt() != null && source.getReceipt()) {
            draftInfo = "receipt=1";
        }

        if (source.getAskForDispositionNotification() != null && source.getAskForDispositionNotification()) {
            if (draftInfo != null) {
                draftInfo += "; DSN=1";
            } else {
                draftInfo = "DSN=1";
            }
        }

        if (source.getHighPriority() != null && source.getHighPriority()) {
            target.setHeader(X_PRIORITY, "1");
        }

        target.setHeader(X_DRAFT_INFO, draftInfo);

        return target;
    }

    /**
     * http://www.oracle.com/technetwork/java/javamail/faq/index.html#hasattach
     */
    private List<String> getAttachmentIds(Part p) throws MessagingException, IOException {
        List<String> l = new ArrayList<String>();

        String disp = p.getDisposition();
        if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
            String fileName = p.getFileName();

            if (fileName != null) {
                l.add(fileName);

                return l;
            }
        }

        if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {

                List<String> s = getAttachmentIds(mp.getBodyPart(i));

                l.addAll(s);

            }
        }

        return l;
    }
}
