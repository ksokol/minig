package org.minig.server.resource.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.minig.server.service.CompositeAttachmentId;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class CompositeAttachmentIdSerializerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() {
        SimpleModule testModule = new SimpleModule("MinigModule", new Version(1, 0, 0, null));
        testModule.addSerializer(CompositeAttachmentId.class, new CompositeAttachmentIdSerializer());
        om.registerModule(testModule);
    }

    @Test
    public void testSimple() throws IOException {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("folder|messageId|filename");

        StringWriter json = new StringWriter();
        om.writeValue(json, compositeAttachmentId);

        assertThat(json.toString(), is("{\"id\":\"folder|messageId|filename\",\"messageId\":\"messageId\",\"folder\":\"folder\",\"fileName\":\"filename\"}"));
    }

    @Test
    public void testSubDelimiters() throws IOException {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("folder|message+Id|file+name");

        StringWriter json = new StringWriter();
        om.writeValue(json, compositeAttachmentId);

        assertThat(json.toString(), is("{\"id\":\"folder|message%252BId|file%252Bname\",\"messageId\":\"message%252BId\",\"folder\":\"folder\",\"fileName\":\"file+name\"}"));
    }

}
