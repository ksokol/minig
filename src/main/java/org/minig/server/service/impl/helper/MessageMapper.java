package org.minig.server.service.impl.helper;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.minig.MinigConstants.FORWARDED;
import static org.minig.MinigConstants.FORWARDED_MESSAGE_ID;
import static org.minig.MinigConstants.IN_REPLY_TO;
import static org.minig.MinigConstants.MDN_SENT;
import static org.minig.MinigConstants.X_DRAFT_INFO;

/**
 * @author Kamill Sokol
 */
@Deprecated
@Component
public class MessageMapper {

    private static final Pattern RECEIPT = Pattern.compile(".*receipt=(1);?.*");
    private static final Pattern DSN = Pattern.compile(".*DSN=(1);?.*");

    @Deprecated
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
            setInReplyTo(cm, msg);
            setForwardedMessageId(cm, msg);

            return cm;
        } catch (Exception e) {
            // TODO
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setForwardedMessageId(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader(FORWARDED_MESSAGE_ID);

        if (header != null && header.length == 1) {
            cm.setForwardedMessageId(header[0]);
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
                        switch (next.charAt(i)) {
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
        Mime4jMessage mime4jMessage = new Mime4jMessage(msg);
        cm.setPlain(mime4jMessage.getPlain());
        cm.setHtml(mime4jMessage.getHtml());
    }

    private void setAttachmentId(MailMessage cm, Message msg) throws MessagingException, IOException {
        List<CompositeAttachmentId> attachmentIds = new Mime4jMessage(msg).getAttachments().stream().map(Mime4jAttachment::getId).collect(Collectors.toList());
        cm.setAttachments(attachmentIds);
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

    private void setInReplyTo(MailMessage cm, Message msg) throws MessagingException {
        String[] header = msg.getHeader(IN_REPLY_TO);
        if (header == null || header.length != 1) {
            return;
        }
        cm.setInReplyTo(header[0]);
    }

    private List<MailMessageAddress> getRecipients(Message msg, RecipientType type) throws MessagingException {
        List<MailMessageAddress> recipientList = new ArrayList<>();

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

    @Deprecated
    public MimeMessage toMimeMessage(MailMessage source) {
        try {

            // TODO
            JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();

            MimeMessage message = javaMailSenderImpl.createMimeMessage();
            MimeMessageHelper target;

            if (source.getPlain() != null && source.getHtml() != null) {
                target = new MimeMessageHelper(message, true, "UTF-8");
                target.setText(source.getPlain(), source.getHtml());
            } else if (source.getPlain() != null && source.getHtml() == null) {
                target = new MimeMessageHelper(message, "UTF-8");
                target.setText(source.getPlain());
            } else {
                target = new MimeMessageHelper(message, "UTF-8");
                target.setText(source.getHtml(), true);
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

    @Deprecated
    public Message toMessage(MailMessage source) {
        return this.toMimeMessage(source);
    }

    @Deprecated
    public Mime4jMessage toMime4jMessage(MailMessage source) {
        Mime4jMessage target = new Mime4jMessage(source);

        target.setPlain(source.getPlain());
        target.setHtml(source.getHtml());

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
                if (address.getEmail() != null) {
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
            target.setHighPriority();
        }

        target.setDraftInfo(draftInfo);
        target.setInReplyTo(source.getInReplyTo());
        target.setForwardedMessageId(source.getForwardedMessageId());

        return target;
    }
}
