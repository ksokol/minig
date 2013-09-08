package fr.aliasource.webmail.client.ctrl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.shared.ServerEventKind;

public class PushTimer extends Timer implements AsyncCallback<ServerEventKind> {

	private boolean stopped;

	public PushTimer() {
		this.stopped = false;
	}

	@Override
	public void run() {
		AjaxCall.push.fetchServerEvent(this);
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("failure", caught);
	}

	@Override
	public void onSuccess(ServerEventKind result) {
		GWT.log("server event: " + result);
		if (!stopped) {
			schedule(1);
		}
	}

}
