package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.ServerEventKind;

@RemoteServiceRelativePath("push")
public interface PushChannel extends RemoteService {

	ServerEventKind fetchServerEvent();
	
}
