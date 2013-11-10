package org.jvnet.mock_javamail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

/**
 * @author Kohsuke Kawaguchi
 * @author dev@sokol-web.de
 */
public class MockFolder extends Folder {
    private Mailbox mailbox;
    private MockFolder parent;

    private MockStore store;

    public MockFolder(MockStore store, Mailbox mailbox) {
        super(store);

        this.store = store;
        this.mailbox = mailbox;

        if (mailbox.getParent() != null) {
            this.parent = new MockFolder(store, mailbox.getParent());
        }
    }

    public String getName() {
        return mailbox.getName();
    }

    public String getFullName() {
        return mailbox.getPath();
    }

    public Folder getParent() throws MessagingException {
        return parent;
    }

    public boolean exists() throws MessagingException {
        return mailbox.isExists();
    }

    public Folder[] list(String pattern) throws MessagingException {
        List<MockFolder> mockFolders = new ArrayList<>();
        List<Mailbox> list = Mailbox.get(mailbox.getAddress());

        switch(pattern) {
            case "*": {
                for (Mailbox mb : list) {
                    if (mb.getPath().startsWith(mailbox.getPath())) {
                        mockFolders.add(new MockFolder(store, mb));
                    }
                }

                return mockFolders.toArray(new Folder[mockFolders.size()]);
            }
            case "%": {
                for (Mailbox mb : list) {
                    if (mb.getPath().matches(mailbox.getPath() + "\\.?[\\w]{0,}")) {
                        mockFolders.add(new MockFolder(store, mb));
                    }
                }

                return mockFolders.toArray(new Folder[mockFolders.size()]);
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    public char getSeparator() throws MessagingException {
        return store.getSeparator();
    }

    public int getType() throws MessagingException {
        return HOLDS_MESSAGES;
    }

    public boolean create(int type) throws MessagingException {
        switch(type) {
            case 1:    {
                mailbox = Mailbox.init(store.getAddress(), mailbox.getPath(), true, true);
                return true;
            } default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    public boolean hasNewMessages() throws MessagingException {
        return mailbox.getNewMessageCount() > 0;
    }

    public Folder getFolder(String name) throws MessagingException {
        String parent = mailbox.getPath();

        if (parent == null) {
            return store.getFolder(name);
        } else {
            return store.getFolder(mailbox.getPath() + getSeparator() + name);
        }
    }

    public boolean delete(boolean recurse) throws MessagingException {
        boolean result = true;

        if (recurse) {
            List<Mailbox> list = Mailbox.get(mailbox.getAddress());

            for (Mailbox mb : list) {
                if (mb.getPath().startsWith(mailbox.getPath())) {
                    result &= Mailbox.remove(mb);
                }
            }

        } else {
            result = Mailbox.remove(mailbox);
        }

        return result;
    }

    public boolean renameTo(Folder f) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    public void open(int mode) throws MessagingException {
        // always succeed
    }

    public void close(boolean expunge) throws MessagingException {
        if (expunge) {
            expunge();
        }
    }

    public boolean isOpen() {
        return true;
    }

    public Flags getPermanentFlags() {
        return null;
    }

    public int getMessageCount() throws MessagingException {
        return mailbox.size();
    }

    @Override
    public int getNewMessageCount() throws MessagingException {
        return mailbox.getNewMessageCount();
    }

    public Message getMessage(int msgnum) throws MessagingException {
        return mailbox.get(msgnum - 1);
    }

    @Override
    public Message[] getMessages(int low, int high) throws MessagingException {
        int low1 = low - 1;
        int high1 = high - 1;

        List<Message> messages = new ArrayList<Message>();
        for (int i = low1; i <= high1; i++) {
            Message m = mailbox.get(i);
            messages.add(m);

            try {
                Collection<Field> fields = getFields(m.getClass());

                for (Field f : fields) {
                    if ("folder".equals(f.getName())) {
                        try {
                            f.setAccessible(true);
                            f.set(m, this);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                        break;
                    }
                    ;
                }

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return messages.toArray(new Message[messages.size()]);
    }

    public static Collection<Field> getFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<String, Field>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fields.containsKey(field.getName())) {
                    fields.put(field.getName(), field);
                }
            }

            clazz = clazz.getSuperclass();
        }

        return fields.values();
    }

    public void appendMessages(Message[] msgs) throws MessagingException {
        for (Message msg : msgs) {

            if (msg.getHeader("Message-ID") == null) {
                msg.saveChanges();
            }

            mailbox.add(msg);

        }
    }

    public Message[] expunge() throws MessagingException {
        List<Message> expunged = new ArrayList<Message>();
        for (Message msg : mailbox) {
            if (msg.getFlags().contains(Flag.DELETED)) expunged.add(msg);
        }
        mailbox.removeAll(expunged);
        return expunged.toArray(new Message[expunged.size()]);
    }

    @Override
    public Message[] search(SearchTerm term) throws MessagingException {
        // TODO Auto-generated method stub
        Message[] search = super.search(term);

        for (Message m : search) {
            Collection<Field> fields = getFields(m.getClass());

            for (Field f : fields) {
                if ("folder".equals(f.getName())) {
                    try {

                        f.setAccessible(true);
                        f.set(m, this);
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        return search;
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
        // TODO Auto-generated method stub

        for (Message m : msgs) {
            Collection<Field> fields = getFields(m.getClass());

            for (Field f : fields) {
                if ("folder".equals(f.getName())) {
                    try {
                        f.setAccessible(true);
                        f.set(m, this);
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        super.copyMessages(msgs, folder);
    }
}
