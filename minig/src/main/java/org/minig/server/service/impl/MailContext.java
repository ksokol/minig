package org.minig.server.service.impl;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

public interface MailContext {

    Session getSession();

    Store getStore();

    Folder getFolder(String parent, String path);

    @Deprecated
    Folder getFolder(String path);

    @Deprecated
    Folder getFolder(String path, boolean writeMode);

    Folder openFolder(String path);

    Folder getInbox();

    Folder getTrash();

    Folder getDraft();

    Folder getSent();

}