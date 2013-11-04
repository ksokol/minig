package org.minig.server.service;

import java.util.List;

import javax.mail.internet.MimeMessage;

public interface SmtpAndImapMockServer {

    public abstract void createAndSubscribeMailBox(String... mailBox);

    public abstract void createAndSubscribeMailBox(String mailBox);

    public abstract void createAndNotSubscribeMailBox(String mailBox);

    public abstract void prepareMailBox(String mailBox, MimeMessage... messages);

    public abstract void prepareMailBox(String mailBox, List<MimeMessage> messages);

    public abstract void verifyMailbox(String mailbox);

    public abstract void verifyMessageCount(String mailBox, int count);

    public void reset();

    public String getMockUserEmail();

    public MimeMessage[] getReceivedMessages(String recipient);

    public void shutdown();

}