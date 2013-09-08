package org.minig.security;

import java.util.Properties;

public interface MailAuthentication {

    String getAddress();

    String getUserMail();

    String getPassword();

    String getDomain();

    String getInboxFolder();

    String getTrashFolder();

    String getDraftsFolder();

    String getSentFolder();

    char getFolderSeparator();

    Properties getConnectionProperties();
}
