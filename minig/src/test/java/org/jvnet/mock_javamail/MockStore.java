package org.jvnet.mock_javamail;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;

/**
 * {@link Store} backed by {@link Mailbox}.
 * 
 * @author Kohsuke Kawaguchi
 * @author dev@sokol-web.de
 */
public class MockStore extends Store {

    private static final char SEPARATOR = '.';

    private Address address;

    public MockStore(Session session, URLName urlname) {
        super(session, urlname);
    }

    public void connect() throws MessagingException {
        connect(url.getHost(), url.getPort(), url.getUsername(), url.getPassword());
    }

    protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
        String concat = user + '@' + host;
        address = new InternetAddress(concat);

        // TODO
        Mailbox mailbox = Mailbox.get(address, "INBOX");

        if (mailbox.isError()) throw new MessagingException("Simulated error connecting to " + address);

        return true;
    }

    public Folder getDefaultFolder() throws MessagingException {
        Mailbox mailbox = Mailbox.get(address, "INBOX");

        return new MockFolder(this, mailbox);
    }

    public Folder getFolder(String name) throws MessagingException {
        Mailbox mailbox = Mailbox.get(address, name);

        if (mailbox == null) {
            return new MockFolder(this, Mailbox.init(address, name, true, false));
        } else {
            return new MockFolder(this, mailbox);
        }
    }

    public Folder getFolder(URLName url) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    public Address getAddress() {
        return address;
    }

    public char getSeparator() {
        return SEPARATOR;
    }
}
