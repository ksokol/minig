package org.minig.server.service.submission;

import config.ServiceTestConfig;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.security.MailAuthentication;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.TestConstants;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.minig.server.TestConstants.MOCK_USER;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class SubmissionServiceTest {

    @Autowired
    private SubmissionService uut;

    @Autowired
    private MailAuthentication mailAuthentication;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

    @Test
    public void testSendMessage() throws MessagingException {
        String expectedBody = "html with umlaut ä";
        new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Drafts").subscribed().exists().build();

        Mailbox inbox = new MailboxBuilder("test@example.com").mailbox("INBOX").subscribed().exists().build();
        Mailbox sentBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Sent").subscribed().exists().build();

        MailMessage mm = new MailMessage();
        mm.setSender(new MailMessageAddress(mailAuthentication.getEmailAddress()));
        mm.setTo(Arrays.asList(new MailMessageAddress("test@example.com")));
        mm.setSubject("test subject");
        mm.setHtml(expectedBody);
        mm.setPlain(expectedBody);

        uut.sendMessage(mm);

        assertThat(sentBox, hasSize(1));
        assertThat(inbox, hasSize(1));

        Mime4jMessage mime4jMessage = new Mime4jMessage(inbox.get(0));

        assertEquals("testuser@localhost", mime4jMessage.getSender());
        assertEquals("test subject", mime4jMessage.getSubject());
        assertThat(mime4jMessage.getHtml(), is(expectedBody));
        assertThat(mime4jMessage.getPlain(), is(expectedBody));
    }

    @Test
    public void testForwardMessage() throws MessagingException {
        String expectedBody = "html with umlaut ä";
        new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX").subscribed().exists().build();
        new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Drafts").subscribed().exists().build();

        Mailbox inbox = new MailboxBuilder("test@example.com").mailbox("INBOX").subscribed().exists().build();
        Mailbox sentBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Sent").subscribed().exists().build();
        Mailbox testBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.test").subscribed().exists().build();

        MimeMessage toBeForwarded = new MimeMessageBuilder().setFolder("INBOX.test").build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        testBox.add(toBeForwarded);

        MailMessage mm = new MailMessage();
        mm.setSender(new MailMessageAddress(mailAuthentication.getEmailAddress()));
        mm.setTo(Arrays.asList(new MailMessageAddress("test@example.com")));
        mm.setSubject("msg with forward");
        mm.setHtml(expectedBody);
        mm.setPlain(expectedBody);
        mm.setForwardedMessageId(toBeForwarded.getMessageID());

        uut.sendMessage(mm);

        assertThat(sentBox, hasSize(1));
        assertThat(inbox, hasSize(1));

        Mime4jMessage mime4jMessage = new Mime4jMessage(inbox.get(0));

        assertEquals("testuser@localhost", mime4jMessage.getSender());
        assertEquals("msg with forward", mime4jMessage.getSubject());

        Message actualMessage =
                mailboxRule.getFirstInFolder("INBOX.test").orElseThrow(() -> new AssertionError("message in folder INBOX.text expected"));

        assertThat(actualMessage.getSubject(), is("Pingdom Monthly Report 2013-04-01 to 2013-04-30"));
        assertThat(actualMessage.getFlags().getUserFlags(), arrayContaining("$Forwarded"));
    }

    @Test
    public void testInvalidForwardMessage() throws MessagingException {
        new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX").subscribed().exists().build();
        new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Drafts").subscribed().exists().build();

        Mailbox inbox = new MailboxBuilder("test@example.com").mailbox("INBOX").subscribed().exists().build();
        Mailbox sentBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Sent").subscribed().exists().build();

        MailMessage mm = new MailMessage();
        mm.setTo(Arrays.asList(new MailMessageAddress("test@example.com")));
        mm.setForwardedMessageId("42");

        uut.sendMessage(mm);

        assertThat(sentBox, hasSize(1));
        assertThat(inbox, hasSize(1));
    }

    @Test
    public void testSendDraftMessage() throws MessagingException, IOException {
        MimeMessage toBeSend = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        Mime4jMessage mime4jMessageToBeSend = new Mime4jMessage(toBeSend);

        assertThat(mime4jMessageToBeSend.getAttachments(), hasSize(2));

        Mailbox inbox = new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        Mailbox sentBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Sent").subscribed().exists().build();
        Mailbox draftsBox = new MailboxBuilder(mailAuthentication.getEmailAddress()).mailbox("INBOX.Drafts").subscribed().exists().build();

        draftsBox.add(toBeSend);

        MailMessage mm = new MailMessage();
        mm.setSender(new MailMessageAddress(mailAuthentication.getEmailAddress()));
        mm.setTo(Arrays.asList(new MailMessageAddress("testuser@localhost")));
        mm.setSubject("msg with attachment");
        mm.setFolder("INBOX.Drafts");
        mm.setMessageId(toBeSend.getMessageID());

        assertThat(inbox, Matchers.hasSize(0));
        assertThat(sentBox, Matchers.hasSize(0));
        assertThat(draftsBox, Matchers.hasSize(1));

        uut.sendMessage(mm);

        assertThat(inbox, Matchers.hasSize(1));
        assertThat(sentBox, Matchers.hasSize(1));
        assertThat(draftsBox, Matchers.hasSize(0));

        Mime4jMessage mime4jMessage = new Mime4jMessage(inbox.get(0));

        assertEquals("testuser@localhost", mime4jMessage.getSender());
        assertEquals("msg with attachment", mime4jMessage.getSubject());
        assertThat(mime4jMessage.getAttachments(), hasSize(2));
    }

}
