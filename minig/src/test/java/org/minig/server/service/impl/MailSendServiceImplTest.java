package org.minig.server.service.impl;

import java.util.Arrays;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.minig.server.service.submission.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class MailSendServiceImplTest {

    @Autowired
    private SubmissionService uut;

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

        mockServer.prepareMailBox("INBOX.Sent");

        uut.sendMessage(mm);

        mockServer.verifyMessageCount("INBOX.Sent", 1);

        assertEquals("testuser@localhost", mockServer.getReceivedMessages()[0].getFrom()[0].toString());
        assertEquals("test subject", mockServer.getReceivedMessages()[0].getSubject());
    }

}
