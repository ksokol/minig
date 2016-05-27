package org.minig.server.service.impl;

import org.minig.server.service.impl.helper.mime.Mime4jAttachment;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Kamill Sokol
 */
class Mime4jAttachmentDataSource implements DataSource {

    private final Mime4jAttachment attachment;

    public Mime4jAttachmentDataSource(Mime4jAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return attachment.getData();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return attachment.getMimeType();
    }

    @Override
    public String getName() {
        return attachment.getId().getFileName();
    }
}
