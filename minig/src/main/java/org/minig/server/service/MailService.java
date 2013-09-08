package org.minig.server.service;

import java.util.List;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;

public interface MailService {

	MailMessageList firstPageMessagesByFolder(String folder);

	MailMessageList findMessagesByFolder(String folder, int page, int pageLength);

	MailMessage findMessage(CompositeId id);

	void deleteMessages(List<CompositeId> messageIdList);

	void deleteMessage(CompositeId messageId);

	void updateMessageFlags(MailMessage source);

	void updateMessagesFlags(MailMessageList source);

	// void createMessage(MailMessage source);

	void moveMessageToFolder(CompositeId message, String folder);

	void moveMessagesToFolder(List<CompositeId> messageIdList, String folder);

	void copyMessagesToFolder(List<CompositeId> messageIdList, String folder);

	MailMessage createDraftMessage(MailMessage message);

	MailMessage updateDraftMessage(MailMessage message);

}