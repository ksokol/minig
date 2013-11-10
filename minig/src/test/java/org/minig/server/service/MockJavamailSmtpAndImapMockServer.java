package org.minig.server.service;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.jvnet.mock_javamail.Mailbox;
import org.minig.MailAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("mockServer")
@Profile("test")
public class MockJavamailSmtpAndImapMockServer implements SmtpAndImapMockServer {

    @Autowired
    private MailAuthentication mailAuthentication;

    @Override
    public void createAndSubscribeMailBox(String... mailBox) {
        for (String box : mailBox) {
            createAndNotSubscribeMailBox(box);
        }

    }

    @Override
    public void createAndSubscribeMailBox(String mailBox) {
        // TODO Auto-generated method stub
        try {
            Mailbox.init(mailAuthentication.getAddress(), mailBox, true);

        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createAndNotSubscribeMailBox(String mailBox) {

        try {
            // Mailbox.init(mailAuthentication.getAddress(), "");
            Mailbox.init(mailAuthentication.getAddress(), mailBox, false);

        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public void prepareMailBox(String mailBox, MimeMessage... messages) {
        if (messages != null) {
            prepareMailBox(mailBox, Arrays.asList(messages));
        } else {
            throw new IllegalArgumentException("message is null");
        }
    }

    @Override
    public void prepareMailBox(String mailBox, List<MimeMessage> messages) {
        try {
            Mailbox mailbox = Mailbox.init(mailAuthentication.getAddress(), mailBox, false);
            mailbox.addAll(messages);

        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public void verifyMailbox(String mailbox) {
        // TODO Auto-generated method stub

        try {
            Mailbox result = Mailbox.get(mailAuthentication.getAddress(), mailbox);
            Assert.assertThat(result, Matchers.notNullValue());
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public void verifyMessageCount(String mailBox, int count) {
        // TODO Auto-generated method stub

        try {
            Mailbox mailbox = Mailbox.get(mailAuthentication.getAddress(), mailBox);

            Assert.assertThat(mailbox, Matchers.hasSize(count));
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        try {
            Mailbox.clearAll();
            Mailbox.init(mailAuthentication.getAddress(), "INBOX", false);
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String getMockUserEmail() {
        // TODO Auto-generated method stub
        return mailAuthentication.getAddress();
    }

    @Override
    public MimeMessage[] getReceivedMessages(String recipient) {

        try {
            Mailbox mailbox = Mailbox.get(new InternetAddress(recipient), "INBOX");

            return mailbox.getUnread().toArray(new MimeMessage[mailbox.getUnread().size()]);
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
