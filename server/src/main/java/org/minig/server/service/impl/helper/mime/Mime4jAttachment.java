package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.springframework.util.Assert;

import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
public final class Mime4jAttachment {
	private final CompositeAttachmentId id;
	private final Mime4jAttachmentData metadata;

	public Mime4jAttachment(CompositeId compositeId, Mime4jAttachmentData metadata) {
		Assert.notNull(compositeId);
		Assert.notNull(metadata);
		this.id = new CompositeAttachmentId(compositeId.getFolder(), compositeId.getMessageId(), metadata.getFilename());
		this.metadata = metadata;
	}

	public CompositeAttachmentId getId() {
		return id;
	}

	public String getMimeType() {
		return metadata.getMimeType();
	}

	public InputStream getData() {
		return metadata.getData();
	}
}
