package org.minig.server.service;

import java.io.OutputStream;

import javax.activation.DataSource;

import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;

public interface AttachmentService {

	MailAttachmentList findAttachments(CompositeId messageId);

	MailAttachment findAttachment(CompositeAttachmentId attachmentId);

	void readAttachment(CompositeAttachmentId attachmentId, OutputStream output);

	CompositeId addAttachment(CompositeId id, DataSource dataSource);

	CompositeId deleteAttachment(CompositeAttachmentId attachmentId);

}
