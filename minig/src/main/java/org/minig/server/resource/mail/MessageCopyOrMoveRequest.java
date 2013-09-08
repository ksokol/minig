package org.minig.server.resource.mail;

import java.util.List;

import org.minig.server.service.CompositeId;

public class MessageCopyOrMoveRequest {

	private String folder;
	private List<CompositeId> messageIdList;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List<CompositeId> getMessageIdList() {
		return messageIdList;
	}

	public void setMessageIdList(List<CompositeId> messageIdList) {
		this.messageIdList = messageIdList;
	}

}
