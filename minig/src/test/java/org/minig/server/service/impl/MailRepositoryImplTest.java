package org.minig.server.service.impl;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class MailRepositoryImplTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private MailRepositoryImpl uut;

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
    }

    @Test
    public void testRead() throws MessagingException {
        List<MimeMessage> mList = new MimeMessageBuilder().build(2);

        mockServer.prepareMailBox("INBOX", mList);

        CompositeId id = new CompositeId("INBOX", mList.get(0).getMessageID());
        MailMessage mm = uut.read(id);

        assertEquals(id.getId(), mm.getId());
    }

    @Test
    public void testFolderCountVariance() {
        MimeMessageBuilder builder = new MimeMessageBuilder();

        assertEquals(0, uut.findByFolder("INBOX", 1, 20).getFullLength());

        mockServer.prepareMailBox("INBOX.testfolder", builder.build(), builder.build());

        assertEquals(0, uut.findByFolder("INBOX", 1, 20).getFullLength());
        assertEquals(2, uut.findByFolder("INBOX.testfolder", 1, 20).getFullLength());

        mockServer.prepareMailBox("INBOX.testfolder", builder.build(25));

        assertEquals(25, uut.findByFolder("INBOX.testfolder", 1, 20).getFullLength());
        assertEquals(20, uut.findByFolder("INBOX.testfolder", 1, 20).getMailList().size());
        assertEquals(5, uut.findByFolder("INBOX.testfolder", 2, 20).getMailList().size());
        assertEquals(25, uut.findByFolder("INBOX.testfolder", 1, 25).getMailList().size());
        assertEquals(0, uut.findByFolder("INBOX.testfolder", 2, 25).getMailList().size());
        assertEquals(0, uut.findByFolder("INBOX.testfolder", 2, 26).getMailList().size());
        assertEquals(13, uut.findByFolder("INBOX.testfolder", 1, 13).getMailList().size());
        assertEquals(0, uut.findByFolder("INBOX.testfolder", 3, 13).getMailList().size());
        assertEquals(0, uut.findByFolder("INBOX.testfolder", -5, -5).getMailList().size());
    }

    @Test
    public void testUpdateFlags() throws MessagingException {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.setStarred(false).setAnswered(false).setHighPriority(false).setRead(false).build();

        mockServer.prepareMailBox("INBOX", m);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        MailMessage read = uut.read(id);

        assertFalse(read.getStarred());
        assertFalse(read.getAnswered());
        assertFalse(read.getHighPriority());
        assertFalse(read.getRead());

        read.setStarred(true);
        read.setAnswered(true);
        read.setHighPriority(true);
        read.setRead(true);

        uut.updateFlags(read);

        id = new CompositeId("INBOX", m.getMessageID());
        read = uut.read(id);

        assertTrue(read.getStarred());
        assertTrue(read.getAnswered());
        assertFalse(read.getHighPriority());
        assertTrue(read.getRead());
    }

    @Test
    public void testUpdateUserFlags() throws MessagingException {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.setForwarded(false).setMDNSent(false).build();

        mockServer.prepareMailBox("INBOX", m);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        MailMessage read = uut.read(id);

        assertFalse(read.getForwarded());
        assertFalse(read.getMdnSent());

        read.setForwarded(true);
        read.setMdnSent(true);

        uut.updateFlags(read);

        id = new CompositeId("INBOX", m.getMessageID());
        read = uut.read(id);

        assertTrue(read.getForwarded());
        assertTrue(read.getMdnSent());
    }

    @Test
    public void testMoveMessage() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", m);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        MailMessage read = uut.read(id);

        mockServer.createAndSubscribeMailBox("INBOX.testUpdateFolder");
        uut.moveMessage(read, "INBOX.testUpdateFolder");

        MailMessageList findByFolder = uut.findByFolder("INBOX.testUpdateFolder", 1, 1);
        assertEquals(1, findByFolder.getFullLength());

        findByFolder = uut.findByFolder("INBOX", 1, 1);
        assertEquals(0, findByFolder.getFullLength());
    }

    @Test
    public void testDelete() throws Exception {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.setStarred(false).build();

        mockServer.prepareMailBox("INBOX", m);
        MailMessageList findByFolder = uut.findByFolder("INBOX", 1, 1);
        assertEquals(1, findByFolder.getFullLength());

        uut.delete(findByFolder.getMailList().get(0));
        findByFolder = uut.findByFolder("INBOX", 1, 1);
        assertEquals(0, findByFolder.getFullLength());
    }

    @Test
    public void testSaveInFolder() {
        MailMessageList findByFolder = uut.findByFolder("INBOX", 1, 1);
        assertEquals(0, findByFolder.getFullLength());

        MailMessage mm = new MailMessage();
        MailMessage saved = uut.saveInFolder(mm, "INBOX");

        findByFolder = uut.findByFolder("INBOX", 1, 1);
        assertEquals(1, findByFolder.getFullLength());
        assertTrue(saved.getId().startsWith("INBOX|"));
    }

    @Test
    public void testCopyMessage() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", m);
        mockServer.createAndSubscribeMailBox("INBOX.copy");

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        uut.copyMessage(id, "INBOX.copy");

        mockServer.verifyMessageCount("INBOX", 1);
        mockServer.verifyMessageCount("INBOX.copy", 1);
    }

}
