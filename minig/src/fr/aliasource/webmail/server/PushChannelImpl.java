package fr.aliasource.webmail.server;

import fr.aliasource.webmail.client.rpc.PushChannel;
import fr.aliasource.webmail.client.shared.ServerEventKind;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class PushChannelImpl extends SecureAjaxCall implements PushChannel {

	private static final long serialVersionUID = -7810229374277789013L;

	@Override
	public ServerEventKind fetchServerEvent() {
		IAccount ac = getAccount();
		return ac.fetchEvent();
	}

}
