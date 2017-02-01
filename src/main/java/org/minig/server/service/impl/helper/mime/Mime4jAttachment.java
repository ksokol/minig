package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Kamill Sokol
 */
public final class Mime4jAttachment {
    private final CompositeAttachmentId id;
    private final String mimeType;
    private final InputStream data;

    public Mime4jAttachment(CompositeId compositeId, String filename, String mimeType, InputStream data) {
        Objects.requireNonNull(compositeId);
        Objects.requireNonNull(filename);
        Objects.requireNonNull(mimeType);
        Objects.requireNonNull(data);
        this.id = new CompositeAttachmentId(compositeId.getFolder(), compositeId.getMessageId(), filename);
        this.mimeType = mimeType;
        this.data = data;
    }

    public CompositeAttachmentId getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getData() {
        return data;
    }
}
