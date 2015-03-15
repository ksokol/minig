package org.minig.jawr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;

import net.jawr.web.resource.handler.reader.BaseServletContextResourceReader;

/**
 * @author Kamill Sokol
 */
public class ClassPathResourceReader extends BaseServletContextResourceReader {

    @Override
    public InputStream getResourceAsStream(final String resourceName) {
        try {
            return new ClassPathResource(resourceName).getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public InputStream getResourceAsStream(final String resourceName, final boolean processingBundle) {
        return super.getResourceAsStream(resourceName);
    }

    @Override
    protected boolean isAccessPermitted(final String resourceName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getResourceNames(final String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory(final String path) {
        throw new UnsupportedOperationException();
    }
}
