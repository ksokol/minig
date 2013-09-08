package fr.aliasource.webmail.client.shared;

public interface SubmissionRequest {

	public abstract IClientMessage getClientMessage();

	public abstract void setClientMessage(IClientMessage clientMessage);

	public abstract String getReplyTo();

	public abstract void setReplyTo(String replyTo);

}