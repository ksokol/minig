package org.minig.security;

import org.minig.MailAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * @author Kamill Sokol
 */
class SecurityContextMailAuthentication implements MailAuthentication, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String getEmailAddress() {
        return getAddress();
    }

    @Override
    public String getAddress() {
        if (getUserMail().contains("@")) {
            return getUserMail();
        } else {
            return getUserMail() + "@" + getDomain();
        }
    }

    @Override
    public String getUserMail() {
        return getMailAuthenticationToken().getName();
    }

    @Override
    public String getPassword() {
        return (String) getMailAuthenticationToken().getCredentials();
    }

    @Override
    public String getDomain() {
        return getMailAuthenticationToken().getDomain();
    }

    private MailAuthenticationToken getMailAuthenticationToken() {
        return (MailAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getInboxFolder() {
        // TODO
        return "INBOX";
    }

    @Override
    public String getTrashFolder() {
        // TODO
        return "INBOX/Trash";
    }

    @Override
    public String getDraftsFolder() {
        // TODO
        return "INBOX/Drafts";
    }

    @Override
    public String getSentFolder() {
        // TODO
        return "INBOX/Sent";
    }

    @Override
    public char getFolderSeparator() {
        // TODO
        return '/';
    }

}
