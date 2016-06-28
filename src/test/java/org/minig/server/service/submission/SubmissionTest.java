package org.minig.server.service.submission;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.TestConstants;
import config.ServiceTestConfig;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.server.service.impl.helper.mime.Mime4jTestHelper;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Message;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class SubmissionTest {

    @Autowired
    private Submission uut;

    @Autowired
    private TestJavaMailSenderFactory javaMailSenderFactory;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule();

    @Test
    public void testReceiptOffDSNOff() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);
        uut.submit(mime4jMessage);
        Message firstInInbox = mailboxRule.getFirstInInbox("recipient@localhost");

        assertThat(firstInInbox.getHeader("Disposition-Notification-To"), nullValue());
        assertThat(firstInInbox.getHeader("X-Mozilla-Draft-Info"), nullValue());

        assertThat(javaMailSenderFactory.getProperties(), not(hasKey("mail.smtp.dsn.notify")));
        assertThat(javaMailSenderFactory.getProperties(), not(hasKey("mail.smtp.dsn.ret")));
    }

    @Test
    public void testReceiptOffDSNOn() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_1);
        uut.submit(mime4jMessage);
        Message firstInInbox = mailboxRule.getFirstInInbox("recipient@localhost");

        assertThat(firstInInbox.getHeader("Disposition-Notification-To"), nullValue());
        assertThat(firstInInbox.getHeader("X-Mozilla-Draft-Info"), nullValue());

        assertThat(javaMailSenderFactory.getProperties(), hasEntry("mail.smtp.dsn.notify", "SUCCESS,FAILURE,DELAY ORCPT=rfc822;testuser@localhost"));
        assertThat(javaMailSenderFactory.getProperties(), hasEntry("mail.smtp.dsn.ret", "FULL"));
    }

    @Test
    public void testReceiptOnDSNOff() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_2);
        uut.submit(mime4jMessage);
        Message firstInInbox = mailboxRule.getFirstInInbox("recipient@localhost");

        assertThat(firstInInbox.getHeader("Disposition-Notification-To"), hasItemInArray(TestConstants.MOCK_USER));
        assertThat(firstInInbox.getHeader("X-Mozilla-Draft-Info"), nullValue());
    }

    @Test
    public void testReceiptOnDSNOn() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_3);
        uut.submit(mime4jMessage);
        Message firstInInbox = mailboxRule.getFirstInInbox("recipient@localhost");

        assertThat(firstInInbox.getHeader("Disposition-Notification-To"), hasItemInArray(TestConstants.MOCK_USER));
        assertThat(firstInInbox.getHeader("X-Mozilla-Draft-Info"), nullValue());

        assertThat(javaMailSenderFactory.getProperties(), hasEntry("mail.smtp.dsn.notify", "SUCCESS,FAILURE,DELAY ORCPT=rfc822;testuser@localhost"));
        assertThat(javaMailSenderFactory.getProperties(), hasEntry("mail.smtp.dsn.ret", "FULL"));
    }

}
