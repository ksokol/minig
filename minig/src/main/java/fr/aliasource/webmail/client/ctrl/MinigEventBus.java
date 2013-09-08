package fr.aliasource.webmail.client.ctrl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class MinigEventBus {

	private static final EventBus eventBus = new SimpleEventBus();

	public static EventBus getEventBus() {
		return eventBus;
	}
}
