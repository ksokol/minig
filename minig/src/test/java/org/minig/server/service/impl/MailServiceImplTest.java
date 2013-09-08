package org.minig.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailService;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.NotFoundException;
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
public class MailServiceImplTest {

    @Autowired
    private MailService uut;

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
        assertEquals(0, uut.firstPageMessagesByFolder("INBOX").getFullLength());
    }

    @Test
    public void testFolderCountFindMessagesByFolder() throws MessagingException, InterruptedException {
        mockServer.prepareMailBox("INBOX.test", new MimeMessageBuilder().build());

        MailMessageList findMessagesByFolder = uut.firstPageMessagesByFolder("INBOX.test");

        assertEquals(1, findMessagesByFolder.getFullLength());
    }

    @Test
    public void testFindMessagesByFolderInvalidPageArgument() {
        int count = 0;

        try {
            uut.findMessagesByFolder("INBOX", 0, 1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.findMessagesByFolder("INBOX", -1, 1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.findMessagesByFolder("INBOX", -1000, 1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testFindMessagesByFolderInvalidPageLengthArgument() {
        int count = 0;

        try {
            uut.findMessagesByFolder("INBOX", 1, 0);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.findMessagesByFolder("INBOX", 1, -1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.findMessagesByFolder("INBOX", 1, -100);
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test(expected = NotFoundException.class)
    public void testFindMessageNotExistentMessage() {
        uut.findMessage(new CompositeId("non", "existent"));
    }

    @Test
    public void testFindMessage() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().setSubject("test subject").build();
        mockServer.prepareMailBox("INBOX", m);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        MailMessage result = uut.findMessage(id);

        assertEquals("test subject", result.getSubject());
    }

    @Test
    public void testTrashMessage() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", m);
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        assertEquals(1, uut.firstPageMessagesByFolder("INBOX").getFullLength());
        assertEquals(0, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());

        CompositeId id = new CompositeId("INBOX", m.getMessageID());
        uut.deleteMessage(id);

        assertEquals(0, uut.firstPageMessagesByFolder("INBOX").getFullLength());
        assertEquals(1, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());
    }

    @Test
    public void testDeleteMessage() throws MessagingException {
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        MimeMessage m = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX.Trash", m);

        assertEquals(1, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());

        CompositeId id = new CompositeId("INBOX.Trash", m.getMessageID());
        uut.deleteMessage(id);

        assertEquals(0, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());
    }

    @Test
    public void testUpdateMessageFlags_valid() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX", m);

        MailMessageList findMessagesByFolder = uut.firstPageMessagesByFolder("INBOX");
        assertEquals(1, findMessagesByFolder.getFullLength());

        MailMessage mm = findMessagesByFolder.getMailList().get(0);
        mm.setAnswered(true);
        mm.setRead(true);
        mm.setStarred(true);

        uut.updateMessageFlags(mm);

        CompositeId id = new CompositeId("INBOX", m.getMessageID());

        MailMessage after = uut.findMessage(id);

        assertTrue(after.getRead());
        assertTrue(after.getStarred());
        assertTrue(after.getAnswered());
        assertFalse(after.getHighPriority());

        mm.setRead(null);
        mm.setStarred(null);
        mm.setAnswered(null);
        mm.setHighPriority(null);

        uut.updateMessageFlags(mm);

        id = new CompositeId("INBOX", m.getMessageID());
        after = uut.findMessage(id);

        assertTrue(after.getStarred());
        assertTrue(after.getAnswered());
        assertFalse(after.getHighPriority());
        assertTrue(after.getRead());
    }

    @Test
    public void testUpdateMessages_invalidArguments() {
        int count = 0;

        try {
            uut.updateMessagesFlags(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.updateMessagesFlags(new MailMessageList());
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    public void testUpdateMessagesFlags_valid() {
        List<MimeMessage> mList = new MimeMessageBuilder().build(2);

        mockServer.prepareMailBox("INBOX", mList);

        MailMessageList firstPageMessagesByFolder = uut.firstPageMessagesByFolder("INBOX");

        for (MailMessage mm : firstPageMessagesByFolder.getMailList()) {
            assertFalse(mm.getStarred());
            mm.setStarred(true);
        }

        uut.updateMessagesFlags(firstPageMessagesByFolder);

        firstPageMessagesByFolder = uut.firstPageMessagesByFolder("INBOX");

        for (MailMessage mm : firstPageMessagesByFolder.getMailList()) {
            assertTrue(mm.getStarred());
        }
    }

    @Test
    public void testMoveMessageToFolder() {
        mockServer.prepareMailBox("INBOX", new MimeMessageBuilder().build());
        mockServer.createAndSubscribeMailBox("INBOX.test");

        MailMessageList findMessagesByFolder = uut.firstPageMessagesByFolder("INBOX");
        assertEquals(1, findMessagesByFolder.getFullLength());

        MailMessage mm = findMessagesByFolder.getMailList().get(0);

        uut.moveMessageToFolder(mm, "INBOX.test");

        assertEquals(0, uut.firstPageMessagesByFolder("INBOX").getFullLength());
        assertEquals(1, uut.firstPageMessagesByFolder("INBOX.Test").getFullLength());
    }

    // @Test(expected = IllegalArgumentException.class)
    // public void testCreateMessageWithInvalidArguments() {
    // uut.createMessage(null);
    // }

    // @Test
    // public void testCreateMessage() {
    // MailMessage mm = new MailMessage();
    // mm.setSubject("testCreateMessage");
    // mm.setId("messageId");
    // mm.setFolder("INBOX");
    //
    // uut.createMessage(mm);
    //
    // MailMessageList firstPageMessagesByFolder =
    // uut.firstPageMessagesByFolder("INBOX");
    //
    // assertEquals(1, firstPageMessagesByFolder.getFullLength());
    // assertEquals("testCreateMessage",
    // firstPageMessagesByFolder.getMailList().get(0).getSubject());
    // }

    @Test
    public void testCopyMessagesToFolderWithInvalidArguments() {
        int count = 0;

        try {
            uut.copyMessagesToFolder(null, "INBOX");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.copyMessagesToFolder(Collections.EMPTY_LIST, null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.copyMessagesToFolder(Collections.EMPTY_LIST, "   ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testCopyMessagesToFolder() throws MessagingException {
        List<MimeMessage> mList = new MimeMessageBuilder().build(3);
        List<CompositeId> idList = new ArrayList<CompositeId>();

        mockServer.prepareMailBox("INBOX", mList);

        for (MimeMessage m : mList) {
            idList.add(new CompositeId("INBOX", m.getMessageID()));
        }

        mockServer.createAndSubscribeMailBox("INBOX.copy");

        uut.copyMessagesToFolder(idList, "INBOX.copy");

        assertEquals(3, uut.firstPageMessagesByFolder("INBOX").getFullLength());
        assertEquals(3, uut.firstPageMessagesByFolder("INBOX.copy").getFullLength());

        idList.add(null);

        mockServer.createAndSubscribeMailBox("INBOX.copy2");

        uut.copyMessagesToFolder(idList, "INBOX.copy2");

        assertEquals(3, uut.firstPageMessagesByFolder("INBOX.copy2").getFullLength());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteMessages_InvalidArguments() {
        uut.deleteMessages(null);
    }

    @Test
    public void testDeleteMessages() throws MessagingException {
        List<MimeMessage> mList = new MimeMessageBuilder().build(3);
        List<CompositeId> idList = new ArrayList<CompositeId>();

        mockServer.prepareMailBox("INBOX", mList);

        for (MimeMessage m : mList) {
            idList.add(new CompositeId("INBOX", m.getMessageID()));
        }

        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        uut.deleteMessages(idList);

        assertEquals(0, uut.firstPageMessagesByFolder("INBOX").getFullLength());
        assertEquals(3, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());

        idList.clear();

        for (MimeMessage m : mList) {
            idList.add(new CompositeId("INBOX.Trash", m.getMessageID()));
        }

        uut.deleteMessages(idList);

        assertEquals(0, uut.firstPageMessagesByFolder("INBOX.Trash").getFullLength());
    }

    @Test
    public void testCreateDraftMessage() {
        mockServer.createAndSubscribeMailBox("INBOX.Drafts");

        MailMessage m = new MailMessage();
        m.setSubject("draft message");

        uut.createDraftMessage(m);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);
        assertEquals("draft message", uut.firstPageMessagesByFolder("INBOX.Drafts").getMailList().get(0).getSubject());
    }

    @Test
    public void testUpdateDraftMessage() throws MessagingException {
        String replacedBody = "replaced plain " + new Date().toString();

        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeId id = new CompositeId("INBOX.Drafts", m.getMessageID());

        MailMessage mm = new MailMessage();
        mm.setCompositeId(id);
        mm.setSubject("save draft");
        mm.getBody().setPlain(replacedBody);
        mm.getBody().setHtml(replacedBody);

        MailMessage updateDraftMessage = uut.updateDraftMessage(mm);
        MailMessage findMessage = uut.findMessage(updateDraftMessage);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);

        assertFalse(id.getId().equals(findMessage.getId()));
        assertEquals(2, findMessage.getAttachments().size());
        assertEquals(replacedBody, findMessage.getBody().getPlain());
        assertEquals(replacedBody, findMessage.getBody().getHtml());
        assertEquals("save draft", findMessage.getSubject());
        assertEquals("1.png", findMessage.getAttachments().get(0).getFileName());
    }
}