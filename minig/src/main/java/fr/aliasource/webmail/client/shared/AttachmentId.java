package fr.aliasource.webmail.client.shared;

public interface AttachmentId {

	public abstract String getId();

	public abstract String getFolder();

	public abstract String getMessageId();

	public abstract String getFileName();
}
