package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class ForwardInfo implements Serializable {

	private static final long serialVersionUID = -1918883354506050891L;

	private boolean enabled;
	private boolean localCopy;
	private boolean allowed;

	private String email;

	public ForwardInfo() {
		allowed = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isLocalCopy() {
		return localCopy;
	}

	public void setLocalCopy(boolean localCopy) {
		this.localCopy = localCopy;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

}
