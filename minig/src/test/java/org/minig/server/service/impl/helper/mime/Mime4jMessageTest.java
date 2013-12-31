package org.minig.server.service.impl.helper.mime;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.activation.FileDataSource;

import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.minig.server.TestConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Mime4jMessageTest {

    private MessageServiceFactoryImpl messageServiceFactory = new MessageServiceFactoryImpl();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private Mime4jMessage freshMime4jMessage(String fromTestMail) throws Exception {
        MessageBuilder newMessageBuilder = messageServiceFactory.newMessageBuilder();

        InputStream decodedInput = new FileInputStream(new File(fromTestMail));

        MessageImpl parseMessage = (MessageImpl) newMessageBuilder.parseMessage(decodedInput);

        return new Mime4jMessage(parseMessage);
    }

    @Test
    public void testSetPlain() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setPlain("replacing plain text");

        assertEquals("replacing plain text", mime4jMessage.getPlain());
    }

    @Test
    public void testSetPlain2() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain3() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain4() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setPlain("This is a message written solely for testing.");

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));
        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
    }

    @Test
    public void testSetPlain5() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.HTML);

        assertEquals(0, mime4jMessage.getAttachments().size());

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));

        assertEquals(1, mime4jMessage.getAttachments().size());
    }

    @Test
    public void testSetMultipart6() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);

        assertEquals("plain text", mime4jMessage.getPlain().trim());
        assertEquals("", mime4jMessage.getHtml().trim());

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetMultipart7() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        assertTrue(mime4jMessage.getPlain().contains("Pingdom Monthly Report"));
        assertTrue(mime4jMessage.getHtml().contains("<table width="));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetMultipart8() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        assertEquals("", mime4jMessage.getPlain().trim());
        assertEquals("", mime4jMessage.getHtml().trim());

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetMultipart9() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_HTML_AND_ATTACHMENT);

        assertEquals("", mime4jMessage.getPlain());
        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain6() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.PLAIN);

        assertEquals(0, mime4jMessage.getAttachments().size());

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));

        assertEquals(1, mime4jMessage.getAttachments().size());
    }

    @Test
    public void testSetPlain7() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        assertEquals(0, mime4jMessage.getAttachments().size());

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));

        assertEquals(1, mime4jMessage.getAttachments().size());
    }

    @Test
    public void testSetPlain8() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        assertEquals(2, mime4jMessage.getAttachments().size());

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));

        assertEquals(3, mime4jMessage.getAttachments().size());
    }

    @Test
    public void testSetPlain9() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        assertEquals(2, mime4jMessage.getAttachments().size());

        mime4jMessage.deleteAttachment("2.png");

        assertEquals(1, mime4jMessage.getAttachments().size());
    }

    @Test
    public void testDSN() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertFalse(mime4jMessage.isDispositionNotification());

        mime4jMessage = freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER);
        assertTrue(mime4jMessage.isDispositionNotification());
    }

    @Test
    public void testAddTo() throws Exception {
        Mime4jMessage mime4jMessage = freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        mime4jMessage.addTo("test1@localhost");
        mime4jMessage.addTo("test1@localhost");
        mime4jMessage.addTo("test2@localhost");

        System.out.println(mime4jMessage.getMessage().getTo());
    }
}
