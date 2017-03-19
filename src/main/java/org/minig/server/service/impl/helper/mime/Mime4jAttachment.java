package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Kamill Sokol
 */
public final class Mime4jAttachment {

    private CompositeAttachmentId id;
    private String filename;
    private final String contentId;
    private final String dispositionType;
    private final String mimeType;
    private final InputStream data;

    public Mime4jAttachment(CompositeId CompositeId, String filename, String mimeType, InputStream data) {
        this(CompositeId, filename, null, "attachment", mimeType, data);
    }

    public Mime4jAttachment(CompositeId compositeId, String filename, String contentId, String dispositionType, String mimeType, InputStream data) {
        Objects.requireNonNull(compositeId);
        this.id = new CompositeAttachmentId(compositeId.getFolder(), compositeId.getMessageId(), "attachment".equals(dispositionType) ? filename : contentId);
        this.filename = Objects.requireNonNull(filename);
        this.mimeType = Objects.requireNonNull(mimeType);
        this.contentId = contentId;
        this.dispositionType = dispositionType;
        this.data = data;
    }

    public CompositeAttachmentId getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentId() {
        return contentId;
    }

    public String getDispositionType() {
        return dispositionType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getData() {
        return data;
    }

    public boolean isAttachment() {
        return "attachment".equals(dispositionType);
    }

    public boolean isInlineAttachment() {
        return "inline".equals(dispositionType);
    }

}
