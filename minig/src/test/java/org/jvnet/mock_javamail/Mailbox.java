package org.jvnet.mock_javamail;

import java.util.*;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * In-memory mailbox that hosts messages.
 * 
 * <p>
 * This class also maintains the 'unread' flag for messages that are newly added. This flag is automatically removed
 * when the message is retrieved, much like how MUA behaves. This flag affects {@link MockFolder#getNewMessageCount()}.
 * 
 * @author Kohsuke Kawaguchi
 * @author dev@sokol-web.de
 */
public class Mailbox extends ArrayList<Message> {
    private static final long serialVersionUID = 1L;

    private final String parent;

    private final Address address;
    private final String name;
    private final String path;
    private boolean exists;
    private char separator = '.'; //TODO

    /**
     * Of the mails in the {@link ArrayList}, these are considered unread.
     * 
     * <p>
     * Because we can't intercept every mutation of {@link ArrayList}, this set may contain messages that are no longer
     * in them.
     */
    private List<MimeMessage> unread = new ArrayList<MimeMessage>();

    private boolean error;

    private boolean subscribed;

    public Mailbox getParent() {
        if (parent == null) {
            return null; // new Mailbox(address, "INBOX", false, true);
        }

        Mailbox mailbox = Mailbox.get(address, parent);

        if (mailbox == null) {
            return new Mailbox(address, parent, false, false);
        } else {
            return mailbox;
        }

        // return null;
    }

    public Mailbox(Address address, String fullname, boolean subscribed, boolean exists) {
        this.address = address;
        this.subscribed = subscribed;
        this.exists = exists;
        this.path = fullname;

        int lastIndexOf = fullname.lastIndexOf(".");

        if (lastIndexOf != -1) {
            this.name = fullname.substring(lastIndexOf + 1);
            this.parent = fullname.substring(0, lastIndexOf);

        } else {
            this.name = fullname;
            this.parent = null;
        }
    }

    /**
     * Gets the e-mail address of this mailbox.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns true if this mailbox is flagged as 'error'.
     * 
     * @see #setError(boolean)
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets if this mailbox should be flagged as 'error'.
     * 
     * Any sending/receiving operation with an error mailbox will fail. This behavior can be used to test the error
     * handling behavior of the application.
     */
    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    private static final Set<Mailbox> mailboxes = new HashSet<Mailbox>();

    public synchronized List<Mailbox> getAll() {
        List<Mailbox> mailboxesOfUser = new ArrayList<>();

        for (Mailbox mb : mailboxes) {
            if (mb.exists && mb.getAddress().equals(address)) {
                mailboxesOfUser.add(mb);
            }
        }

        return mailboxesOfUser;
    }

    public void existsNow() {
        this.exists = true;
        this.subscribed = true;
    }

    /**
     * Get the inbox for the given address.
     */
    @Deprecated
    public synchronized static Mailbox get(Address a, String mailboxPath) {
        Mailbox mailbox = new Mailbox(a, mailboxPath, true, true);

        if (mailboxes.contains(mailbox)) {

            for (Mailbox mb : mailboxes) {
                if (mailbox.equals(mb)) {
                    return mb;
                }
            }
        }

        return null;
    }

    @Deprecated
    public static Mailbox get(String address, String mailboxPath) throws AddressException {
        return get(new InternetAddress(address), mailboxPath);
    }

    @Deprecated
    public static List<Mailbox> get(Address address) throws AddressException {
        List<Mailbox> mailboxesOfUser = new ArrayList<Mailbox>();

        for (Mailbox mb : mailboxes) {
            if (mb.exists && mb.getAddress().equals(address)) {
                mailboxesOfUser.add(mb);
            }
        }

        return mailboxesOfUser;
    }

    @Deprecated
    public static Mailbox init(String address, String mailboxPath, boolean subscribed) throws AddressException {
        Mailbox mailbox = new Mailbox(new InternetAddress(address), mailboxPath, subscribed, true);

        mailboxes.remove(mailbox);
        mailboxes.add(mailbox);

        return mailbox;
    }

    @Deprecated
    public static Mailbox init(Address address, String mailboxPath, boolean subscribed) throws AddressException {
        Mailbox mailbox = new Mailbox(address, mailboxPath, subscribed, true);

        mailboxes.remove(mailbox);
        mailboxes.add(mailbox);

        return mailbox;
    }

    @Deprecated
    public static Mailbox init(Address address, String mailboxPath, boolean subscribed, boolean exists) throws AddressException {
        Mailbox mailbox = new Mailbox(address, mailboxPath, subscribed, exists);

        mailboxes.remove(mailbox);
        mailboxes.add(mailbox);

        return mailbox;
    }

    public int getNewMessageCount() {
        // to compute the real size, we need to trim off all the e-mails that are no longer in the base set
        unread.retainAll(this);
        return unread.size();
    }

    public boolean isExists() {
        return exists;
    }

    @Override
    public int size() {
        return super.size();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Message get(int msgnum) {
        Message m = super.get(msgnum);
        unread.remove(m);
        return m;
    }

    public List<MimeMessage> getUnread() {
        return unread;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean addAll(Collection<? extends Message> messages) {
        // TODO
        unread.addAll((Collection<? extends MimeMessage>) messages);
        return super.addAll(messages);
    }

    @Override
    public boolean add(Message message) {
        // TODO
        unread.add((MimeMessage) message);
        return super.add(message);
    }

    /**
     * Removes the 'new' status from all the e-mails. Akin to "mark all e-mails as read" in the MUA.
     */
    public void clearNewStatus() {
        unread.clear();
    }

    /**
     * Discards all the mailboxes and its data.
     */
    public static void clearAll() {
        mailboxes.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1; // super.hashCode();
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        // if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Mailbox other = (Mailbox) obj;
        if (address == null) {
            if (other.address != null) return false;
        } else if (!address.equals(other.address)) return false;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }

    @Override
    public String toString() {
        return address + ":" + this.path + " " + super.toString();
    }

    @Deprecated
    public static boolean remove(Mailbox mailbox) {
        return mailboxes.remove(mailbox);
    }

    public boolean delete() {
        return mailboxes.remove(this);
    }


    public static boolean update(Mailbox mailbox) {
        mailboxes.remove(mailbox);
        return mailboxes.add(mailbox);
    }

    public char getSeparator() {
        return separator;
    }
}
