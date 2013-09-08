package org.minig.server.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.MailMessage;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class AttachmentRepositoryImplTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private AttachmentRepositoryImpl uut;

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

        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm).getAttachmentMetadata();

        assertEquals(0, attachmentMetadata.size());
    }

    @Test
    public void testReadMetadata_hasNoAttachements() throws MessagingException, IOException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testBody.mail");

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        mockServer.prepareMailBox("INBOX", m);
        MailMessage mm = mailRepository.read(id);

        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm).getAttachmentMetadata();

        assertEquals(0, attachmentMetadata.size());
    }

    @Test
    public void testReadMetadata() throws MessagingException, IOException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testAttachmentId.mail");

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        mockServer.prepareMailBox("INBOX", m);

        MailMessage mm = mailRepository.read(id);
        List<MailAttachment> attachmentMetadata = uut.readMetadata(mm).getAttachmentMetadata();

        MailAttachment mailAttachment1 = uut.readMetadata(mm).getAttachmentMetadata().get(0);
        MailAttachment mailAttachment2 = uut.readMetadata(mm).getAttachmentMetadata().get(1);

        assertEquals(2, attachmentMetadata.size());

        assertEquals("1.png", mailAttachment1.getFileName());

        CompositeId expected = new CompositeAttachmentId("INBOX", m.getMessageID(), mailAttachment1.getFileName());

        assertEquals(expected.getId(), mailAttachment1.getId());
        assertEquals("IMAGE/PNG; name=1.png", mailAttachment1.getMime());
        assertEquals(180702, mailAttachment1.getSize());

        assertEquals("2.png", mailAttachment2.getFileName());

        expected = new CompositeAttachmentId("INBOX", m.getMessageID(), mailAttachment2.getFileName());

        assertEquals(expected.getId(), mailAttachment2.getId());
        assertEquals("IMAGE/PNG; name=2.png", mailAttachment2.getMime());
        assertEquals(181998, mailAttachment2.getSize());
    }

    @Test
    public void testRead_hasNoAttachments() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testBody.mail");

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        MailAttachment read = uut.read(id);
        assertTrue(read == null);
    }

    @Test
    public void testRead_hasAttachments() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testAttachmentId.mail");

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        MailAttachment read = uut.read(id);

        assertEquals("1.png", read.getFileName());
        assertEquals(id.getId(), read.getId());
        assertEquals("IMAGE/PNG; name=1.png", read.getMime());
        assertEquals(180702, read.getSize());
    }

    @Test
    public void testReadAttachmentPayload() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testAttachmentId.mail");

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        InputStream readAttachmentPayload = uut.readAttachmentPayload(id);

        byte[] byteArray = IOUtils.toByteArray(readAttachmentPayload);
        byte[] expected = IOUtils.toByteArray(new FileInputStream("src/test/resources/1.png"));

        assertTrue(Arrays.equals(expected, byteArray));
    }

    @Test(expected = NotFoundException.class)
    public void testReadAttachmentPayload_noAttachment() throws Exception {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testBody.mail");

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX", m.getMessageID(), "1.png");

        mockServer.prepareMailBox("INBOX", m);

        uut.readAttachmentPayload(id);
    }

    @Test
    public void testAppendMultipartAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testBody.mail");

        mockServer.prepareMailBox("INBOX.Drafts", m);

        MailAttachmentList readMetadata = uut.readMetadata(new CompositeId("INBOX.Drafts", m.getMessageID()));
        assertEquals(0, readMetadata.getAttachmentMetadata().size());

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File("src/test/resources/folder.gif")));

        MailAttachmentList readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertEquals(1489, read.getBody().getPlain().length());
        assertTrue(read.getBody().getPlain().contains("From: 2013-04-25 09:35:54, To: 2013-04-25 09:44:54, Downtime: 0h 09m 00s"));

        assertEquals(25336, read.getBody().getHtml().length());
        assertTrue(read.getBody().getHtml().contains("<td><br><h3>178.254.55.49</h3></td></tr>"));

        assertEquals(1, readMetadata2.getAttachmentMetadata().size());
        assertEquals("folder.gif", readMetadata2.getAttachmentMetadata().get(0).getFileName());
    }

    @Test
    public void testAppendHtmlAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testAppendHtmlAttachment.mail");

        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File("src/test/resources/folder.gif")));

        MailAttachmentList readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertTrue(read.getBody().getPlain().isEmpty());

        assertEquals(180, read.getBody().getHtml().length());
        assertTrue(read.getBody().getHtml().contains("</body>"));

        assertEquals(1, readMetadata2.getAttachmentMetadata().size());
        assertEquals("folder.gif", readMetadata2.getAttachmentMetadata().get(0).getFileName());
    }

    @Test
    public void testAppendPlainAttachment() throws MessagingException, InterruptedException {
        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testAppendPlainAttachment.mail");

        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeAttachmentId id = new CompositeAttachmentId("INBOX.Drafts", m.getMessageID(), "folder.gif");
        CompositeId appendAttachmentId = uut.appendAttachment(id, new FileDataSource(new File("src/test/resources/folder.gif")));

        MailAttachmentList readMetadata2 = uut.readMetadata(appendAttachmentId);
        MailMessage read = mailRepository.read(id);

        assertEquals(71, read.getBody().getPlain().length());
        assertTrue(read.getBody().getPlain().contains("row with text"));

        assertTrue(read.getBody().getHtml() == null);

        assertEquals(1, readMetadata2.getAttachmentMetadata().size());
        assertEquals("folder.gif", readMetadata2.getAttachmentMetadata().get(0).getFileName());
    }
}
