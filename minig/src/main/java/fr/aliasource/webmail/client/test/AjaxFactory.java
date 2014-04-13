package fr.aliasource.webmail.client.test;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.URL;

import fr.aliasource.webmail.client.shared.*;

public class AjaxFactory {

    public static final String JSON_URL = "api/1";

    public static Ajax<IClientMessageList> fetchMessages(String folder, final int pageLength, final int page) {
        String url = JSON_URL + "/message?page_length=" + pageLength + "&page=" + page + "&folder=" + folder;
        return new Ajax<IClientMessageList>(RequestBuilder.GET, url, IClientMessageList.class);
    }

    public static Ajax<IClientMessage> fetchMessage(String id) {
        String url = JSON_URL + "/message/" + id;
        return new Ajax<IClientMessage>(RequestBuilder.GET, url, IClientMessage.class);
    }

    public static Ajax<IClientMessage> saveDraftMessage() {
        String url = JSON_URL + "/message/draft";
        return new Ajax<IClientMessage>(RequestBuilder.POST, url, IClientMessage.class);
    }

    public static Ajax<IClientMessage> updateDraftMessage(String id) {
        String url = JSON_URL + "/message/draft/" + id;
        return new Ajax<IClientMessage>(RequestBuilder.PUT, url, IClientMessage.class);
    }

    public static Ajax<IClientMessage> updateMessageFlags(String id) {
        String url = JSON_URL + "/message/flag/" + id;
        return new Ajax<IClientMessage>(RequestBuilder.PUT, url, IClientMessage.class);
    }

    public static Ajax<IClientMessageList> updateMessagesFlags() {
        String url = JSON_URL + "/message/flag";
        return new Ajax<IClientMessageList>(RequestBuilder.PUT, url, IClientMessageList.class);
    }

    public static Ajax<MessageCopyOrMoveRequest> moveMessages() {
        String url = JSON_URL + "/message/move";
        return new Ajax<MessageCopyOrMoveRequest>(RequestBuilder.PUT, url, MessageCopyOrMoveRequest.class);
    }

    public static Ajax<MessageCopyOrMoveRequest> copyMessages() {
        String url = JSON_URL + "/message/copy";
        return new Ajax<MessageCopyOrMoveRequest>(RequestBuilder.PUT, url, MessageCopyOrMoveRequest.class);
    }

    public static Ajax<IFolderList> subscribedFolder() {
        // TODO uri escape

        String url = JSON_URL + "/folder";

        Ajax<IFolderList> builder = new Ajax<IFolderList>(RequestBuilder.GET, url, IFolderList.class);

        return builder;
    }

    public static Ajax<IAttachmentMetadata> attachmentMetadata(String messageId, String attachmentId) {
        String url = JSON_URL + "/attachment/" + messageId + "|" + attachmentId;
        return new Ajax<IAttachmentMetadata>(RequestBuilder.GET, url, IAttachmentMetadata.class);
    }

    public static Ajax<IAttachmentMetadataList> attachmentMetadataList(String messageId) {
        // TODO uri escape

        String url = JSON_URL + "/attachment/" + messageId;

        Ajax<IAttachmentMetadataList> builder = new Ajax<IAttachmentMetadataList>(RequestBuilder.GET, url, IAttachmentMetadataList.class);

        return builder;
    }

    public static String attachmentDownloadLink(String attachmentId) {
        return AjaxFactory.JSON_URL + "/attachment/" + attachmentId + "?download=true";
    }

    public static Ajax<IDeleteMessagesRequest> deleteMessages() {
        String url = JSON_URL + "/message/delete";
        Ajax<IDeleteMessagesRequest> builder = new Ajax<IDeleteMessagesRequest>(RequestBuilder.PUT, url, IDeleteMessagesRequest.class);
        return builder;
    }

    public static Ajax<Void> deleteMessage(String id) {
        String url = JSON_URL + "/message/" + id;
        Ajax<Void> builder = new Ajax<Void>(RequestBuilder.DELETE, url, Void.class);
        return builder;
    }

    public static Ajax<IFolderList> subscribedFolder(boolean subscribed) {
        String url = JSON_URL + "/folder?subscribed=" + subscribed;
        Ajax<IFolderList> builder = new Ajax<IFolderList>(RequestBuilder.GET, url, IFolderList.class);
        return builder;
    }

    public static Ajax<SubmissionRequest> submissionRequest() {
        String url = JSON_URL + "/submission";
        Ajax<SubmissionRequest> builder = new Ajax<SubmissionRequest>(RequestBuilder.POST, url, SubmissionRequest.class);
        return builder;
    }

    public static Ajax<IClientMessage> forwardMessage(String id) {
        String url = JSON_URL + "/submission/forward/" + id;
        Ajax<IClientMessage> builder = new Ajax<IClientMessage>(RequestBuilder.POST, url, IClientMessage.class);
        return builder;
    }

    public static Ajax<Void> dispositionRequest(String id) {
        String url = JSON_URL + "/submission/disposition/" + id;
        Ajax<Void> builder = new Ajax<Void>(RequestBuilder.POST, url, Void.class);
        return builder;
    }

    public static Ajax<ICreateFolderRequest> createFolder(String parent) {
        String url = JSON_URL + "/folder";
        Method method = RequestBuilder.POST;

        if (parent != null) {
            url += "/" + URL.encode(parent);
        }

        Ajax<ICreateFolderRequest> builder = new Ajax<ICreateFolderRequest>(method, url, ICreateFolderRequest.class);

        return builder;
    }

    public static Ajax<IFolder> updateFolder(String id) {
        String url = JSON_URL + "/folder/" + URL.encode(id);
        return new Ajax<IFolder>(RequestBuilder.PUT, url, IFolder.class);

    }

    public static Ajax<IFolder> deleteFolder(String id) {
        String url = JSON_URL + "/folder/" + URL.encode(id);
        return new Ajax<IFolder>(RequestBuilder.DELETE, url, IFolder.class);
    }

    public static String uploadAttachment(String id) {
        return JSON_URL + "/attachment/" + id;
    }

    public static Ajax<Id> deleteAttachment(String messageId, String attachmentId) {
        String url = JSON_URL + "/attachment/" + messageId + "|" + attachmentId;
        return new Ajax<Id>(RequestBuilder.DELETE, url, Id.class);
    }
}
