package org.minig.server.service;

import config.ServiceTestConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.TestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class AttachmentServiceTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private AttachmentService uut;

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
    }

    @Test
    public void testFindAttachments_hasAttachments() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_ATTACHMENT);

        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId(m.getMessageID());

        mockServer.prepareMailBox("INBOX", m);

        MailAttachmentList findAttachments = uut.findAttachments(id);

        assertEquals(2, findAttachments.getAttachmentMetadata().size());
    }

    @Test
    public void testFindAttachments_hasNoAttachments() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId(m.getMessageID());

        mockServer.prepareMailBox("INBOX", m);

        MailAttachmentList findAttachments = uut.findAttachments(id);

        assertEquals(0, findAttachments.getAttachmentMetadata().size());
    }

    @Test
    public void testFindAttachments_noMessage() {
        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId("<id@localhost>");

        MailAttachmentList findAttachments = uut.findAttachments(id);

        assertEquals(0, findAttachments.getAttachmentMetadata().size());
    }

    @Test(expected = NotFoundException.class)
    public void testFindAttachment_doesNotExist() {
        uut.findAttachment(new CompositeAttachmentId("INBOX", "<id@localhost>", "fake"));
    }

    @Test
    public void testFindAttachment() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        MailAttachment findAttachment = uut.findAttachment(id);

        assertEquals("1.png", findAttachment.getFileName());
    }

    @Test(expected = NotFoundException.class)
    public void testReadAttachment_hasNoAttachment() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        uut.readAttachment(id, new ByteArrayOutputStream());
    }

    @Test
    public void testReadAttachment_hasAttachment() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        uut.readAttachment(id, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byte[] expected = IOUtils.toByteArray(new FileInputStream(TestConstants.ATTACHMENT_IMAGE_1_PNG));

        assertTrue(Arrays.equals(expected, byteArray));
    }


	@Test
	public void testReadAttachment_hasAttachment2() throws Exception {
		MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_RFC_2231_2);
		CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "umlaut ä veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long.png");

		mockServer.prepareMailBox("INBOX", m);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		uut.readAttachment(id, byteArrayOutputStream);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		byte[] expected = IOUtils.toByteArray(new FileInputStream(TestConstants.MULTIPART_RFC_2231_2_IMAGE));

		assertThat(byteArray, is(expected));
	}
}
