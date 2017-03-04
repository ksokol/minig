package org.minig.server.service.submission;

import config.ServiceTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class DispositionServiceImplTest {

    @Autowired
    private DispositionServiceImpl uut;

    @Before
    public void setUp() throws Exception {
        MailboxHolder.reset();
    }

    @Test
    public void testSendDisposition() throws MessagingException {
        Mailbox testuserInbox = new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        Mailbox recipientInbox = new MailboxBuilder("recipient@localhost").mailbox("INBOX").subscribed().exists().build();

        MimeMessage m = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.DISPOSITION_NOTIFICATION);
        testuserInbox.add(m);

        CompositeId inbox = new CompositeId("INBOX", m.getMessageID());

        uut.sendDisposition(inbox);

        assertThat(testuserInbox.getNewMessageCount(), is(0));
        assertThat(recipientInbox.getNewMessageCount(), is(1));

        Message message = recipientInbox.get(0);
        Mime4jMessage mime4jMessage = new Mime4jMessage(message);

        assertThat(mime4jMessage.getSubject(), is("Return Receipt (displayed) - Disposition Notification Test"));
        assertThat(mime4jMessage.getPlain(), is("This is a Return Receipt for the mail that you sent to testuser@localhost. \r\n\r\n" +
                "Note: This Return Receipt only acknowledges that the message was displayed on the recipients computer. " +
                "There is no guarantee that the recipient has read or understood the message contents."));

        assertThat(mime4jMessage.getAttachments(), hasSize(1));
        assertThat(mime4jMessage.getAttachment("Disposition Notification Test.eml"), notNullValue());
    }
}
