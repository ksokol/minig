package org.minig;

import java.util.Properties;

public interface MailAuthentication {

    String getEmailAddress();

    /**
     * use getEmailAddress instead
     * @return
     */
    @Deprecated
    String getAddress();

    /**
     * use getEmailAddress instead
     * @return
     */
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
