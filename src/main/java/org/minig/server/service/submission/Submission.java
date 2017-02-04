package org.minig.server.service.submission;

import org.minig.MailAuthentication;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Kamill Sokol
 */
@Component
public class Submission {

    private static final String DSN0 = "mail.smtp.dsn.notify";
    private static final String DSN1 = "mail.smtp.dsn.ret";

    @Autowired
    private MailContext mailContext;

    @Autowired
    private MailAuthentication authentication;

    @Autowired
    private JavaMailSenderFactory javaMailSenderFactory;

    public void submit(Mime4jMessage message) {
        Assert.notNull(message, "message is null");

        try {
            submitInternal(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void submitInternal(Mime4jMessage message) throws MessagingException {
        JavaMailSender mailSender = javaMailSenderFactory.newInstance(mailContext.getSession());
        MimeMessage target = message.toMessage();
        Session session = mailContext.getSession();
        Properties properties = session.getProperties();

        InternetAddress internetAddress = new InternetAddress(authentication.getEmailAddress());
        target.setFrom(internetAddress);

        if (message.isDSN()) {
            properties.put(DSN0, "SUCCESS,FAILURE,DELAY ORCPT=rfc822;" + authentication.getEmailAddress());
            properties.put(DSN1, "FULL");
        }

        if(message.isReturnReceipt()) {
            target.setHeader("Disposition-Notification-To", authentication.getEmailAddress());
        }

        try {
            clean(target);
            mailSender.send(target);
        } finally {
            properties.remove(DSN0);
            properties.remove(DSN1);
        }
    }

    private void clean(Message message) throws MessagingException {
        message.removeHeader("X-Mozilla-Draft-Info");
    }
}
