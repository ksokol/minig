package org.minig.security;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MailAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MailAuthenticationToken authenticated;

        String[] split = authentication.getName().split("@");

        if (split == null || split.length != 2) {
            throw new UsernameNotFoundException("not a valid authentication object");
        }

        String domain = split[1];
        String password = (String) authentication.getCredentials();
        Store store = null;
        // System.out.println("do login with: " + login + " - " + password +
        // " - " + domain);

        // TODO: test
        // http://harikrishnan83.wordpress.com/2009/01/24/access-gmail-with-imap-using-java-mail-api/
        // Properties props = System.getProperties();
        // props.setProperty("mail.store.protocol", "imap");
        try {
            // Session session = Session.getDefaultInstance(props, null);
            // Store store = session.getStore("imap");
            // store.connect(domain, login + "@" + domain, password);
            //

            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imap");

            Session session = Session.getDefaultInstance(props, null);

            // http://stackoverflow.com/questions/1921981/imap-javax-mail-fetching-only-body-without-attachment
            // session.setDebug(true);
            store = session.getStore("imap");
            store.connect(domain, authentication.getName(), password);

            List<SimpleGrantedAuthority> ga = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            authenticated = new MailAuthenticationToken(authentication.getName(), authentication.getCredentials(), ga, domain);

            // TODO
            Properties javaMailProperties = new Properties();
            javaMailProperties.setProperty("mail.store.protocol", "imap");
            // javaMailProperties.setProperty("mail.debug", "true");
            // props.setProperty("mail.store.protocol", "imap");
            // props.setProperty("mail.smtp.host", authentication.getDomain());
            javaMailProperties.put("mail.smtp.starttls.enable", "true");
            // props.setProperty("mail.user", "myuser");
            // props.setProperty("mail.password", "mypwd");

            // props.setProperty("mail.smtp.host",
            // credentials.getDomain());
            // props.setProperty("mail.smtp.host",
            // credentials.getDomain());
            // TODO
            // props.put("mail.smtp.port", "8025");
            javaMailProperties.put("mail.smtp.auth", "true");
            javaMailProperties.put("mail.imap.port", "143");

            javaMailProperties.put("mail.smtp.host", domain);
            // javaMailProperties.put("mail.debug", "true");
            authenticated.setConnectionProperties(javaMailProperties);

        } catch (Exception e) {
            // throw new RuntimeException(e.getMessage(), e);
            throw new UsernameNotFoundException(e.getMessage());
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                }
            }
        }

        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // @Override
    // public void login(String login, String domain, String password) {
    // // TODO Auto-generated method stub
    //
    // credentials = new Credentials(login + "@" + domain, password);
    //
    // // System.out.println("do login with: " + login + " - " + password +
    // // " - " + domain);
    //
    // // TODO: test
    // //
    // http://harikrishnan83.wordpress.com/2009/01/24/access-gmail-with-imap-using-java-mail-api/
    // // Properties props = System.getProperties();
    // // props.setProperty("mail.store.protocol", "imap");
    // try {
    // // Session session = Session.getDefaultInstance(props, null);
    // // Store store = session.getStore("imap");
    // // store.connect(domain, login + "@" + domain, password);
    // //
    // Store store = Utils.getStore(credentials);
    // store.close();
    //
    // javaxMailFolderService = new JavaxMailFolderService(credentials);
    //
    // } catch (Exception e) {
    // throw new RuntimeException(e.getMessage(), e);
    // }
    //
    // // throw new UnsupportedOperationException();
    //
    // }
}
