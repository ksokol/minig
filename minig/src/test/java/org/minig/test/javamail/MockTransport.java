package org.minig.test.javamail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;

/**
 * Mock {@link Transport} to deliver to {@link Mailbox}.
 *
 * @author Kohsuke Kawaguchi
 * @author Kamill Sokol
 */
public class MockTransport extends Transport {

    public MockTransport(Session session, URLName urlname) {
        super(session, urlname);
    }

    public void connect(String host, int port, String user, String password) throws MessagingException {
        // noop
    }

    public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
        for (Address a : addresses) {
            // create a copy to isolate the sender and the receiver
            Mailbox mailbox = MailboxHolder.get(a, "INBOX");
            if (mailbox == null) {
                InternetAddress ia = (InternetAddress) a;
                MailboxBuilder.buildDefault(ia.getAddress());
                mailbox = MailboxHolder.get(a, "INBOX");
                mailbox.add(msg);
            } else {
                if (mailbox.error) {
                    throw new MessagingException("Simulated error sending message to " + a);
                }   else {
                    mailbox.add(msg);
                }
            }
        }
    }
}
