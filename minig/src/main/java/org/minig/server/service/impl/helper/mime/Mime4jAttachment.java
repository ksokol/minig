package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.springframework.util.Assert;

/**
 * @author Kamill Sokol
 */
public final class Mime4jAttachment {
	private final CompositeAttachmentId id;
	private final Mime4jAttachmentMetadata metadata;

	public Mime4jAttachment(CompositeId compositeId, Mime4jAttachmentMetadata metadata) {
		Assert.notNull(compositeId);
		Assert.notNull(metadata);
		this.id = new CompositeAttachmentId(compositeId.getFolder(), compositeId.getMessageId(), metadata.getFilename());
		this.metadata = metadata;
	}

	public CompositeAttachmentId getId() {
		return id;
	}

	public long getSize() {
		return metadata.getSize();
	}

	public String getMimeType() {
		return metadata.getMimeType();
	}
}
