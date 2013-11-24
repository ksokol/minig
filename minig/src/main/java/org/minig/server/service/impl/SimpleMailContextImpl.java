package org.minig.server.service.impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import org.minig.MailAuthentication;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Profile("prod")
public class SimpleMailContextImpl implements MailContext, DisposableBean {

    private static final Logger LOGGER = Logger.getLogger(SimpleMailContextImpl.class.getName());

    @Autowired
    private MailAuthentication authentication;

    private Deque<Folder> trackFetchedFolder = new ConcurrentLinkedDeque<Folder>();
    private Session session;
    private Store store;

    @Override
    public Session getSession() {
        checkSession();
        return session;
    }

    @Override
    public Store getStore() {
        checkSession();
        return store;
    }

    @Override
    public Folder getFolder(String parent, String path) {
        try {
            char separator = store.getDefaultFolder().getSeparator();

            return getFolder(parent + separator + path);
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Folder getFolder(String path, boolean writeMode) {
        checkSession();

        if (path == null) {
            return null;
        }

        try {
            Folder folder = store.getFolder(path);

            if (folder != null && folder.exists() && writeMode) {
                folder.open(Folder.READ_WRITE);
            }

            trackFetchedFolder.addLast(folder);

            return folder;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Folder openFolder(String path) {
        checkSession();

        try {
            Folder folder = store.getFolder(path);

            if (folder != null && folder.exists()) {
                folder.open(Folder.READ_WRITE);
                trackFetchedFolder.addLast(folder);
                return folder;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        throw new RuntimeException("folder does not exist");
    }

    @Override
    public Folder getFolder(String path) {
        return getFolder(path, true);
    }

    @Override
    public Folder getInbox() {
        Folder inbox = getFolder(authentication.getInboxFolder());
        Folder defaultFolder = null;

        try {
            if (inbox != null && !inbox.exists()) {
                defaultFolder = getStore().getDefaultFolder();
            } else {
                defaultFolder = inbox;
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return defaultFolder;
    }

    @Override
    public Folder getTrash() {
        return getFolder(authentication.getTrashFolder(), false);
    }

    @Override
    public Folder getDraft() {
        return getFolder(authentication.getDraftsFolder());
    }

    @Override
    public Folder getSent() {
        return getFolder(authentication.getSentFolder());
    }

    @Override
    public void destroy() throws Exception {
        Folder folderToBeClosed = null;

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("trackFetchedFolder: " + trackFetchedFolder.size());
        }

        while ((folderToBeClosed = trackFetchedFolder.pollFirst()) != null) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("closing: " + folderToBeClosed);
            }

            if (folderToBeClosed.isOpen()) {
                try {
                    folderToBeClosed.close(true);
                } catch (Exception e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }

        if (this.store != null) {
            this.store.close();
        }

        if (this.session != null) {
            this.session = null;
        }

        trackFetchedFolder.clear();
    }

    private void checkSession() {
        if (this.session == null) {
            synchronized (this) {
                if (this.session == null) {
                    this.session = Session.getInstance(this.authentication.getConnectionProperties(), new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(authentication.getUserMail(), authentication.getPassword());
                        }
                    });

                    try {
                        this.store = this.session.getStore();
                        store.connect(authentication.getDomain(), authentication.getUserMail(), authentication.getPassword());
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
