package fr.aliasource.webmail.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("heartbeat")
public interface Heartbeat extends RemoteService {

	/**
	 * @return true if the session is still active on the tomcat side
	 */
	boolean isSessionAlive();

}
