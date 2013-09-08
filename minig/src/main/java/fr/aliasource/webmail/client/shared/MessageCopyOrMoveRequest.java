package fr.aliasource.webmail.client.shared;

import java.util.List;

public interface MessageCopyOrMoveRequest {

	public String getFolder();

	public void setFolder(String folder);

	public List<Id> getMessageIdList();

	public void setMessageIdList(List<Id> messageIdList);
}
