package org.minig.server.service;

import org.minig.MailAuthentication;
import org.minig.server.TestConstants;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Kamill Sokol
 */
@Primary
@Component
public class MockMailAuthentication implements MailAuthentication {

    private String domain = "localhost";
    private String login = "testuser";
    private String password = "login";
    private String inboxFolder = "INBOX";
    private String trashFolder = "INBOX.Trash";
    private String sentFolder = "INBOX.Sent";
    private String draftsFolder = "INBOX.Drafts";
    private char separator = '.';

    @Override
    public String getEmailAddress() {
        return TestConstants.MOCK_USER;
    }

    @Override
    public String getAddress() {
        return TestConstants.MOCK_USER;
    }

    @Override
    public String getUserMail() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getInboxFolder() {
        return inboxFolder;
    }

    @Override
    public String getTrashFolder() {
        return trashFolder;
    }

    @Override
    public String getDraftsFolder() {
        return draftsFolder;
    }

    @Override
    public String getSentFolder() {
        return sentFolder;
    }

    @Override
    public char getFolderSeparator() {
        return separator;
    }

}
