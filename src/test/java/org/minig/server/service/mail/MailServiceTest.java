package org.minig.server.service.mail;

import config.ServiceTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.SmtpAndImapMockServer;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.WithAuthenticatedUser;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.minig.server.TestConstants.MOCK_USER;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
@WithAuthenticatedUser
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
    public void shouldReturnMessage() throws Exception {
        var message = new MimeMessageBuilder().build();
        mailboxRule.append("INBOX", message);

        var actual = uut.findByCompositeId(new CompositeId("INBOX", message.getMessageID()));
        assertThat(actual.getId(), is("INBOX|" + message.getMessageID()));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundException() {
        uut.findByCompositeId(new CompositeId("INBOX", "unknown"));
    }

    @Test
    public void testFindMessage() throws MessagingException {
        var m = new MimeMessageBuilder().setSubject("test subject").build();
        mockServer.prepareMailBox("INBOX", m);

        var id = new CompositeId("INBOX", m.getMessageID());
        var result = uut.findMessage(id);

        assertThat(result.getSubject(), is("test subject"));
    }

    @Test
    public void testTrashMessage() throws MessagingException {
        var message = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", message);
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        var id = new CompositeId("INBOX", message.getMessageID());
        uut.deleteMessage(id);

        assertThat(mailboxRule.getAllInFolder("INBOX"), empty());
        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), hasSize(1));
    }

    @Test
    public void testDeleteMessage() throws MessagingException {
        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        var m = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX.Trash", m);

        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), hasSize(1));

        var id = new CompositeId("INBOX.Trash", m.getMessageID());
        uut.deleteMessage(id);

        assertThat(mailboxRule.getAllInFolder("INBOX.Trash"), empty());
    }

    @Test
    public void testUpdateMessageFlags_valid() throws MessagingException {
        var message = new MimeMessageBuilder().build();
        mockServer.prepareMailBox("INBOX", message);

        var mailMessage = new MailMessage();
        mailMessage.setCompositeId(new CompositeId("INBOX", message.getMessageID()));
        mailMessage.setAnswered(true);
        mailMessage.setRead(true);
        mailMessage.setStarred(true);

        uut.updateMessageFlags(mailMessage);

        var id = new CompositeId("INBOX", message.getMessageID());

        var after = uut.findMessage(id);

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
        var count = 0;

        try {
            uut.updateMessagesFlags(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            var mailMessageList = new MailMessageList();
            mailMessageList.setMailList(null);
            uut.updateMessagesFlags(mailMessageList);
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    public void testUpdateMessagesFlags_valid() throws MessagingException {
        var mimeMessages = new MimeMessageBuilder().build(2);
        mockServer.prepareMailBox("INBOX", mimeMessages);

        var mailMessage1 = new MailMessage();
        mailMessage1.setCompositeId(new CompositeId("INBOX", mimeMessages.get(0).getMessageID()));
        mailMessage1.setStarred(true);

        var mailMessage2 = new MailMessage();
        mailMessage2.setCompositeId(new CompositeId("INBOX", mimeMessages.get(1).getMessageID()));
        mailMessage2.setStarred(true);

        var mailMessageList = new MailMessageList();
        mailMessageList.setMailList(Arrays.asList(mailMessage1, mailMessage2));

        uut.updateMessagesFlags(mailMessageList);

        var inbox = mailboxRule.getAllInFolder("INBOX");

        for (var message : inbox) {
            assertThat(message.getFlags().getSystemFlags(), arrayContaining(Flags.Flag.FLAGGED));
        }
    }

    @Test
    public void testMoveMessageToFolder() throws MessagingException {
        var message = new MimeMessageBuilder().build();

        mockServer.prepareMailBox("INBOX", message);
        mockServer.createAndSubscribeMailBox("INBOX.test");

        var compositeId = new CompositeId("INBOX", message.getMessageID());
        uut.moveMessageToFolder(compositeId, "INBOX.test");

        assertThat(mailboxRule.getAllInFolder("INBOX"), empty());
        assertThat(mailboxRule.getAllInFolder("INBOX.test"), hasSize(1));
    }

    @Test
    public void testCopyMessagesToFolderWithInvalidArguments() {
        var count = 0;

        try {
            uut.copyMessagesToFolder(null, "INBOX");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.copyMessagesToFolder(Collections.emptyList(), null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.copyMessagesToFolder(Collections.emptyList(), "   ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testCopyMessagesToFolder() throws MessagingException {
        var mList = new MimeMessageBuilder().build(3);
        List<CompositeId> idList = new ArrayList<>();

        mockServer.prepareMailBox("INBOX", mList);

        for (var m : mList) {
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
        var mList = new MimeMessageBuilder().build(3);
        List<CompositeId> idList = new ArrayList<>();

        mockServer.prepareMailBox("INBOX", mList);

        for (var m : mList) {
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

        var m = new MailMessage();
        m.setSubject("draft message");

        m.setPlain("plain body");

        var recipient = new MailMessageAddress();
        recipient.setDisplayName("sender@localhost");
        recipient.setEmail("sender@localhost");
        m.setTo(Collections.singletonList(recipient));

        var draftMessage = uut.createDraftMessage(m);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);
        assertThat(draftMessage.getSubject(), is("draft message"));
        assertThat(draftMessage.getTo(), hasSize(1));
        assertThat(draftMessage.getTo().get(0).getEmail(), is("sender@localhost"));
        assertThat(draftMessage.getTo().get(0).getDisplayName(), is("sender@localhost"));
        assertThat(draftMessage.getPlain(), is("plain body"));
    }

    @Test
    public void testAttachmentsOnUpdateDraftMessage() throws MessagingException {
        var replacedBody = "replaced plain " + new Date().toString();

        var m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        mockServer.prepareMailBox("INBOX.Drafts", m);

        var id = new CompositeId("INBOX.Drafts", m.getMessageID());

        var mm = new MailMessage();
        mm.setCompositeId(id);
        mm.setSubject("save draft");
        mm.setPlain(replacedBody);
        mm.setHtml(replacedBody);

        var updateDraftMessage = uut.updateDraftMessage(mm);

        mockServer.verifyMessageCount("INBOX.Drafts", 1);

        assertNotEquals(id.getId(), updateDraftMessage.getId());
        assertEquals(2, updateDraftMessage.getAttachments().size());
        assertEquals(replacedBody, updateDraftMessage.getPlain());
        assertEquals(replacedBody, updateDraftMessage.getHtml());
        assertEquals("save draft", updateDraftMessage.getSubject());
        assertEquals("1.png", updateDraftMessage.getAttachments().get(0).getFileName());
    }

    @Test
    public void testUpdateDraftMessage() throws MessagingException {
        var recipient = "sender@localhost";
        var m = new MimeMessageBuilder().setFolder("INBOX.Drafts").setRecipientTo((List) null).build();
        mockServer.prepareMailBox("INBOX.Drafts", m);

        var id = new CompositeId("INBOX.Drafts", m.getMessageID());

        var mm = new MailMessage();
        mm.setCompositeId(id);
        mm.setAskForDispositionNotification(true);
        mm.setHighPriority(true);
        mm.setReceipt(true);
        mm.setDate(new Date());

        var recipientAddress = new MailMessageAddress(recipient);
        var addresses = Collections.singletonList(recipientAddress);

        mm.setTo(addresses);
        mm.setCc(addresses);
        mm.setBcc(addresses);

        var updateDraftMessage = uut.updateDraftMessage(mm);

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
        var m = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        mockServer.prepareMailBox("INBOX.Drafts", m);

        var messages = MailboxHolder.get(TestConstants.MOCK_USER, "INBOX.Drafts");

        var mime4jMessage = new Mime4jMessage(messages.getUnread().get(0));

        assertThat(mime4jMessage.isDSN(), is(false));
        assertThat(mime4jMessage.isReturnReceipt(), is(false));

        var message = uut.findMessage(mime4jMessage.getId());

        var mailMessage = uut.updateDraftMessage(message);

        assertThat(mailMessage.getAskForDispositionNotification(), is(false));
        assertThat(mailMessage.getReceipt(), is(false));
    }

    @Test
    public void testCreateDraftWithForwardAndAttachments() throws MessagingException {
        var m = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);

        new MailboxBuilder("testuser@localhost").mailbox("INBOX").exists(true).subscribed().build().add(m);
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").exists(true).subscribed().build();

        var mm = new MailMessage();
        mm.setForwardedMessageId(m.getMessageID());

        var updateDraftMessage = uut.createDraftMessage(mm);

        assertThat(updateDraftMessage.getAttachments(), hasSize(2));
    }

    @Test
    public void testCreateDraftWithInvalidForwardAndAttachments() {
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").exists(true).subscribed().build();

        var mm = new MailMessage();
        mm.setForwardedMessageId("42");

        var updateDraftMessage = uut.createDraftMessage(mm);

        assertThat(updateDraftMessage.getAttachments(), hasSize(0));
    }

    @Test
    public void shouldReturnHtmlBodyWithAbsoluteUrlsToInlineAttachments() throws Exception {
        var message = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mailboxRule.append("INBOX/test", message);

        var actualHtmlBody = uut.findHtmlBodyByCompositeId(new CompositeId("INBOX/test", message.getMessageID()));

        assertThat(actualHtmlBody, containsString("http://localhost/api/1/attachment/folder%257C%253C1367760625.51865ef16e3f6%2540swift.generated%253E%257C1367760625.51865ef16e3f6%2540swift.generated"));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUnknownCompositeIdGiven() {
        uut.findHtmlBodyByCompositeId(new CompositeId("INBOX", "unknown"));
    }
}
