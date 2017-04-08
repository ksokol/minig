package org.minig.server.service;

import config.ServiceTestConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailAttachment;
import org.minig.server.MailMessage;
import org.minig.server.TestConstants;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.minig.server.TestConstants.BODY_A;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class AttachmentRepositoryTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private AttachmentRepository uut;

    @Autowired
    private MailRepository mailRepository;

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
    }

    @Test
    public void testReadMetadata_fakeMessage() throws MessagingException, IOException {
        MailMessage mm = new MailMessage();
        mm.setMessageId("fakeId");
        mm.setFolder("fakeFolder");
        mm.setAttachments(Collections.EMPTY_LIST);

        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm);

        assertEquals(0, attachmentMetadata.size());
    }

    @Test
    public void testReadMetadata_hasNoAttachements() throws MessagingException, IOException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        mockServer.prepareMailBox("INBOX", m);
        MailMessage mm = mailRepository.read(id);

        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm);

        assertEquals(0, attachmentMetadata.size());
    }

    @Test
    public void testReadMetadata() throws MessagingException, IOException {
        MimeMessage m = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        Mailbox inbox = new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        inbox.add(m);

        MailMessage mm = mailRepository.read(id);

        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm);

        MailAttachment mailAttachment1 = uut.readMetadata(mm).get(0);
        MailAttachment mailAttachment2 = uut.readMetadata(mm).get(1);

        assertThat(attachmentMetadata, hasSize(2));
        assertThat(mailAttachment1.getFileName(), is("1.png"));

        CompositeId expected = new CompositeAttachmentId("INBOX", m.getMessageID(), mailAttachment1.getFileName());

        assertThat(expected.getId(), is(mailAttachment1.getId()));
        assertThat(mailAttachment1.getMime(), is(equalToIgnoringWhiteSpace("image/png")));

        assertThat(mailAttachment2.getFileName(), is("2.png"));

        expected = new CompositeAttachmentId("INBOX", m.getMessageID(), mailAttachment2.getFileName());

        assertThat(expected.getId(), is(mailAttachment2.getId()));
        assertThat(mailAttachment2.getMime(), is(equalToIgnoringWhiteSpace("image/png")));
    }

    @Test
    public void testReadAttachmentPayload() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_ATTACHMENT);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        InputStream readAttachmentPayload = uut.readAttachmentPayload(id);

        byte[] byteArray = IOUtils.toByteArray(readAttachmentPayload);

        assertTrue(Arrays.equals(BODY_A, byteArray));
    }

    @Test(expected = NotFoundException.class)
    public void testReadAttachmentPayload_noAttachment() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        uut.readAttachmentPayload(id);
    }

    @Test
    public void testAppendMultipartAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        mockServer.prepareMailBox("INBOX.Drafts", m);

        List<MailAttachment> readMetadata = uut.readMetadata(new CompositeId("INBOX.Drafts", m.getMessageID()));
        assertEquals(0, readMetadata.size());

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File(TestConstants.ATTACHMENT_IMAGE_FOLDER_GIF)));

        List<MailAttachment> readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertThat(read.getPlain().length(), greaterThanOrEqualTo(1449)); //ignore line endings
        assertTrue(read.getPlain().contains("From: 2013-04-25 09:35:54, To: 2013-04-25 09:44:54, Downtime: 0h 09m 00s"));

        assertThat(read.getHtml().length(), greaterThanOrEqualTo(25257));
        assertTrue(read.getHtml().contains("<td><br><h3>178.254.55.49</h3></td>"));

        assertEquals(1, readMetadata2.size());
        assertEquals("folder.gif", readMetadata2.get(0).getFileName());
    }

    @Test
    public void testAppendHtmlAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.HTML);

        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File(TestConstants.ATTACHMENT_IMAGE_FOLDER_GIF)));

        List<MailAttachment> readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertThat(read.getPlain(), isEmptyString());
        assertThat(read.getHtml().length(), greaterThanOrEqualTo(173)); //ignore line endings
        assertThat(read.getHtml(), containsString("</body>"));

        assertThat(readMetadata2, hasSize(1));
        assertThat(readMetadata2.get(0).getFileName(), is("folder.gif"));
    }

    @Test
    public void testAppendPlainAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.PLAIN);

        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File(TestConstants.ATTACHMENT_IMAGE_FOLDER_GIF)));

        List<MailAttachment> readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertThat(read.getPlain().length(), greaterThanOrEqualTo(70)); //ignore line endings
        assertTrue(read.getPlain().contains("row with text"));

        assertThat(read.getHtml(), isEmptyString());

        assertEquals(1, readMetadata2.size());
        assertEquals("folder.gif", readMetadata2.get(0).getFileName());
    }
}
