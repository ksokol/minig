package fr.aliasource.webmail.client.rpc;

import java.io.Serializable;

public class SendResponse implements Serializable {

	private static final long serialVersionUID = 8123662869221984361L;

	private String reason;

	public SendResponse() {

	}

	public SendResponse(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isOk() {
		return reason == null;
	}

}
