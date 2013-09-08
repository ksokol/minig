package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;

public class UiInstantMessagingId implements Serializable {

	private static final long serialVersionUID = 6347123504430384830L;

	private String protocol;
	private String id;

	public UiInstantMessagingId() {
		// default constructor to please GWT
	}

	public UiInstantMessagingId(String protocol, String address) {
		this.protocol = protocol;
		this.id = address;
	}

	public String getId() {
		return id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
