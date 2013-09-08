package fr.aliasource.webmail.client.conversations;

import com.google.gwt.event.shared.EventHandler;

public interface StarredChangedEventHandler extends EventHandler {

	void onMessageReceived(StarredChangedEvent event);

}
