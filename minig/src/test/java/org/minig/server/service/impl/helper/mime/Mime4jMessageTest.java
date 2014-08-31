package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.junit.Test;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;

import javax.activation.FileDataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Kamill Sokol
 */
public class Mime4jMessageTest {

    @Test
    public void testSetPlain() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setPlain("replacing plain text");

        assertEquals("replacing plain text", mime4jMessage.getPlain());
    }

    @Test
    public void testSetPlain2() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain3() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain4() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setPlain("This is a message written solely for testing.");

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));
        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
    }

    @Test
    public void testSetPlain5() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
		assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetMultipart6() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);

        assertThat(mime4jMessage.getPlain(), is(equalToIgnoringWhiteSpace("plain text")));
        assertThat(mime4jMessage.getHtml(), is(""));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertThat(mime4jMessage.getPlain(), is(replacedBody));
        assertThat(mime4jMessage.getHtml(), is(replacedBody));
    }

    @Test
    public void testSetMultipart7() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

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
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

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
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_HTML_AND_ATTACHMENT);

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
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetPlain7() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetPlain8() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
		assertThat(mime4jMessage.getAttachments(), hasSize(2));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
		assertThat(mime4jMessage.getAttachments(), hasSize(3));
    }

    @Test
    public void testSetPlain9() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
		assertThat(mime4jMessage.getAttachments(), hasSize(2));

        mime4jMessage.deleteAttachment("2.png");
		assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testNoDSN() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(false));
    }

    @Test
    public void testDSN() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_1);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(true));
        assertThat(mime4jMessage.isDSN(), is(true));
    }

    @Test
    public void testDSNReceipt() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_2);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(true));
        assertThat(mime4jMessage.isReturnReceipt(), is(true));
    }

    @Test
    public void testAddTo() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        mime4jMessage.addTo("testuser1@localhost");
        ArrayList<Address> addresses = new ArrayList<>(3);
        addresses.add(new Mailbox("testuser", "localhost"));
        addresses.add(new Mailbox("testuser1", "localhost"));
        AddressList addressList = new AddressList(addresses, false);

        assertThat(mime4jMessage.getMessage().getTo(), is(addressList));
    }

	@Test
	public void testMime4jAttachment() throws Exception {
		Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_ATTACHMENT_BINARY);
		mime4jMessage.setId(new CompositeId("folder", "messageId"));
		assertThat(mime4jMessage.getAttachments(), hasSize(1));

		Mime4jAttachment attachment = mime4jMessage.getAttachments().get(0);
		assertThat(attachment.getId().getId(), is("folder|messageId|umlaut ä.png"));
	}

    @Test
    public void testMime4jNestedMessage() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.NESTED_MESSAGE);

        assertThat(mime4jMessage.getAttachments(), hasSize(1));

        Mime4jAttachment attachment = mime4jMessage.getAttachment("Disposition Notification Test.eml");
        String text = new Scanner(attachment.getData()).useDelimiter("\\A").next();

        assertThat(attachment.getMimeType(), is("text/plain"));
        assertThat(text, equalToIgnoringWhiteSpace("Body nested"));
    }
}
