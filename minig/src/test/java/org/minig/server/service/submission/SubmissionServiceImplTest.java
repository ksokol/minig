package org.minig.server.service.submission;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
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
    private SmtpAndImapMockServer mockServer;

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
    }

    @Test
    public void testSendMessage() throws MessagingException {
        mockServer.createAndSubscribeMailBox("INBOX.Sent", "INBOX.Drafts");

        MailMessage mm = new MailMessage();
        mm.setSender(new MailMessageAddress(mockServer.getMockUserEmail()));
        mm.setTo(Arrays.asList(new MailMessageAddress("test@example.com")));
        mm.setSubject("test subject");

        uut.sendMessage(mm);

        mockServer.verifyMessageCount("INBOX.Sent", 1);

        assertEquals("testuser@localhost", mockServer.getReceivedMessages()[0].getFrom()[0].toString());
        assertEquals("test subject", mockServer.getReceivedMessages()[0].getSubject());
    }

    @Test
    public void testForwardMessage() throws MessagingException {
        mockServer.createAndSubscribeMailBox("INBOX.Sent", "INBOX.Drafts", "INBOX.test");

        MimeMessage toBeForwarded = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        mockServer.prepareMailBox("INBOX.test", toBeForwarded);

        MailMessage mm = new MailMessage();
        mm.setSender(new MailMessageAddress(mockServer.getMockUserEmail()));
        mm.setTo(Arrays.asList(new MailMessageAddress("test@example.com")));
        mm.setSubject("msg with forward");

        CompositeId compositeId = new CompositeId("INBOX.test", toBeForwarded.getMessageID());

        uut.forwardMessage(mm, compositeId);

        mockServer.verifyMessageCount("INBOX.Sent", 1);

        assertEquals("testuser@localhost", mockServer.getReceivedMessages()[0].getFrom()[0].toString());
        assertEquals("msg with forward", mockServer.getReceivedMessages()[0].getSubject());

        MailMessageList findByFolder = mailRepository.findByFolder("INBOX.test", 1, 10);

        assertEquals("Pingdom Monthly Report 2013-04-01 to 2013-04-30", findByFolder.getMailList().get(0).getSubject());
        assertTrue(findByFolder.getMailList().get(0).getForwarded());
    }
}
