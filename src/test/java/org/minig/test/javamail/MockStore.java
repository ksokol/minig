package org.minig.test.javamail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

/**
 * {@link Store} backed by {@link Mailbox}.
 * 
 * @author Kohsuke Kawaguchi
 * @author Kamill Sokol
 */
public class MockStore extends Store {

    private InternetAddress address;

    public MockStore(Session session, URLName urlname) {
        super(session, urlname);
    }

    public void connect() throws MessagingException {
        connect(url.getHost(), url.getPort(), url.getUsername(), url.getPassword());
    }

    protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
        address = new InternetAddress(user);

        Mailbox mailbox = MailboxHolder.get(address, "INBOX");

        if(mailbox == null) {
            MailboxBuilder.buildDefault(address.getAddress());
            mailbox = MailboxHolder.get(address, "INBOX");
        }

        if (mailbox.error) {
            throw new MessagingException("Simulated error connecting to " + address);
        }

        return true;
    }

    public Folder getDefaultFolder() throws MessagingException {
        Mailbox mailbox = MailboxHolder.get(address, "INBOX");

        return new MockFolder(this, mailbox);
    }

    @Override
    public Folder getFolder(String name) throws MessagingException {
        Mailbox mailbox = MailboxHolder.get(address, name);

        if (mailbox == null) {
            mailbox = new MailboxBuilder(address).mailbox(name).subscribed(true).exists(false).build();
        }

        return new MockFolder(this, mailbox);
    }

    public Folder getFolder(URLName url) throws MessagingException {
        throw new UnsupportedOperationException();
    }
}
