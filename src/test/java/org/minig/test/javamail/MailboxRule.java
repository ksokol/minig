package org.minig.test.javamail;

import org.junit.rules.ExternalResource;
import org.minig.server.TestConstants;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kamill Sokol
 */
public class MailboxRule extends ExternalResource {

    private final String emailAddress;

    /**
     * @deprecated Use {@link #MailboxRule(String)} instead.
     */
    @Deprecated
    public MailboxRule() {
        this(TestConstants.MOCK_USER);
    }

    public MailboxRule(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    protected void before() throws Throwable {
        MailboxHolder.reset();
        MailboxBuilder.buildDefault(emailAddress);
    }

    @Override
    protected void after() {
        MailboxHolder.reset();
    }

    public void createFolder(String user, String mailBox) {
        new MailboxBuilder(user).mailbox(mailBox).subscribed().exists().build();
    }

    public Mailbox getMailbox(String folder) {
        return MailboxHolder.get(emailAddress, folder);
    }

    /**
     * Use {@link #getFirstInFolder(String)} instead.
     */
    @Deprecated
    public Message getFirstInInbox(String emailAddress) {
        Iterator<Message> inbox = MailboxHolder.get(emailAddress, "INBOX").iterator();
        if(inbox.hasNext()) {
            return inbox.next();
        }
        throw new IllegalArgumentException("empty INBOX");
    }

    public Optional<Message> getFirstInFolder(String folder) {
        Mailbox mailbox = MailboxHolder.get(emailAddress, folder);
        if(mailbox == null) {
            return Optional.empty();
        }
        return mailbox.stream().findFirst();
    }

    public List<Message> getAllInFolder(String folder) {
        Mailbox mailbox = MailboxHolder.get(emailAddress, folder);
        if(mailbox == null) {
            return Collections.emptyList();
        }
        return mailbox.stream().collect(Collectors.toList());
    }

    public void append(String mailboxPath, MimeMessage...messages) {
        Mailbox mailbox = MailboxHolder.get(emailAddress, mailboxPath);
        if(mailbox == null) {
            mailbox = new MailboxBuilder(emailAddress).mailbox(mailboxPath).subscribed(false).exists().build();
        }
        mailbox.addAll(Arrays.asList(messages));
    }
}
