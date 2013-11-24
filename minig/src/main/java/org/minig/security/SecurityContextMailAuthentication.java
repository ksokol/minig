package org.minig.security;

import java.util.Properties;

import org.minig.MailAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityContextMailAuthentication implements MailAuthentication {

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
        // TODO Auto-generated method stub
        return "INBOX";
    }

    @Override
    public String getTrashFolder() {
        // TODO Auto-generated method stub
        return "INBOX/Trash";
    }

    @Override
    public String getDraftsFolder() {
        // TODO Auto-generated method stub
        return "INBOX/Drafts";
    }

    @Override
    public String getSentFolder() {
        // TODO Auto-generated method stub
        return "INBOX/Sent";
    }

    @Override
    public char getFolderSeparator() {
        // TODO Auto-generated method stub
        return '/';
    }

    @Override
    public Properties getConnectionProperties() {
        return getMailAuthenticationToken().getConnectionProperties();
    }

}
