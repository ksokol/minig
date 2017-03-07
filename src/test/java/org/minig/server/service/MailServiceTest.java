package org.minig.server.service;

import config.ServiceTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class MailServiceTest {

    @Autowired
    private MailService uut;

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule();

    @Test
    public void testFolderCountFindMessagesByFolder() {
        String folder = "INBOX.test";
        mailboxRule.append(folder, new MimeMessageBuilder().build());
        MailMessageList findMessagesByFolder = uut.firstPageMessagesByFolder(folder);
        assertThat(findMessagesByFolder.getFullLength(), is(1));
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

        assertThat(count, is(3));
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

        assertThat(count, is(3));
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

        assertThat(result.getSubject(), is("test subject"));
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
            MailMessageList mailMessageList = new MailMessageList();
            mailMessageList.setMailList(null);
            uut.updateMessagesFlags(mailMessageList);
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
        assertEquals(1, uut.firstPageMessagesByFolder("INBOX.test").getFullLength());
    }

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

        m.setPlain("plain body");

        MailMessageAddress recipient = new MailMessageAddress();
        recipient.setDisplayName("sender@localhost");
        recipient.setEmail("sender@localhost");
        m.setTo(Arrays.asList(recipient));

        MailMessage draftMessage = uut.createDraftMessage(m);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);
        assertThat(draftMessage.getSubject(), is("draft message"));
        assertThat(draftMessage.getTo(), hasSize(1));
        assertThat(draftMessage.getTo().get(0).getEmail(), is("sender@localhost"));
        assertThat(draftMessage.getTo().get(0).getDisplayName(), is("sender@localhost"));
        assertThat(draftMessage.getPlain(), is("plain body"));
    }

    @Test
    public void testAttachmentsOnUpdateDraftMessage() throws MessagingException {
        String replacedBody = "replaced plain " + new Date().toString();

        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeId id = new CompositeId("INBOX.Drafts", m.getMessageID());

        MailMessage mm = new MailMessage();
        mm.setCompositeId(id);
        mm.setSubject("save draft");
        mm.setPlain(replacedBody);
        mm.setHtml(replacedBody);

        MailMessage updateDraftMessage = uut.updateDraftMessage(mm);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);

        assertFalse(id.getId().equals(updateDraftMessage.getId()));
        assertEquals(2, updateDraftMessage.getAttachments().size());
        assertEquals(replacedBody, updateDraftMessage.getPlain());
        assertEquals(replacedBody, updateDraftMessage.getHtml());
        assertEquals("save draft", updateDraftMessage.getSubject());
        assertEquals("1.png", updateDraftMessage.getAttachments().get(0).getFileName());
    }

    @Test
    public void testUpdateDraftMessage() throws MessagingException {
        String recipient = "sender@localhost";
        MimeMessage m = new MimeMessageBuilder().setFolder("INBOX.Drafts").setRecipientTo((List) null).build();
        mockServer.prepareMailBox("INBOX.Drafts", m);

        CompositeId id = new CompositeId("INBOX.Drafts", m.getMessageID());

        MailMessage mm = new MailMessage();
        mm.setCompositeId(id);
        mm.setAskForDispositionNotification(true);
        mm.setHighPriority(true);
        mm.setReceipt(true);
        mm.setDate(new Date());

        MailMessageAddress recipientAddress = new MailMessageAddress();
        recipientAddress.setDisplayName(recipient);
        recipientAddress.setEmail(recipient);
        List<MailMessageAddress> addresses = Arrays.asList(recipientAddress);

        mm.setTo(addresses);
        mm.setCc(addresses);
        mm.setBcc(addresses);

        MailMessage updateDraftMessage = uut.updateDraftMessage(mm);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);

        assertThat(updateDraftMessage.getTo(), hasSize(1));
        assertThat(updateDraftMessage.getTo().get(0).getEmail(), is(recipient));
        assertThat(updateDraftMessage.getTo().get(0).getDisplayName(), is(recipient));

        assertThat(updateDraftMessage.getCc(), hasSize(1));
        assertThat(updateDraftMessage.getCc().get(0).getEmail(), is(recipient));
        assertThat(updateDraftMessage.getCc().get(0).getDisplayName(), is(recipient));

        assertThat(updateDraftMessage.getBcc(), hasSize(1));
        assertThat(updateDraftMessage.getBcc().get(0).getEmail(), is(recipient));
        assertThat(updateDraftMessage.getBcc().get(0).getDisplayName(), is(recipient));

        assertThat(updateDraftMessage.getAskForDispositionNotification(), is(true));
        assertThat(updateDraftMessage.getHighPriority(), is(true));
        assertThat(updateDraftMessage.getReceipt(), is(true));
        assertThat(updateDraftMessage.getDate(), notNullValue());
    }

    @Test
    public void testUpdateDraftMessage2() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        mockServer.prepareMailBox("INBOX.Drafts", m);

        Mailbox messages = MailboxHolder.get(TestConstants.MOCK_USER, "INBOX.Drafts");

        Mime4jMessage mime4jMessage = new Mime4jMessage(messages.getUnread().get(0));

        assertThat(mime4jMessage.isDSN(), is(false));
        assertThat(mime4jMessage.isReturnReceipt(), is(false));

        MailMessage message = uut.findMessage(mime4jMessage.getId());

        MailMessage mailMessage = uut.updateDraftMessage(message);

        assertThat(mailMessage.getAskForDispositionNotification(), is(false));
        assertThat(mailMessage.getReceipt(), is(false));
    }

    @Test
    public void testCreateDraftWithForwardAndAttachments() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);

        new MailboxBuilder("testuser@localhost").mailbox("INBOX").exists(true).subscribed().build().add(m);
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").exists(true).subscribed().build();

        MailMessage mm = new MailMessage();
        mm.setForwardedMessageId(m.getMessageID());

        MailMessage updateDraftMessage = uut.createDraftMessage(mm);

        assertThat(updateDraftMessage.getAttachments(), hasSize(2));
    }

    @Test
    public void testCreateDraftWithInvalidForwardAndAttachments() throws MessagingException {
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").exists(true).subscribed().build();

        MailMessage mm = new MailMessage();
        mm.setForwardedMessageId("42");

        MailMessage updateDraftMessage = uut.createDraftMessage(mm);

        assertThat(updateDraftMessage.getAttachments(), hasSize(0));
    }
}
