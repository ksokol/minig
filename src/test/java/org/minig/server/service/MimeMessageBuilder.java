package org.minig.server.service;

import org.minig.server.MailMessageAddress;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class MimeMessageBuilder {

    // defaults
    private String messageId = "1";
    private String folder = "folder";
    private String sender = "sender@localhost";
    private String subject;
    private Date date;
    private String mailer;
    private boolean highPriority;
    private boolean answered;
    private boolean read;
    private boolean starred;
    private boolean askForDispositionNotification;
    private boolean receipt;
    private boolean deleted;
    private List<InternetAddress> recipientToList = new ArrayList<>();
    private List<InternetAddress> recipientCcList = new ArrayList<>();
    private List<InternetAddress> recipientBccList = new ArrayList<>();
    private List<InternetAddress> replyToList = new ArrayList<>();
    private List<InternetAddress> dispositionNotification = new ArrayList<>();
    private boolean forwarded;
    private boolean mdnSent;
    private String inReplyTo;
    private String forwardedMessageId;

    private String file;

    public static MimeMessageBuilder withSource(String file) {
        MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder();
        mimeMessageBuilder.file = file;
        return mimeMessageBuilder;
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
            when(m.getHeader("In-Reply-To")).thenReturn(new String[] { inReplyTo });

            when(m.match(any(SearchTerm.class))).then(invocation -> {
                Object[] arguments = invocation.getArguments();
                SearchTerm term = (SearchTerm) arguments[0];
                return term.match(m);
            });

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

            if(sender != null) {
                when(m.getFrom()).thenReturn(new Address[]{new InternetAddress(sender)});
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return m;
    }

    private <T> T readOnly(T mock) {
        return doThrow(new IllegalWriteException("IMAPMessage is read-only")).when(mock);
    }

    /**
     * Use {@link #spy()} instead.
     */
    @Deprecated
    public MimeMessage build(String file) {
        try {
            MimeMessage mimeMessage = new MimeMessage(null, new FileInputStream(file));

            MimeMessage spy = Mockito.spy(mimeMessage);
            Folder folderMock = Mockito.mock(Folder.class);

            when(folderMock.getFullName()).thenReturn(folder);
            when(spy.getFolder()).thenReturn(folderMock);

            /*
             * taken from com.sun.mail.imap.IMAPMessage
             */
            readOnly(spy).setHeader(anyString(), anyString());
            readOnly(spy).addHeader(anyString(), anyString());
            readOnly(spy).removeHeader(anyString());
            readOnly(spy).setFrom(Matchers.<Address>anyObject());
            readOnly(spy).addFrom(Matchers.<Address[]>anyObject());
            readOnly(spy).setSender(Matchers.<Address>anyObject());
            readOnly(spy).setRecipients(Matchers.<Message.RecipientType>anyObject(), Matchers.<Address[]>anyObject());
            readOnly(spy).addRecipients(Matchers.<Message.RecipientType>anyObject(), Matchers.<Address[]>anyObject());
            readOnly(spy).setReplyTo(Matchers.<Address[]>anyObject());
            readOnly(spy).setSubject(anyString(), anyString());
            readOnly(spy).setSentDate(Matchers.<Date>anyObject());
            readOnly(spy).setContentLanguage(Matchers.<String[]>anyObject());
            readOnly(spy).setDisposition(anyString());
            readOnly(spy).setContentID(anyString());
            readOnly(spy).setContentMD5(anyString());

            return spy;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MimeMessage spy() {
        try {
            MimeMessage mimeMessage = new MimeMessage(null, new FileInputStream(file));
            MimeMessage spy = Mockito.spy(mimeMessage);
            Folder folderMock = Mockito.mock(Folder.class);

            when(folderMock.getFullName()).thenReturn(folder);
            when(spy.getFolder()).thenReturn(folderMock);
            when(spy.getMessageID()).thenReturn(messageId);

            if(inReplyTo != null) {
                mimeMessage.setHeader("In-Reply-To", inReplyTo);
            }


            if(mdnSent) {
                spy.setFlags(new Flags("$MDNSent"), true);
            }

            if(forwardedMessageId != null) {
                mimeMessage.setHeader("X-Forwarded-Message-Id", forwardedMessageId);
            }

            if(receipt) {
                mimeMessage.setHeader("X-Mozilla-Draft-Info", "receipt=1");
            }

            if(askForDispositionNotification) {
                String[] header = mimeMessage.getHeader("X-Mozilla-Draft-Info");

                if(header != null && header.length == 1) {
                    mimeMessage.setHeader("X-Mozilla-Draft-Info", header[0] + ";DSN=1");
                } else {
                    mimeMessage.setHeader("X-Mozilla-Draft-Info", "DSN=1");
                }
            }

            if(highPriority) {
                mimeMessage.setHeader("X-Priority", "1");
            }

            if(mailer != null) {
                mimeMessage.setHeader("User-Agent", mailer);
            }

            if (!dispositionNotification.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (InternetAddress internetAddress : dispositionNotification) {
                    sb.append(internetAddress.toString()).append(", ");
                }
                mimeMessage.setHeader("Disposition-Notification-To", sb.substring(0, sb.length() - 2));
            }

            mimeMessage.setRecipients(RecipientType.BCC, recipientBccList.toArray(new Address[recipientBccList.size()]));
            mimeMessage.setRecipients(RecipientType.CC, recipientCcList.toArray(new Address[recipientCcList.size()]));
            mimeMessage.setRecipients(RecipientType.TO, recipientToList.toArray(new Address[recipientToList.size()]));
            mimeMessage.setReplyTo(replyToList.toArray(new Address[replyToList.size()]));

            return spy;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
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

    public MimeMessageBuilder setRecipientTo(String recipient, String personal) {
        try {
            recipientToList.add(new InternetAddress(recipient, personal));
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
        return this;
    }

    public MimeMessageBuilder setRecipientTo(List<InternetAddress> recipients) {
        if(recipients == null) {
            recipientToList = Collections.emptyList();
        } else {
            recipientToList = recipients;
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

    public MimeMessageBuilder setRecipientCc(String recipient, String personal) {
        try {
            recipientCcList.add(new InternetAddress(recipient, personal));
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
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

    public MimeMessageBuilder setRecipientBcc(String recipient, String personal) {
        try {
            recipientBccList.add(new InternetAddress(recipient, personal));
        } catch (UnsupportedEncodingException e) {
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

    public MimeMessageBuilder setRecipientDispositionNotification(String recipient, String personal) {
        try {
            dispositionNotification.add(new InternetAddress(recipient, personal));
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
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

    public MimeMessageBuilder setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
        return this;
    }

    public MimeMessageBuilder setReplyTo(String replyTo) {
        try {
            replyToList.add(new InternetAddress(replyTo));
        } catch (AddressException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
        return this;
    }

    public MimeMessageBuilder setReplyTo(String replyTo, String personal) {
        try {
            replyToList.add(new InternetAddress(replyTo, personal));
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
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

    public MimeMessageBuilder setForwardedMessageId(String forwardedMessageId) {
        this.forwardedMessageId = forwardedMessageId;
        return this;
    }
}
