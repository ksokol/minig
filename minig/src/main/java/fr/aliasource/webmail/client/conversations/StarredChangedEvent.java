package fr.aliasource.webmail.client.conversations;

import java.util.Collections;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

public class StarredChangedEvent extends GwtEvent<StarredChangedEventHandler> {

	public static Type<StarredChangedEventHandler> TYPE = new Type<StarredChangedEventHandler>();

	private boolean starred;
	private Set<String> ids;

	@SuppressWarnings("unchecked")
	public StarredChangedEvent(Set<String> ids, boolean starred) {
		this.ids = (ids == null) ? Collections.EMPTY_SET : ids;
		this.starred = starred;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StarredChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(StarredChangedEventHandler handler) {
		handler.onMessageReceived(this);

	}

	public boolean isStarred() {
		return starred;
	}

	public Set<String> getIds() {
		return ids;
	}

}
