package org.jvnet.mock_javamail;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.SearchTerm;

/**
 * @author dev@sokol-web.de <Kamill Sokol>
 *
 */
public class MockFolder extends Folder {

    private Mailbox mailbox;

    public MockFolder(Store store, Mailbox mailbox) {
        super(store);

        this.mailbox = mailbox;
    }

    @Override
    public String getName() {
        return mailbox.getName();
    }

    @Override
    public String getFullName() {
        return mailbox.getPath();
    }

    @Override
    public Folder getParent() throws MessagingException {
        if (mailbox.getParent() != null) {
            return new MockFolder(store, mailbox.getParent());
        }

        return null;
    }

    @Override
    public boolean exists() throws MessagingException {
        return mailbox.isExists();
    }

    @Override
    public Folder[] list(String pattern) throws MessagingException {
        List<MockFolder> mockFolders = new ArrayList<>();
        List<Mailbox> all = mailbox.getAll();

        switch(pattern) {
            case "*": {
                for (Mailbox mb : all) {
                    if (mb.getPath().startsWith(mailbox.getPath())) {
                        mockFolders.add(new MockFolder(getStore(), mb));
                    }
                }

                return mockFolders.toArray(new Folder[mockFolders.size()]);
            }
            case "%": {
                for (Mailbox mb : all) {
                    if (mb.getPath().matches(mailbox.getPath() + "\\.?[\\w]*")) {
                        mockFolders.add(new MockFolder(getStore(), mb));
                    }
                }

                return mockFolders.toArray(new Folder[mockFolders.size()]);
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public char getSeparator() throws MessagingException {
        return mailbox.getSeparator();
    }

    @Override
    public int getType() throws MessagingException {
        return HOLDS_MESSAGES;
    }

    @Override
    public boolean create(int type) throws MessagingException {
        switch(type) {
            case 1: {
                mailbox.existsNow();
                return true;
            } default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public boolean hasNewMessages() throws MessagingException {
        return mailbox.getNewMessageCount() > 0;
    }

    @Override
    public Folder getFolder(String name) throws MessagingException {
        String folderName = name;

        if (mailbox.getPath() != null) {
            folderName = mailbox.getPath() + getSeparator() + name;
        }

        return getStore().getFolder(folderName);
    }

    @Override
    public boolean delete(boolean recurse) throws MessagingException {
        boolean result = true;

        if (recurse) {
            for (Mailbox mb : mailbox.getAll()) {
                if (mb.getPath().startsWith(mailbox.getPath())) {
                    result &= mb.delete();
                }
            }
        } else {
            result = mailbox.delete();
        }

        return result;
    }

    @Override
    public boolean renameTo(Folder f) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void open(int mode) throws MessagingException {
        // always succeed
    }

    @Override
    public void close(boolean expunge) throws MessagingException {
        if (expunge) {
            expunge();
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public Flags getPermanentFlags() {
        return null;
    }

    @Override
    public int getMessageCount() throws MessagingException {
        return mailbox.size();
    }

    @Override
    public int getNewMessageCount() throws MessagingException {
        return mailbox.getNewMessageCount();
    }

    @Override
    public Message getMessage(int msgnum) throws MessagingException {
        return mailbox.get(msgnum - 1);
    }

    @Override
    public Message[] getMessages(int low, int high) throws MessagingException {
        int low1 = low - 1;
        int high1 = high - 1;
        List<Message> messages = new ArrayList<>();

        for (int i = low1; i <= high1; i++) {
            Message m = mailbox.get(i);
            messages.add(enhance(m));
        }

        return messages.toArray(new Message[messages.size()]);
    }

    @Override
    public void appendMessages(Message[] msgs) throws MessagingException {
        for (Message msg : msgs) {
            if (msg.getHeader("Message-ID") == null) {
                msg.saveChanges();
            }

            mailbox.add(msg);
        }
    }

    @Override
    public Message[] expunge() throws MessagingException {
        List<Message> expunged = new ArrayList<>();

        for (Message msg : mailbox) {
            if (msg.getFlags().contains(Flag.DELETED)) {
                expunged.add(msg);
            }
        }

        mailbox.removeAll(expunged);

        return expunged.toArray(new Message[expunged.size()]);
    }

    @Override
    public Message[] search(SearchTerm term) throws MessagingException {
        Message[] search = super.search(term);

        return enhance(search);
    }

    @Override
    public boolean isSubscribed() {
        return mailbox.isSubscribed();
    }

    @Override
    public void setSubscribed(boolean subscribe) throws MessagingException {
        mailbox.setSubscribed(subscribe);
    }

    @Override
    public void copyMessages(Message[] msgs, Folder folder) throws MessagingException {
        super.copyMessages(enhance(msgs), folder);
    }

    private Message enhance(Message msg) {
        return enhance(new Message[]{msg})[0];
    }

    private Message[] enhance(Message[] msgs) {
        for (Message m : msgs) {
            ReflectionTestUtils.setField(m, "folder", this);
        }

        return msgs;
    }
}
