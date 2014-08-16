package org.minig.server.service;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Kamill Sokol
 */
public class CompositeId {

	public static final String SEPARATOR = "|";

	protected String id;
	private String messageId;
	private String folder;

	public CompositeId() {
	}

	public CompositeId(String id) {
		setId(id);
	}

	public CompositeId(String folder, String messageId) {
		this.folder = folder;
		this.messageId = messageId;
	}

	public String getId() {
		buildId();
		return id;
	}

    @JsonIgnore
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		if (messageId != null) {
			this.messageId = messageId;
			// buildId();
		}
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		if (folder != null) {
			this.folder = folder;
			// buildId();
		}
	}

	public void setId(String id) {
		if (id != null && folder == null && messageId == null) {
			String[] split = id.split("\\" + SEPARATOR);

			if (split != null && split.length > 1) {
				messageId = split[1];
				folder = split[0];
				buildId();
			}
		}
	}

	public void setCompositeId(CompositeId id) {
		setId(id.getId());
	}

	protected void buildId() {
		if (id == null && folder != null && messageId != null) {
			id = folder + SEPARATOR + messageId;
		}
	}
}
