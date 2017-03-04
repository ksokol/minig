package org.minig.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * @author Kamill Sokol
 */
public class MailAuthentication implements Serializable {

    private static final long serialVersionUID = 2L;

    public String getEmailAddress() {
        return getAddress();
    }

    @Deprecated
    public String getAddress() {
        if (getUserMail().contains("@")) {
            return getUserMail();
        } else {
            return getUserMail() + "@" + getDomain();
        }
    }

    @Deprecated
    public String getUserMail() {
        return getMailAuthenticationToken().getName();
    }

    public String getPassword() {
        return (String) getMailAuthenticationToken().getCredentials();
    }

    public String getDomain() {
        return getMailAuthenticationToken().getDomain();
    }

    private MailAuthenticationToken getMailAuthenticationToken() {
        return (MailAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    public String getInboxFolder() {
        // TODO
        return "INBOX";
    }

    public String getTrashFolder() {
        // TODO
        return getInboxFolder() + getFolderSeparator() + "Trash";
    }

    public String getDraftsFolder() {
        // TODO
        return getInboxFolder() + getFolderSeparator() + "Drafts";
    }

    public String getSentFolder() {
        // TODO
        return getInboxFolder() + getFolderSeparator() + "Sent";
    }

    public char getFolderSeparator() {
        return getMailAuthenticationToken().getFolderSeparator();
    }
}
