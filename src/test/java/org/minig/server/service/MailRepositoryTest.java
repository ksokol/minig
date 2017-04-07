package org.minig.server.service;

import com.sun.mail.imap.IMAPFolder.FetchProfileItem;
import config.ServiceTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.TestConstants;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.FetchProfile;
import javax.mail.FetchProfile.Item;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.minig.server.TestConstants.MOCK_USER;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class MailRepositoryTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private MailRepository uut;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

    @Test
    public void testRead() throws MessagingException {
        List<MimeMessage> mList = new MimeMessageBuilder().build(2);

        mockServer.prepareMailBox("INBOX", mList);

        CompositeId id = new CompositeId("INBOX", mList.get(0).getMessageID());
        MailMessage mm = uut.read(id);

        assertEquals(id.getId(), mm.getId());
    }

    @Test
    public void shouldReturnCorrectPageWithTwoMessages() {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        mailboxRule.append("INBOX", message, message);

        Page<MimeMessage> page = uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(0, 20));

        assertThat(page.getTotalElements(), is(2L));
        assertThat(page.getContent(), hasSize(2));
        assertThat(page.getNumberOfElements(), is(2));
        assertThat(page.getTotalPages(), is(1));
        assertThat(page.getNumber(), is(0));
        assertThat(page.getSize(), is(20));
    }

    @Test
    public void shouldReturnCorrectPaginationAndEmptyContentWhenRequestedSizeIsOutOfBound() {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        mailboxRule.append("INBOX", message, message);

        Page<MimeMessage> page = uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(1, 20));

        assertThat(page.getTotalElements(), is(2L));
        assertThat(page.getContent(), hasSize(0));
        assertThat(page.getNumberOfElements(), is(0));
        assertThat(page.getTotalPages(), is(1));
        assertThat(page.getNumber(), is(1));
        assertThat(page.getSize(), is(20));
    }

    @Test
    public void shouldCalculateValidPagination() {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        IntStream.range(0, 25).forEach(i -> mailboxRule.append("INBOX", message));

        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(1, 20)).getNumberOfElements(), is(5));
        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(0, 25)).getNumberOfElements(), is(25));
        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(0, 30)).getNumberOfElements(), is(25));
        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(1, 30)).getNumberOfElements(), is(0));
        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(0, 13)).getNumberOfElements(), is(13));
        assertThat(uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(2, 13)).getNumberOfElements(), is(0));
    }

    @Test
    public void shouldFetchMessagesWithFetchProfile() throws Exception {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        mailboxRule.append("INBOX", message);

        uut.findByFolderOrderByDateDesc("INBOX", new PageRequest(0, 25));
        List<FetchProfile> fetchProfiles = mailboxRule.getMailbox("INBOX").getFetchProfiles();

        assertThat(fetchProfiles, hasSize(1));
        assertThat(fetchProfiles.get(0).getItems(), arrayContaining(Item.ENVELOPE, Item.FLAGS, Item.CONTENT_INFO));
        assertThat(fetchProfiles.get(0).getHeaderNames(), arrayContaining("$Forwarded", "$MDNSent", "Message-ID"));
    }

    @Test
    public void shouldFindMimeMessageByCompositeId() throws Exception {
        MimeMessage message1 = new MimeMessageBuilder().build(TestConstants.PLAIN);
        MimeMessage message2 = new MimeMessageBuilder().build(TestConstants.HTML);
        String messageId = message1.getMessageID();
        mailboxRule.append("INBOX", message1, message2);

        MimeMessage inbox = uut.findByCompositeId(new CompositeId("INBOX", messageId))
                .orElseThrow(() -> new AssertionError("mime message with message id " + messageId + " expected"));

        assertThat(inbox, is(message1));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCompositeIdIsUnknown() throws Exception {
        assertThat(uut.findByCompositeId(new CompositeId("INBOX", "unknown")).isPresent(), is(false));
    }

    @Test
    public void shouldFetchCompleteMessage() throws Exception {
        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        String messageId = message.getMessageID();
        mailboxRule.append("INBOX", message);

        uut.findByCompositeId(new CompositeId("INBOX", messageId))
                .orElseThrow(() -> new AssertionError("mime message with message id " + messageId + " expected"));

        List<FetchProfile> fetchProfiles = mailboxRule.getMailbox("INBOX").getFetchProfiles();

        assertThat(fetchProfiles, hasSize(1));
        assertThat(fetchProfiles.get(0).getItems(), arrayContaining(Item.ENVELOPE, Item.FLAGS, Item.CONTENT_INFO, FetchProfileItem.MESSAGE));
        assertThat(fetchProfiles.get(0).getHeaderNames(),
                arrayContaining("$Forwarded",
                                "$MDNSent",
                                "Message-ID",
                                "X-Mozilla-Draft-Info",
                                "X-PRIORITY",
                                "Disposition-Notification-To",
                                "In-Reply-To",
                                "X-Forwarded-Message-Id",
                                "User-Agent"
                    )
                );
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
        MimeMessage message = new MimeMessageBuilder().build();

        mailboxRule.append("INBOX", message);

        CompositeId id = new CompositeId("INBOX", message.getMessageID());
        MailMessage mail = uut.read(id);

        mailboxRule.createFolder(MOCK_USER, "INBOX.testUpdateFolder");
        uut.moveMessage(mail, "INBOX.testUpdateFolder");

        assertThat(mailboxRule.getFirstInFolder("INBOX").isPresent(), is(false));
        assertThat(mailboxRule.getFirstInFolder("INBOX.testUpdateFolder").isPresent(), is(true));
    }

    @Test
    public void testDelete() throws Exception {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage message = builder.setMessageId("id").build();

        mailboxRule.append("INBOX", message);

        uut.delete(new CompositeId("INBOX", message.getMessageID()));

        assertThat(mailboxRule.getFirstInFolder("INBOX").isPresent(), is(false));
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

    @Test
    public void testFindByMessageId() throws Exception {
        new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.child1a").subscribed().exists().build();
        Mailbox child1b = new MailboxBuilder("testuser@localhost").mailbox("INBOX.child2b").subscribed().exists().build();
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.child2b.child1a").subscribed().exists().build();
        Mailbox child2bChild1b = new MailboxBuilder("testuser@localhost").mailbox("INBOX.child2b.child1b").subscribed().exists().build();

        MimeMessage message = new MimeMessageBuilder().build(TestConstants.PLAIN);
        String messageID = message.getMessageID();

        child1b.add(message);
        child2bChild1b.add(message);

        CompositeId byMessageId = uut.findByMessageId(messageID);

        assertThat(byMessageId.getId(), is(new CompositeId("INBOX.child2b.child1b", message.getMessageID()).getId()));
    }

}
