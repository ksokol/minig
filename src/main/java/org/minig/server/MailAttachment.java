package org.minig.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;

import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
public class MailAttachment extends CompositeAttachmentId {

    private String mime;
    private String contentId;
    private String dispositionType;

    @JsonIgnore
    private InputStream data;

    public MailAttachment(Mime4jAttachment mime4jAttachment) {
        this(mime4jAttachment.getId(), mime4jAttachment.getMimeType(), mime4jAttachment.getContentId(), mime4jAttachment.getDispositionType(), mime4jAttachment.getData());
    }

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
