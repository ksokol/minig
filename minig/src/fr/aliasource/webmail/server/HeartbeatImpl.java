package fr.aliasource.webmail.server;

import fr.aliasource.webmail.client.rpc.Heartbeat;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class HeartbeatImpl extends SecureAjaxCall implements Heartbeat {

	private static final long serialVersionUID = 2901787351621042484L;

	@Override
	public boolean isSessionAlive() {
		IAccount ac = getAccount();
		return ac != null;
	}

}
