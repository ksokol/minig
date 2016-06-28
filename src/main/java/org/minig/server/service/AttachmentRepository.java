package org.minig.server.service;

import java.io.InputStream;
import java.util.List;

import javax.activation.DataSource;

import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;

public interface AttachmentRepository {

	@Deprecated
	MailAttachmentList readMetadata(CompositeId id);

	MailAttachment read(CompositeAttachmentId attachmentId);

	InputStream readAttachmentPayload(CompositeAttachmentId attachmentId);

	CompositeId appendAttachment(CompositeId id, DataSource dataSource);

	List<Mime4jAttachment> read(CompositeId id);
}