package fr.aliasource.webmail.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.aliasource.webmail.client.rpc.ChatService;
import fr.aliasource.webmail.client.shared.chat.History;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class ChatServiceImpl extends SecureAjaxCall implements ChatService {

	private static final long serialVersionUID = -4600073926075259800L;

	private static final Log logger = LogFactory.getLog(ChatService.class);

	@Override
	public void storeHistory(History chatHistory) {
		logger.info("Storing history !!!!");
		IAccount ac = getAccount();
		if (ac != null) {
			ac.storeHistory(chatHistory);
		}
	}

}
