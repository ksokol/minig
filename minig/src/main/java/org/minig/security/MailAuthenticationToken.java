package org.minig.security;

import java.util.Collection;
import java.util.Properties;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MailAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = -6332698097449169633L;

	private String domain;

	private Properties connectionProperties;

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

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Properties getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

}
