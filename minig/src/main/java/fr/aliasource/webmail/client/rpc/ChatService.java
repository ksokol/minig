package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.chat.History;

@RemoteServiceRelativePath("chatService")
public interface ChatService extends RemoteService {

	void storeHistory(History chatHistory);

}
