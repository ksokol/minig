package org.minig.test.javamail;

import org.junit.rules.ExternalResource;
import org.minig.server.TestConstants;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;

/**
 * @author Kamill Sokol
 */
public class MailboxRule extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        MailboxHolder.reset();
        MailboxBuilder.buildDefault(TestConstants.MOCK_USER);
    }

    @Override
    protected void after() {
        MailboxHolder.reset();
    }

    public Message getFirstInInbox(String emailAddress) {
        Iterator<Message> inbox = MailboxHolder.get(emailAddress, "INBOX").iterator();
        if(inbox.hasNext()) {
            return inbox.next();
        }
        throw new IllegalArgumentException("empty INBOX");
    }

    public void appendInbox(MimeMessage message) {
        append("INBOX", message);
    }

    public void append(String mailbox, MimeMessage message) {
        new MailboxBuilder(TestConstants.MOCK_USER)
                .mailbox(mailbox)
                .subscribed(true)
                .exists().build()
                .add(message);
    }
}
