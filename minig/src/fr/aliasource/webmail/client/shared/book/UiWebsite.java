package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;

public class UiWebsite implements Serializable {

	private static final long serialVersionUID = -3414267759790067277L;

	private String url;

	public UiWebsite() {
		// default constructor to please GWT
	}

	public UiWebsite(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
