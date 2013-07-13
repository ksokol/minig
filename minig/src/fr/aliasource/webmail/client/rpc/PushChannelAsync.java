package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.ServerEventKind;

public interface PushChannelAsync {

	void fetchServerEvent(AsyncCallback<ServerEventKind> callback);

}
