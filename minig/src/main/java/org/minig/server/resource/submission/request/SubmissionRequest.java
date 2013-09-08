package org.minig.server.resource.submission.request;

import org.minig.server.MailMessage;

public class SubmissionRequest {

	private MailMessage clientMessage;
	private String replyTo;

	public MailMessage getClientMessage() {
		return clientMessage;
	}

	public void setClientMessage(MailMessage clientMessage) {
		this.clientMessage = clientMessage;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

}
