package fr.aliasource.webmail.client.ctrl;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HeartbeatTimer extends Timer implements AsyncCallback<Boolean> {

	@Override
	public void run() {
		AjaxCall.heartbeat.isSessionAlive(this);
	}

	@Override
	public void onFailure(Throwable caught) {
		tartiflette();
	}

	@Override
	public void onSuccess(Boolean result) {
		if (!result) {
			tartiflette();
		}
	}

	private void tartiflette() {
		cancel();
		WebmailController.get().tartiflette();
	}
}
