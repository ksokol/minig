package org.minig.server.service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.minig.server.MailMessageAddress;
import org.mockito.Mockito;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MimeMessageBuilder {

    // defaults
    private String messageId = "1";
    private String folder = "folder";
    private String sender = "sender@localhost";
    private String subject = "subject";
    private Date date = new Date();
    private String mailer = "mailer";
    private boolean highPriority = false;
    private boolean answered = false;
    private boolean read = false;
    private boolean starred = false;
    private boolean askForDispositionNotification = false;
    private boolean receipt = false;
    private boolean deleted = false;
    private List<InternetAddress> recipientToList = new ArrayList<InternetAddress>();
    private List<InternetAddress> recipientCcList = new ArrayList<InternetAddress>();
    private List<InternetAddress> recipientBccList = new ArrayList<InternetAddress>();
    private List<InternetAddress> dispositionNotification = new ArrayList<InternetAddress>();
    private List<String> attachmentIdList = new ArrayList<String>();
    private boolean forwarded;
    private boolean mdnSent;

    public MimeMessageBuilder() {
        try {
            recipientToList.add(new InternetAddress("recipient1@localhost"));
            recipientCcList.add(new InternetAddress("recipient10@localhost"));
            recipientBccList.add(new InternetAddress("recipient20@localhost"));
            dispositionNotification.add(new InternetAddress("recipient100@localhost"));

            attachmentIdList.add("1");
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<MimeMessage> build(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }

        List<MimeMessage> l = new ArrayList<MimeMessage>();

        for (int i = 0; i < count; i++) {
            l.add(build());
        }

        return l;
    }

    public MimeMessage build() {
        try {
            MimeMessage message = new MimeMessage((Session) null);

            message.setFrom(new InternetAddress(sender));

            message.addRecipients(Message.RecipientType.TO, recipientToList.toArray(new Address[recipientToList.size()]));

            message.setSubject(subject);
            message.setText("noop");

            message.saveChanges();
            return message;
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MimeMessage mock() {
        MimeMessage m = org.mockito.Mockito.mock(MimeMessage.class, RETURNS_DEEP_STUBS);

        try {
            when(m.getMessageID()).thenReturn(messageId);
            when(m.getFolder().getFullName()).thenReturn(folder);
            when(m.getSubject()).thenReturn(subject);
            when(m.getSentDate()).thenReturn(date);
            when(m.getHeader("User-Agent")).thenReturn(new String[] { mailer });
            when(m.getHeader("Message-ID")).thenReturn(new String[] { messageId });

            if (highPriority) {
                when(m.getHeader("X-Priority")).thenReturn(new String[] { "1 " });
            }

            when(m.getRecipients(RecipientType.TO)).thenReturn(recipientToList.toArray(new Address[recipientToList.size()]));

            when(m.getRecipients(RecipientType.CC)).thenReturn(recipientCcList.toArray(new Address[recipientCcList.size()]));

            when(m.getRecipients(RecipientType.BCC)).thenReturn(recipientBccList.toArray(new Address[recipientBccList.size()]));

            if (!dispositionNotification.isEmpty()) {
                String disposition = "";

                for (InternetAddress ia : dispositionNotification) {
                    disposition += ia.toString() + ", ";
                }

                when(m.getHeader("Disposition-Notification-To")).thenReturn(new String[] { disposition });
            }

            List<Flag> flags = new ArrayList<Flag>();

            if (answered) {
                flags.add(Flag.ANSWERED);
            }

            if (read) {
                flags.add(Flag.SEEN);
            }

            if (starred) {
                flags.add(Flag.FLAGGED);
            }

            if (deleted) {
                flags.add(Flag.DELETED);
            }

            String xHeader = "";

            if (receipt) {
                xHeader += "receipt=1";
            }

            if (askForDispositionNotification) {
                xHeader = (!xHeader.isEmpty()) ? xHeader + "; DSN=1" : "DSN=1";
            }

            if (!xHeader.isEmpty()) {
                when(m.getHeader("X-Mozilla-Draft-Info")).thenReturn(new String[] { xHeader });
            }

            List<String> userFlags = new ArrayList<String>();

            if (forwarded) {
                userFlags.add("$Forwarded");
            }

            if (mdnSent) {
                userFlags.add("$MDNSent");
            }

            if (!userFlags.isEmpty()) {
                when(m.getFlags().getUserFlags()).thenReturn(userFlags.toArray(new String[userFlags.size()]));
            }

            when(m.getFlags().getSystemFlags()).thenReturn(flags.toArray(new Flag[flags.size()]));
            when(m.getFrom()).thenReturn(new Address[] { new InternetAddress(sender) });
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return m;
    }

    public MimeMessage build(String file) {
        try {
            MimeMessage mimeMessage = new MimeMessage(null, new FileInputStream(file));

            MimeMessage spy = spy(mimeMessage);
            Folder folderMock = Mockito.mock(Folder.class);

            when(folderMock.getFullName()).thenReturn(folder);
            when(spy.getFolder()).thenReturn(folderMock);

            return spy;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MimeMessageBuilder setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MimeMessageBuilder setFolder(String folder) {
        this.folder = folder;
        return this;
    }

    public MimeMessageBuilder setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public MimeMessageBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MimeMessageBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public MimeMessageBuilder setMailer(String mailer) {
        this.mailer = mailer;
        return this;
    }

    public MimeMessageBuilder setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
        return this;
    }

    public MimeMessageBuilder setAnswered(boolean answered) {
        this.answered = answered;
        return this;
    }

    public MimeMessageBuilder setRead(boolean read) {
        this.read = read;
        return this;
    }

    public MimeMessageBuilder setStarred(boolean starred) {
        this.starred = starred;
        return this;
    }

    public MimeMessageBuilder setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
        return this;
    }

    public MimeMessageBuilder setMDNSent(boolean mdnSent) {
        this.mdnSent = mdnSent;
        return this;
    }

    public MimeMessageBuilder setRecipientTo(String recipient) {
        try {
            recipientToList.add(new InternetAddress(recipient));
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return this;
    }

    public MimeMessageBuilder setRecipientCc(String recipient) {
        try {
            recipientCcList.add(new InternetAddress(recipient));
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return this;
    }

    public MimeMessageBuilder setRecipientBcc(String recipient) {
        try {
            recipientBccList.add(new InternetAddress(recipient));
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return this;
    }

    public MimeMessageBuilder setRecipientDispositionNotification(String recipient) {
        try {
            dispositionNotification.add(new InternetAddress(recipient));
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return this;
    }

    public String getFolder() {
        return folder;
    }

    public MailMessageAddress getSender() {
        return new MailMessageAddress(sender);
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    public String getMailer() {
        return mailer;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isStarred() {
        return starred;
    }

    public List<MailMessageAddress> getRecipientTo() {
        List<MailMessageAddress> l = new ArrayList<MailMessageAddress>();

        for (InternetAddress ia : recipientToList) {
            l.add(new MailMessageAddress(ia.toString()));
        }

        return l;
    }

    public List<MailMessageAddress> getRecipientCc() {
        List<MailMessageAddress> l = new ArrayList<MailMessageAddress>();

        for (InternetAddress ia : recipientCcList) {
            l.add(new MailMessageAddress(ia.toString()));
        }

        return l;
    }

    public List<MailMessageAddress> getRecipientBcc() {
        List<MailMessageAddress> l = new ArrayList<MailMessageAddress>();

        for (InternetAddress ia : recipientBccList) {
            l.add(new MailMessageAddress(ia.toString()));
        }

        return l;
    }

    public List<String> getAttachmentIdList() {
        List<String> l = new ArrayList<String>();

        for (String s : attachmentIdList) {
            l.add(s);
        }

        return l;
    }

    public List<MailMessageAddress> getDispositionNotification() {
        List<MailMessageAddress> l = new ArrayList<MailMessageAddress>();

        for (InternetAddress ia : dispositionNotification) {
            l.add(new MailMessageAddress(ia.toString()));
        }

        return l;
    }

    public boolean isAskForDispositionNotification() {
        return askForDispositionNotification;
    }

    public MimeMessageBuilder setAskForDispositionNotification(boolean askForDispositionNotification) {
        this.askForDispositionNotification = askForDispositionNotification;
        return this;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public boolean isDeleted() {
        return starred;
    }

    public MimeMessageBuilder setReceipt(boolean receipt) {
        this.receipt = receipt;
        return this;
    }

    public MimeMessageBuilder setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }
}
