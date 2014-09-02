package org.minig.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Kamill Sokol
 */
public class MailAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private String domain;

	public MailAuthenticationToken() {
		super("anonymous", null);
	}

	public MailAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String domain) {
		super(principal, credentials, authorities);
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

}
