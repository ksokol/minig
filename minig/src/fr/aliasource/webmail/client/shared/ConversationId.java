package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class ConversationId implements Serializable {

	private static final long serialVersionUID = -5112694297505126995L;

	private String conversationId;

	public ConversationId() {
	}
	
	public ConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	public String getConversationId() {
		return conversationId;
	}
	
	public String getSourceFolder() {
		int idx = conversationId.lastIndexOf('/');
		if (idx > 0) {
			return conversationId.substring(0, idx);
		}
		return null;
	}
	
	public boolean hasFolder() {
		return conversationId.contains("/");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conversationId == null) ? 0 : conversationId.hashCode());
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
		ConversationId other = (ConversationId) obj;
		if (conversationId == null) {
			if (other.conversationId != null)
				return false;
		} else if (!conversationId.equals(other.conversationId))
			return false;
		return true;
	}
	
	
}
