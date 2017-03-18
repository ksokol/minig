package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Kamill Sokol
 */
public final class Mime4jAttachment {

    private CompositeId id;
    private String filename;
    private final String contentId;
    private final String dispositionType;
    private final String mimeType;
    private final InputStream data;

    public Mime4jAttachment(String filename, String mimeType, InputStream data) {
        this(filename, null, "attachment", mimeType, data);
    }

    public Mime4jAttachment(String filename, String contentId, String dispositionType, String mimeType, InputStream data) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(mimeType);
        this.filename = filename;
        this.contentId = contentId;
        this.dispositionType = dispositionType;
        this.mimeType = mimeType;
        this.data = data;
    }

    public CompositeAttachmentId getId() {
        if(id == null) {
            throw new IllegalStateException("id is null");
        }
        return new CompositeAttachmentId(id.getFolder(), id.getMessageId(), isAttachment() ? filename : contentId);
    }

    public String getFilename() {
        return filename;
    }

    @Deprecated
    public void setFilename(String filename) {
        this.filename = filename;
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

    public void setId(CompositeId id) {
        this.id = id;
    }

    public boolean isAttachment() {
        return "attachment".equals(dispositionType);
    }

    public boolean isInlineAttachment() {
        return "inline".equals(dispositionType);
    }

}
