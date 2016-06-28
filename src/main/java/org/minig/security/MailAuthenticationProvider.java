package org.minig.security;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.minig.server.service.impl.JavaMailPropertyBuilder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Kamill Sokol
 */
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

        try {
            Properties javaMailProperties = new JavaMailPropertyBuilder(domain).build();
            Session session = Session.getInstance(javaMailProperties, null);

            store = session.getStore();
            store.connect(domain, authentication.getName(), password);

            List<SimpleGrantedAuthority> ga = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            authenticated = new MailAuthenticationToken(authentication.getName(), authentication.getCredentials(), ga, domain);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException ignored) {
                }
            }
        }

        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
