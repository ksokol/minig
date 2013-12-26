package org.minig;

import java.util.Properties;

public interface MailAuthentication {

    String getEmailAddress();

    @Deprecated
    String getAddress();

    @Deprecated
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
