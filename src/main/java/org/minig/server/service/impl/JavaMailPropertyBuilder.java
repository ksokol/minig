package org.minig.server.service.impl;

import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.util.Assert;

import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @author Kamill Sokol
 */
public class JavaMailPropertyBuilder {

    private final String domain;

    public JavaMailPropertyBuilder(String domain) {
        Assert.hasText(domain);
        this.domain = domain;
    }

    public Properties build() {
        try {
            Properties javaMailProperties = new Properties();
            MailSSLSocketFactory sf = new MailSSLSocketFactory();

            sf.setTrustAllHosts(true);

            javaMailProperties.put("mail.store.protocol", "imap");
            javaMailProperties.put("mail.imap.starttls.enable", "true");
            javaMailProperties.put("mail.smtp.starttls.enable", "true");
            javaMailProperties.put("mail.smtp.auth", "true");
            javaMailProperties.put("mail.imap.port", "143");
            javaMailProperties.put("mail.smtp.host", domain);
            javaMailProperties.put("mail.smtp.ssl.socketFactory", sf);
            javaMailProperties.put("mail.imap.ssl.socketFactory", sf);

            javaMailProperties.put("mail.smtp.ssl.checkserveridentity", "true");
            //javaMailProperties.put("mail.debug", "true");

            return javaMailProperties;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
