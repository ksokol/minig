package org.minig.server.service;

import java.util.Properties;

import org.minig.MailAuthentication;
import org.springframework.stereotype.Component;

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
        return "testuser@localhost";
    }

    @Override
    public String getAddress() {
        return "testuser@localhost";
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

    @Override
    public Properties getConnectionProperties() {
        Properties javaMailProperties = new Properties();

        javaMailProperties.setProperty("mail.store.protocol", "imap");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.imap.port", "143");
        javaMailProperties.put("mail.smtp.host", domain);

        return javaMailProperties;
    }

}
