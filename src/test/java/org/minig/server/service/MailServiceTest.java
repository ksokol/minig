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

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.minig.server.TestConstants.MOCK_USER;

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
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

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
        MimeMessage message = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", message);
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        CompositeId id = new CompositeId("INBOX", message.getMessageID());
        uut.deleteMessage(id);

        assertThat(mailboxRule.getAllInFolder("INBOX"), empty());
        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), hasSize(1));
    }

    @Test
    public void testDeleteMessage() throws MessagingException {
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        MimeMessage m = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX.Trash", m);

        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), hasSize(1));

        CompositeId id = new CompositeId("INBOX.Trash", m.getMessageID());
        uut.deleteMessage(id);

        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), empty());
    }

    @Test
    public void testUpdateMessageFlags_valid() throws MessagingException {
        MimeMessage message = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX", message);

        MailMessage mailMessage = new MailMessage();
        mailMessage.setCompositeId(new CompositeId("INBOX", message.getMessageID()));
        mailMessage.setAnswered(true);
        mailMessage.setRead(true);
        mailMessage.setStarred(true);

        uut.updateMessageFlags(mailMessage);

        CompositeId id = new CompositeId("INBOX", message.getMessageID());

        MailMessage after = uut.findMessage(id);

        assertTrue(after.getRead());
        assertTrue(after.getStarred());
        assertTrue(after.getAnswered());
        assertFalse(after.getHighPriority());

        mailMessage.setRead(null);
        mailMessage.setStarred(null);
        mailMessage.setAnswered(null);
        mailMessage.setHighPriority(null);

        uut.updateMessageFlags(mailMessage);

        id = new CompositeId("INBOX", message.getMessageID());
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
    public void testUpdateMessagesFlags_valid() throws MessagingException {
        List<MimeMessage> mimeMessages = new MimeMessageBuilder().build(2);
        mockServer.prepareMailBox("INBOX", mimeMessages);

        MailMessage mailMessage1 = new MailMessage();
        mailMessage1.setCompositeId(new CompositeId("INBOX", mimeMessages.get(0).getMessageID()));
        mailMessage1.setStarred(true);

        MailMessage mailMessage2 = new MailMessage();
        mailMessage2.setCompositeId(new CompositeId("INBOX", mimeMessages.get(1).getMessageID()));
        mailMessage2.setStarred(true);

        MailMessageList mailMessageList = new MailMessageList();
        mailMessageList.setMailList(Arrays.asList(mailMessage1, mailMessage2));

        uut.updateMessagesFlags(mailMessageList);

        List<Message> inbox = mailboxRule.getAllInFolder("INBOX");

        for (Message message : inbox) {
            assertThat(message.getFlags().getSystemFlags(), arrayContaining(Flags.Flag.FLAGGED));
        }
    }

    @Test
    public void testMoveMessageToFolder() throws MessagingException {
        MimeMessage message = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", message);
        mockServer.createAndSubscribeMailBox("INBOX.test");

        CompositeId compositeId = new CompositeId("INBOX", message.getMessageID());
        uut.moveMessageToFolder(compositeId, "INBOX.test");

        assertThat(mailboxRule.getAllInFolder("INBOX"), empty());
        assertThat(mailboxRule.getAllInFolder("INBOX.test"), hasSize(1));
    }

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

        assertThat(mailboxRule.getAllInFolder("INBOX"), hasSize(3));
        assertThat(mailboxRule.getAllInFolder("INBOX.copy"), hasSize(3));

        idList.add(null);

        mockServer.createAndSubscribeMailBox("INBOX.copy2");

        uut.copyMessagesToFolder(idList, "INBOX.copy2");

        assertThat(mailboxRule.getAllInFolder("INBOX.copy2"), hasSize(3));
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

        assertThat(mailboxRule.getAllInFolder("INBOX"), empty());
        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), hasSize(3));

        idList.clear();

        for (MimeMessage m : mList) {
            idList.add(new CompositeId("INBOX.Trash", m.getMessageID()));
        }

        uut.deleteMessages(idList);

        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), empty());
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

    @Test
    public void shouldReturnHtmlBodyWithAbsoluteUrlsToInlineAttachments() throws Exception {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mailboxRule.append("INBOX/test", message);

        String actualHtmlBody = uut.findHtmlBodyByCompositeId(new CompositeId("INBOX/test", message.getMessageID()));

        assertThat(actualHtmlBody, containsString("background-image:url(http://localhost/1/attachment/folder|<1367760625.51865ef16e3f6@swift.generated>|1367760625.51865ef16e3f6@swift.generated);"));
        assertThat(actualHtmlBody, containsString("<img src=\"http://localhost/1/attachment/folder|<1367760625.51865ef16e3f6@swift.generated>|1367760625.51865ef16cc8c@swift.generated\" alt=\"Pingdom\" /><"));
        assertThat(actualHtmlBody, containsString("background-image:url(http://localhost/1/attachment/folder|<1367760625.51865ef16e3f6@swift.generated>|1367760625.51865ef16f798@swift.generated);"));

    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUnknownCompositeIdGiven() throws Exception {
        uut.findHtmlBodyByCompositeId(new CompositeId("INBOX", "unknown"));
    }
}
