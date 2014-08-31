package org.minig.test.javamail;

import org.junit.rules.ExternalResource;
import org.minig.server.TestConstants;

import javax.mail.Message;
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
}
