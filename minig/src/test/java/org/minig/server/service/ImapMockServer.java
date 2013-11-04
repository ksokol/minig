package org.minig.server.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;

import org.minig.MailAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.imap.ImapHostManagerImpl;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.PatchedInMemoryStore;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@Component("mockServer")
//@Profile("test")
class ImapMockServer implements InitializingBean, DisposableBean, SmtpAndImapMockServer {

    private static final Logger logger = LoggerFactory.getLogger(ImapMockServer.class);

    // #mail.-1145792675.INBOX.test
    private static final Pattern MAILBOX_ID = Pattern.compile("#mail\\.-[0-9]*\\.(.*)");

    @Autowired
    @Qualifier("javaMailProperties")
    private Properties javaMailProperties;

    @Autowired
    private MailAuthentication mailAuthentication;

    public GreenMail greenMail;
    public ImapHostManager imapHostManager;

    @Override
    public void destroy() throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        greenMail.stop();

        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
    }

    @Override
    public void shutdown() {
        try {
            destroy();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(javaMailProperties);

        Integer imapPort = Integer.valueOf(javaMailProperties.getProperty("mail.imap.port"));
        Integer smtpPort = Integer.valueOf(javaMailProperties.getProperty("mail.smtp.port"));

        String imapProtocol = javaMailProperties.getProperty("mail.store.protocol");
        String smtpProtocol = javaMailProperties.getProperty("mail.transport.protocol");

        logger.info("using protocol {} on port {}", smtpProtocol, smtpPort);
        logger.info("using protocol {} on port {}", imapProtocol, imapPort);

        ServerSetup imapServerSetupTest = new ServerSetup(imapPort, "localhost", imapProtocol);
        ServerSetup smtpServerSetupTest = new ServerSetup(smtpPort, "localhost", smtpProtocol);

        greenMail = new GreenMail(new ServerSetup[] { smtpServerSetupTest, imapServerSetupTest });
        greenMail.setUser(mailAuthentication.getUserMail(), mailAuthentication.getPassword());

        imapHostManager = greenMail.getManagers().getImapHostManager();

        reset();
        greenMail.start();
    }

    private GreenMailUser getUser() {
        return greenMail.getManagers().getUserManager().getUser(mailAuthentication.getUserMail());
    }

    private MailFolder emptyMailBox(String mailBox) {
        logger.debug("user context: {}", getUser().getLogin());

        try {
            imapHostManager.deleteMailbox(getUser(), mailBox);
        } catch (Exception e) {
        }

        try {
            MailFolder folder = imapHostManager.createMailbox(getUser(), mailBox);

            return folder;
        } catch (FolderException e) {
            return imapHostManager.getFolder(getUser(), mailBox);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getMockUserEmail() {
        return getUser().getEmail();
    }

    @Override
    public void createAndSubscribeMailBox(String mailBox) {
        try {
            imapHostManager.createMailbox(getUser(), mailBox);
            imapHostManager.subscribe(getUser(), mailBox);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createAndSubscribeMailBox(String... mailBox) {
        for (String mb : mailBox) {
            createAndNotSubscribeMailBox(mb);
        }
    }

    @Override
    public void createAndNotSubscribeMailBox(String mailBox) {
        try {
            imapHostManager.createMailbox(getUser(), mailBox);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.minig.server.service.SmtpAndImapMockServer#prepareMailBox(java.lang .String,
     * javax.mail.internet.MimeMessage)
     */
    @Override
    public void prepareMailBox(String mailBox, MimeMessage... messages) {
        MailFolder folder = emptyMailBox(mailBox);

        try {
            if (messages != null) {
                for (MimeMessage m : messages) {
                    folder.store(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.minig.server.service.SmtpAndImapMockServer#prepareMailBox(java.lang .String, java.util.List)
     */
    @Override
    public void prepareMailBox(String mailBox, List<MimeMessage> messages) {
        MailFolder folder = emptyMailBox(mailBox);

        try {
            if (messages != null) {
                for (MimeMessage m : messages) {
                    folder.store(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.minig.server.service.SmtpAndImapMockServer#verifyMailbox(java.lang .String)
     */
    @Override
    public void verifyMailbox(String mailbox) {
        MailFolder folder = imapHostManager.getFolder(getUser(), mailbox);
        Matcher matcher = MAILBOX_ID.matcher(folder.getFullName());

        assertTrue(matcher.matches());
        assertEquals(mailbox, matcher.group(1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.minig.server.service.SmtpAndImapMockServer#verifyMessageCount(java .lang.String, int)
     */
    @Override
    public void verifyMessageCount(String mailBox, int count) {
        MailFolder folder = imapHostManager.getFolder(getUser(), mailBox);
        assertEquals(count, folder.getMessageCount());
    }

    public void reset() {
        try {
            Field f = imapHostManager.getClass().getDeclaredField("store");
            f.setAccessible(true);
            f.set(imapHostManager, new PatchedInMemoryStore());

            Class<?> innerClazz = ImapHostManagerImpl.class.getDeclaredClasses()[1];

            Constructor<?> constructor = innerClazz.getDeclaredConstructor(ImapHostManagerImpl.class);
            constructor.setAccessible(true);

            Object inner = constructor.newInstance(imapHostManager);

            Field f2 = imapHostManager.getClass().getDeclaredField("subscriptions");
            f2.setAccessible(true);
            f2.set(imapHostManager, inner);

            imapHostManager.createMailbox(getUser(), mailAuthentication.getInboxFolder());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public MimeMessage[] getReceivedMessages(String recipient) {
        return greenMail.getReceivedMessages();
    }
}
