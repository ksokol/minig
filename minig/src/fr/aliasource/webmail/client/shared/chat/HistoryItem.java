package fr.aliasource.webmail.client.shared.chat;

import java.io.Serializable;
import java.util.Date;

public class HistoryItem implements Serializable {

	private static final long serialVersionUID = 3985729206707161083L;

	private String from;
	private String text;
	private Date timestamp;

	public HistoryItem() {

	}

	public HistoryItem(String from, String text) {
		this.from = from;
		this.text = text;
		this.timestamp = new Date();
	}

	public String getFrom() {
		return from;
	}

	public String getText() {
		return text;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
