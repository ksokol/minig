package org.minig.server.service;

import java.io.InputStream;

import javax.activation.DataSource;

import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;

public interface AttachmentRepository {

	public MailAttachmentList readMetadata(CompositeId message);

	public MailAttachment read(CompositeAttachmentId attachmentId);

	public InputStream readAttachmentPayload(CompositeAttachmentId attachmentId);

	CompositeId appendAttachment(CompositeId id, DataSource dataSource);
}