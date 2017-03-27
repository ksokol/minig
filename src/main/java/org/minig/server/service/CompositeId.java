package org.minig.server.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.minig.server.resource.config.CompositeIdSerializer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Kamill Sokol
 *
 * TODO move id parse to JsonDeserializer
 */
public class CompositeId {

    static final String SEPARATOR = "|";

    @JsonSerialize(using = CompositeIdSerializer.class)
    protected String id;
    private String messageId;
    private String folder;

    public CompositeId() {
    }

    public CompositeId(MimeMessage mimeMessage) {
        try {
            this.folder = mimeMessage.getFolder().getFullName();
            this.messageId = mimeMessage.getMessageID();
        } catch (MessagingException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }

    public CompositeId(String id) {
        setId(id);
    }

    public CompositeId(String folder, String messageId) {
        this.folder = folder;
        this.messageId = messageId;
        buildId();
    }

    public String getId() {
        buildId();
        return id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        if (messageId != null) {
            this.messageId = messageId;
            // buildId();
        }
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        if (folder != null) {
            this.folder = folder;
            // buildId();
        }
    }

    public void setId(String id) {
        if (id != null && folder == null && messageId == null) {
            try {
                // TODO double decode id e.g. INBOX%252Ftest -> INBOX%2Ftest -> INBOX/test
                String decodedId = URLDecoder.decode(URLDecoder.decode(id, UTF_8.name()), UTF_8.name());
                splitAndSet(decodedId);
            } catch (UnsupportedEncodingException exception) {
                throw new IllegalArgumentException(exception.getMessage(), exception);
            }
        }
    }

    private void splitAndSet(String decodedId) {
        String[] split = decodedId.split("\\" + SEPARATOR);

        if (split.length > 1) {
            messageId = split[1];
            folder = split[0];
            buildId();
        }
    }

    public void setCompositeId(CompositeId id) {
        setId(id.getId());
    }

    protected void buildId() {
        if (id == null && folder != null && messageId != null) {
            id = folder + SEPARATOR + messageId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeId)) return false;
        CompositeId that = (CompositeId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringBuilder(folder).append(SEPARATOR).append(messageId).toString();
    }
}
