package org.minig.server.service.impl.helper.mime;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Kamill Sokol
 */
public class StringDataSource implements DataSource {

    private final String content;

    public StringDataSource(String content) {
        this.content = content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getName() {
        return content;
    }
}
