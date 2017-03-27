package org.minig.server;

import org.minig.server.service.CompositeAttachmentId;

import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
public class MailAttachment extends CompositeAttachmentId {

    private String mime;
    private String contentId;
    private String dispositionType;
    private InputStream data;

    public MailAttachment(CompositeAttachmentId compositeAttachmentId, String mime, String contentId, String dispositionType, InputStream data) {
        super(compositeAttachmentId.getFolder(), compositeAttachmentId.getMessageId(), compositeAttachmentId.getFileName());
        this.mime = mime;
        this.contentId = contentId;
        this.dispositionType = dispositionType;
        this.data = data;
    }

    public String getMime() {
        return mime;
    }

    public String getContentId() {
        return contentId;
    }

    public String getDispositionType() {
        return dispositionType;
    }

    public InputStream getData() {
        return data;
    }
}
