package org.minig.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.minig.server.resource.config.CompositeIdSerializer;
import org.minig.server.service.CompositeId;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static org.minig.MinigConstants.FORWARDED;

/**
 * @author Kamill Sokol
 */
public class PartialMailMessage {

    private final MimeMessage mimeMessage;
    private final CompositeId compositeId;

    public PartialMailMessage(MimeMessage mimeMessage) {
        this.mimeMessage = Objects.requireNonNull(mimeMessage, "mimeMessage is null");
        this.compositeId = new CompositeId(mimeMessage);
    }

    @JsonSerialize(using = CompositeIdSerializer.class)
    public String getId() {
        return compositeId.getId();
    }

    public String getMessageId() {
        return compositeId.getMessageId();
    }

    public String getFolder() {
        return compositeId.getFolder();
    }

    public String getSubject() throws MessagingException {
        return mimeMessage.getSubject();
    }

    public MailMessageAddress getSender() throws MessagingException {
        Address[] addresses = mimeMessage.getFrom();

        if (addresses == null) {
            return new MailMessageAddress();
        }

        InternetAddress internetAddress = (InternetAddress) addresses[0];
        String personal = internetAddress.getPersonal();

        if (personal != null) {
            return new MailMessageAddress(personal, internetAddress.getAddress());
        }

        return new MailMessageAddress(internetAddress.toString());
    }

    public Date getDate() throws MessagingException {
        return mimeMessage.getSentDate();
    }

    public boolean getForwarded() throws MessagingException {
        return hasUserFlag(FORWARDED);
    }

    public boolean getRead() throws MessagingException {
        return hasFlag(Flags.Flag.SEEN);
    }

    public boolean getStarred() throws MessagingException {
        return hasFlag(Flags.Flag.FLAGGED);
    }

    public boolean getAnswered() throws MessagingException {
        return hasFlag(Flags.Flag.ANSWERED);
    }

    public Boolean getDeleted() throws MessagingException {
        return hasFlag(Flags.Flag.DELETED);
    }

    private boolean hasFlag(Flags.Flag expectedFlag) throws MessagingException {
        return Arrays.stream(mimeMessage.getFlags().getSystemFlags()).anyMatch(flag -> flag == expectedFlag);
    }

    private boolean hasUserFlag(String flag) throws MessagingException {
        return mimeMessage.getFlags().getUserFlags() != null && Arrays.stream(mimeMessage.getFlags().getUserFlags()).anyMatch(flag::equals);
    }
}
