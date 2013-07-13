package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;

public class UiEmail implements Serializable {

	private static final long serialVersionUID = -1626961559345293253L;

	public UiEmail() {
		// default constructor to please GWT
	}

	public UiEmail(String email) {
		super();
		this.email = email;
	}

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
