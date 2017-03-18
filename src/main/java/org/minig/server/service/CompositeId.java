package org.minig.server.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Kamill Sokol
 */
public class CompositeId {

    public static final String SEPARATOR = "|";

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
            String[] split = id.split("\\" + SEPARATOR);

            if (split != null && split.length > 1) {
                messageId = split[1];
                folder = split[0];
                buildId();
            }
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
    public String toString() {
        return new StringBuilder(folder).append(SEPARATOR).append(messageId).toString();
    }
}
