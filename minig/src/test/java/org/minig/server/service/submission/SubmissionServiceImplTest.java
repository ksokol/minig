package org.minig.server.service.submission;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.mime4j.MimeException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.MailAuthentication;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.minig.test.mime4j.Mime4jTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class SubmissionServiceImplTest {

    @Autowired
    private SubmissionService uut;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailAuthentication mailAuthentication;

    @Before
    public void setUp() throws Exception {
        MailboxHolder.reset();
    }

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
        mm.getBody().setHtml(expectedBody);
        mm.getBody().setPlain(expectedBody);

        uut.sendMessage(mm);

        assertThat(sentBox, hasSize(1));
        assertThat(inbox, hasSize(1));

        Mime4jMessage mime4jMessage = Mime4jTestHelper.convertMimeMessage(inbox.get(0));

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
        mm.getBody().setHtml(expectedBody);
        mm.getBody().setPlain(expectedBody);

        CompositeId compositeId = new CompositeId("INBOX.test", toBeForwarded.getMessageID());

        uut.forwardMessage(mm, compositeId);

        assertThat(sentBox, hasSize(1));
        assertThat(inbox, hasSize(1));

        Mime4jMessage mime4jMessage = Mime4jTestHelper.convertMimeMessage(inbox.get(0));

        assertEquals("testuser@localhost", mime4jMessage.getSender());
        assertEquals("msg with forward", mime4jMessage.getSubject());

        MailMessageList findByFolder = mailRepository.findByFolder("INBOX.test", 1, 10);

        assertEquals("Pingdom Monthly Report 2013-04-01 to 2013-04-30", findByFolder.getMailList().get(0).getSubject());
        assertTrue(findByFolder.getMailList().get(0).getForwarded());
    }

    @Test
    public void testSendDraftMessage() throws MessagingException, IOException, MimeException {
        MimeMessage toBeSend = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);
        Mime4jMessage mime4jMessageToBeSend = Mime4jTestHelper.convertMimeMessage(toBeSend);

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

        Mime4jMessage mime4jMessage = Mime4jTestHelper.convertMimeMessage(inbox.get(0));

        assertEquals("testuser@localhost", mime4jMessage.getSender());
        assertEquals("msg with attachment", mime4jMessage.getSubject());
        assertThat(mime4jMessage.getAttachments(), hasSize(2));
    }

}
