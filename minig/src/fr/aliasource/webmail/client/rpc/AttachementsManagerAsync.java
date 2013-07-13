package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.AttachmentList;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.Folder;

public interface AttachementsManagerAsync {

	void allocateAttachementId(AsyncCallback<String> callback);

	void dropAttachement(String[] attachementId, AsyncCallback<Void> callback);

	void getAttachementMetadata(String[] attachementId,
			AsyncCallback<AttachmentMetadata[]> callback);

	void list(Folder f, int page, int pageLength,
			AsyncCallback<AttachmentList> callback);

}
