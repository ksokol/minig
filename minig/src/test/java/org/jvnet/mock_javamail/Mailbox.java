package org.jvnet.mock_javamail;

import java.util.*;

import javax.mail.Address;
import javax.mail.Message;

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

    //TODO
    String parent;
    Address address;
    String name;
    String path;

    boolean exists;
    char separator = '.'; //TODO

    /**
     * Of the mails in the {@link ArrayList}, these are considered unread.
     * 
     * <p>
     * Because we can't intercept every mutation of {@link ArrayList}, this set may contain messages that are no longer
     * in them.
     */
    private List<Message> unread = new ArrayList<>();

    /**
     * Sets if this mailbox should be flagged as 'error'.
     *
     * Any sending/receiving operation with an error mailbox will fail. This behavior can be used to test the error
     * handling behavior of the application.
     */
    boolean error;

    boolean subscribed;

    Mailbox getParent() {
        if (parent == null) {
            return null;
        }

        Mailbox mailbox = new MailboxBuilder(address).mailbox(parent).subscribed().exists().standalone().build();

        if (MailboxHolder.allMailboxes().contains(mailbox)) {
            for (Mailbox mb : MailboxHolder.allMailboxes()) {
                if (mailbox.equals(mb)) {
                    return mb;
                }
            }
        }

        return new MailboxBuilder(address).mailbox(parent).build();
    }

    Mailbox() {}

    public List<Mailbox> getAll() {
        List<Mailbox> mailboxesOfUser = new ArrayList<>();

        for (Mailbox mb : MailboxHolder.allMailboxes()) {
            if (mb.exists && mb.address.equals(address)) {
                mailboxesOfUser.add(mb);
            }
        }

        return mailboxesOfUser;
    }

    public void existsNow() {
        this.exists = true;
        this.subscribed = true;
    }

    public int getNewMessageCount() {
        // to compute the real size, we need to trim off all the e-mails that are no longer in the base set
        unread.retainAll(this);
        return unread.size();
    }

    public Message get(int msgnum) {
        Message m = super.get(msgnum);
        unread.remove(m);
        return m;
    }

    public List<Message> getUnread() {
        return unread;
    }


    public boolean addAll(Collection<? extends Message> messages) {
        unread.addAll(messages);
        return super.addAll(messages);
    }

    @Override
    public boolean add(Message message) {
        unread.add(message);
        return super.add(message);
    }

    /**
     * Removes the 'new' status from all the e-mails. Akin to "mark all e-mails as read" in the MUA.
     */
    public void clearNewStatus() {
        unread.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
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
    public boolean delete() {
        return MailboxHolder.remove(this);
    }

    public char getSeparator() {
        return separator;
    }
}
