package org.minig.server.service;

import javax.mail.Folder;
import javax.mail.MessagingException;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

public class FolderBuilder {

	// defaults
	private String fullName = "fullName";
	private String name = "name";
	private String parentFullName = null;
	private boolean subscribed = false;

	public Folder mock() {
		try {
			Folder mock = org.mockito.Mockito.mock(Folder.class,
					RETURNS_DEEP_STUBS);

			when(mock.getFullName()).thenReturn(fullName);
			when(mock.getName()).thenReturn(name);
			when(mock.isSubscribed()).thenReturn(subscribed);

			if (parentFullName != null) {
				when(mock.getParent().getFullName()).thenReturn(parentFullName);
			} else {
				when(mock.getParent()).thenReturn(null);
			}

			return mock;
		} catch (MessagingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getFullName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parentFullName;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public FolderBuilder setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public FolderBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public FolderBuilder setParent(String parent) {
		this.parentFullName = parent;
		return this;
	}

	public FolderBuilder setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
		return this;
	}

}
