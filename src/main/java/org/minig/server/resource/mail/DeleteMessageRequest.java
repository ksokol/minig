package org.minig.server.resource.mail;

import java.util.List;

import org.minig.server.service.CompositeId;

public class DeleteMessageRequest {

	private List<CompositeId> messageIdList;

	public List<CompositeId> getMessageIdList() {
		return messageIdList;
	}

	public void setMessageIdList(List<CompositeId> messageIdList) {
		this.messageIdList = messageIdList;
	}

}
