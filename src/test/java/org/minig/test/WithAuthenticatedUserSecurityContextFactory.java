package org.minig.test;

import org.minig.security.MailAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static org.minig.server.TestConstants.MOCK_USER;

/**
 * @author Kamill Sokol
 */
public class WithAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthenticatedUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthenticatedUser withAuthenticatedUser) {
        MailAuthenticationToken authentication = new MailAuthenticationToken(
                MOCK_USER,
                "login",
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                "localhost",
                '.'
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
