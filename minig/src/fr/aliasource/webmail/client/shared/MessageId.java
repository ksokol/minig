package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class MessageId implements Serializable {

	private static final long serialVersionUID = -4608411588049227451L;

	private long messageId;
	
	public MessageId() {
	}
	
	public MessageId(long messageId) {
		this.messageId = messageId;
	}
	
	public long getMessageId() {
		return messageId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (messageId ^ (messageId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageId other = (MessageId) obj;
		if (messageId != other.messageId)
			return false;
		return true;
	}
	
}
