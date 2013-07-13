package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HeartbeatAsync {

	void isSessionAlive(AsyncCallback<Boolean> callback);

}
