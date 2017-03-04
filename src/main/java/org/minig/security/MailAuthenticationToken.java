package org.minig.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Kamill Sokol
 */
public class MailAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 2L;

    private final String domain;
    private final char folderSeparator;

    public MailAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String domain, char folderSeparator) {
        super(principal, credentials, authorities);
        this.domain = domain;
        this.folderSeparator = folderSeparator;
    }

    public String getDomain() {
        return domain;
    }

    public char getFolderSeparator() {
        return folderSeparator;
    }
}
