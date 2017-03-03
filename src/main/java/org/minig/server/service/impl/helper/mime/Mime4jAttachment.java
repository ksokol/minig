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
    private final String mimeType;
    private final InputStream data;

    public Mime4jAttachment(String filename, String mimeType, InputStream data) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(mimeType);
        this.filename = filename;
        this.mimeType = mimeType;
        this.data = data;
    }

    public CompositeAttachmentId getId() {
        if(id == null) {
            throw new IllegalStateException("id is null");
        }
        return new CompositeAttachmentId(id.getFolder(), id.getMessageId(), filename);
    }

    public String getFilename() {
        return filename;
    }

    @Deprecated
    public void setFilename(String filename) {
        this.filename = filename;
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
}
