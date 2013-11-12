package org.jvnet.mock_javamail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dev@sokol-web.de <Kamill Sokol>
 */
public class MailboxBuilder {

    private String address;
    private String name;
    private boolean subscribed;
    private boolean exists;
    private List<Message> messages = new ArrayList<>();
    private boolean error;

    private boolean standalone = false;

    public MailboxBuilder(Address address) {
        this.address = address.toString();
    }

    public MailboxBuilder(String address) {
        this.address = address;
    }

    public MailboxBuilder inbox() {
        this.name = "INBOX";
        return this;
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

    public MailboxBuilder standalone() {
        this.standalone = true;
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
            String tmpName = (name == null) ? "INBOX" : name;
            String tmpName2 = tmpName;
            String path = tmpName;
            String parent = null;

            int lastIndexOf = tmpName.lastIndexOf(".");

            if (lastIndexOf != -1) {
                tmpName2 = tmpName.substring(lastIndexOf + 1);
                parent = tmpName.substring(0, lastIndexOf);
            }

            Mailbox mailbox = new Mailbox();

            mailbox.name = tmpName2;
            mailbox.address = new InternetAddress(address);
            mailbox.path = path;
            mailbox.parent = parent;
            mailbox.subscribed = subscribed;
            mailbox.exists = exists;
            mailbox.addAll(messages);
            mailbox.error = error;

            if(!standalone) {
                MailboxHolder.addFixture(mailbox);
            }

            return mailbox;
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
