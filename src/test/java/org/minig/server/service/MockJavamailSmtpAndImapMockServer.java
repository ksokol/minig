package org.minig.server.service;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.minig.security.MailAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Deprecated
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
        new MailboxBuilder(mailAuthentication.getAddress()).mailbox(mailBox).subscribed().exists().build();
    }

    @Override
    public void createAndNotSubscribeMailBox(String mailBox) {
        new MailboxBuilder(mailAuthentication.getAddress()).mailbox(mailBox).subscribed(false).exists().build();
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
        Mailbox mailbox = new MailboxBuilder(mailAuthentication.getAddress()).mailbox(mailBox).subscribed(false).exists().build();
        mailbox.addAll(messages);
    }

    @Override
    public void verifyMailbox(String mailbox) {
        Mailbox result = MailboxHolder.get(mailAuthentication.getAddress(), mailbox);
        Assert.assertThat(result, Matchers.notNullValue());
    }

    @Override
    public void verifyMessageCount(String mailBox, int count) {
        Mailbox mailbox = MailboxHolder.get(mailAuthentication.getAddress(), mailBox);
        Assert.assertThat(mailbox, Matchers.hasSize(count));
    }

    @Override
    public void reset() {
        MailboxHolder.reset();
        new MailboxBuilder(mailAuthentication.getAddress()).mailbox("INBOX").subscribed(false).exists().build();
    }

    @Override
    public MimeMessage[] getReceivedMessages(String recipient) {
        Mailbox mailbox = MailboxHolder.get(recipient, "INBOX");
        return mailbox.getUnread().toArray(new MimeMessage[mailbox.getUnread().size()]);
    }

}
