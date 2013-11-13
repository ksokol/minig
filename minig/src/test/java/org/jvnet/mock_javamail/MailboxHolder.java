package org.jvnet.mock_javamail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dev@sokol-web.de <Kamill Sokol>
 */
public class MailboxHolder {

    private static final Set<Mailbox> mailboxes = new HashSet<>();


    public static List<Mailbox> allMailboxes(Address address) {
        List<Mailbox> mailboxesOfAddress = new ArrayList<>();

        for (Mailbox mb : mailboxes) {
            if (mb.exists && mb.address.equals(address)) {
                mailboxesOfAddress.add(mb);
            }
        }

        return mailboxesOfAddress;
    }

    @Deprecated
    public synchronized static Mailbox get(Address a, String mailboxPath) {
        Mailbox mailbox = new MailboxBuilder(a).mailbox(mailboxPath).standalone().subscribed().exists().build();

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
    public static Mailbox get(String address, String mailboxPath) {
        try {
            return get(new InternetAddress(address), mailboxPath);
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Mailbox getFixture(Address address, String name) {
        for (Mailbox mb : mailboxes) {
            if (mb.address.equals(address) && name.equals(mb.path)) {
                return mb;
            }
        }

        return null;
    }

    public static void addFixture(Mailbox mailbox) {
        mailboxes.remove(mailbox);
        mailboxes.add(mailbox);
    }

    public static synchronized boolean remove(Mailbox mailbox) {
        return mailboxes.remove(mailbox);
    }

    public static void reset() {
        mailboxes.clear();
    }
}
