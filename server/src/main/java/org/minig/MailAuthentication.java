package org.minig;

/**
 * @author Kamill Sokol
 */
public interface MailAuthentication {

    String getEmailAddress();

    /**
     * use {@link MailAuthentication#getEmailAddress()} instead
     */
    @Deprecated
    String getAddress();

    /**
     * use {@link MailAuthentication#getEmailAddress()} instead
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

}
