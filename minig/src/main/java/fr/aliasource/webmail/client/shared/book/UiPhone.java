package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;

public class UiPhone implements Serializable {

	private static final long serialVersionUID = -6997356612619253858L;

	private String number;

	public UiPhone() {
		// default constructor to please GWT
	}

	public UiPhone(String number) {
		super();
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
