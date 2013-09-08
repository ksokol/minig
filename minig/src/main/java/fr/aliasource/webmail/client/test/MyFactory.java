package fr.aliasource.webmail.client.test;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import fr.aliasource.webmail.client.shared.IAttachmentMetadata;
import fr.aliasource.webmail.client.shared.IAttachmentMetadataList;
import fr.aliasource.webmail.client.shared.IBody;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.shared.ICreateFolderRequest;
import fr.aliasource.webmail.client.shared.IDeleteMessagesRequest;
import fr.aliasource.webmail.client.shared.IEmailAddress;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.shared.IFolderList;
import fr.aliasource.webmail.client.shared.IMessageId;
import fr.aliasource.webmail.client.shared.Id;
import fr.aliasource.webmail.client.shared.MessageCopyOrMoveRequest;
import fr.aliasource.webmail.client.shared.SubmissionRequest;

public interface MyFactory extends AutoBeanFactory {

	AutoBean<IClientMessageList> clientMessageList();

	AutoBean<IClientMessage> clientMessage();

	AutoBean<IBody> body();

	AutoBean<IMessageId> messageId();

	AutoBean<IEmailAddress> emailAddress();

	AutoBean<IFolderList> folderList();

	AutoBean<IFolder> folder();

	AutoBean<SubmissionRequest> clientMessageSend();

	AutoBean<ICreateFolderRequest> createFolder();

	AutoBean<IAttachmentMetadata> getAttachmentMetadata();

	AutoBean<IAttachmentMetadataList> getAttachmentMetadataList();

	AutoBean<IDeleteMessagesRequest> deleteMessageRequest();

	AutoBean<MessageCopyOrMoveRequest> messagesCopyOrMoveRequest();

	AutoBean<Id> id();
}
