package org.minig.security;

import org.minig.server.service.impl.JavaMailPropertyBuilder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.List;
import java.util.Properties;

/**
 * @author Kamill Sokol
 */
public class MailAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MailAuthenticationToken authenticated;

        String[] split = authentication.getName().split("@");

        if (split.length != 2) {
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

            List<GrantedAuthority> ga = AuthorityUtils.createAuthorityList("ROLE_USER");
            authenticated = new MailAuthenticationToken(authentication.getName(), authentication.getCredentials(), ga, domain, store.getDefaultFolder().getSeparator());
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
