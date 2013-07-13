package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.chat.History;

public interface ChatServiceAsync {

	void storeHistory(History chatHistory, AsyncCallback<Void> callback);

}
