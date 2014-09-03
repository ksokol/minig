package org.minig.test.javamail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamill Sokol
 */
public class MailboxBuilder {

    private String address;
    private String name;
    private boolean subscribed;
    private boolean exists;
    private List<Message> messages = new ArrayList<>();
    private boolean error;

    public MailboxBuilder(Address address) {
        this.address = address.toString();
    }

    public MailboxBuilder(String address) {
        this.address = address;
    }

    public MailboxBuilder mailbox(String name) {
        this.name = name;
        return this;
    }

    public MailboxBuilder subscribed() {
        this.subscribed = true;
        return this;
    }

    public MailboxBuilder subscribed(boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public MailboxBuilder exists() {
        this.exists = true;
        return this;
    }

    public MailboxBuilder exists(boolean exists) {
        this.exists = exists;
        return this;
    }

    public MailboxBuilder addMessage(Message message) {
        this.messages.add(message);
        return this;
    }

    public MailboxBuilder throwError() {
        error = true;
        return this;
    }

    public Mailbox build() {
        try {
            String tmpName = name;
            String path = name;
            String parent = null;

            int lastIndexOf = name.lastIndexOf(".");

            if (lastIndexOf != -1) {
                tmpName = name.substring(lastIndexOf + 1);
                parent = name.substring(0, lastIndexOf);
            }

            Mailbox mailbox = new Mailbox();

            mailbox.name = tmpName;
            mailbox.address = new InternetAddress(address);
            mailbox.path = path;
            mailbox.parent = parent;
            mailbox.subscribed = subscribed;
            mailbox.exists = exists;
            mailbox.addAll(messages);
            mailbox.error = error;

            MailboxHolder.add(mailbox);

            return mailbox;
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void buildDefault(String mailbox) {
        //TODO fix me
        if(MailboxHolder.get(mailbox, "INBOX") == null) {
            new MailboxBuilder(mailbox).mailbox("INBOX").subscribed().exists().build();
        }
        if(MailboxHolder.get(mailbox, "INBOX.Drafts") == null) {
            new MailboxBuilder(mailbox).mailbox("INBOX.Drafts").subscribed().exists().build();
        }
        if(MailboxHolder.get(mailbox, "INBOX.Sent") == null) {
            new MailboxBuilder(mailbox).mailbox("INBOX.Sent").subscribed().exists().build();
        }
    }
}
