package org.minig.server.service.submission;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;

/**
 * @author Kamill Sokol
 */
public class JavaMailSenderFactory {

    public JavaMailSender newInstance(Session session) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setSession(session);
        return javaMailSender;
    }
}
