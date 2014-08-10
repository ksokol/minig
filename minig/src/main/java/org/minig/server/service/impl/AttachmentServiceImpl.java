package org.minig.server.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.io.IOUtils;
import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.MailMessage;
import org.minig.server.service.AttachmentRepository;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.server.service.FolderRepository;
import org.minig.server.service.MailRepository;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.ServiceException;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AttachmentServiceImpl implements AttachmentService {

	@Autowired
	private MailRepository mailRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@Autowired
	private FolderRepository folderRepository;

	@Override
	public MailAttachmentList findAttachments(CompositeId id) {
		Assert.notNull(id);

		MailMessage message = mailRepository.read(id);

		if (message != null) {
			return attachmentRepository.readMetadata(message);
		} else {
			return new MailAttachmentList();
		}
	}

	@Override
	public MailAttachment findAttachment(CompositeAttachmentId attachmentId) {
		Assert.notNull(attachmentId);

		MailAttachment attachment = attachmentRepository.read(attachmentId);

		if (attachment == null) {
			throw new NotFoundException();
		}

		return attachment;
	}

	@Override
	public void readAttachment(CompositeAttachmentId attachmentId, OutputStream output) {
		Assert.notNull(attachmentId);

		InputStream input = attachmentRepository.readAttachmentPayload(attachmentId);

		if (input == null) {
			throw new NotFoundException();
		} else {
			try {
				IOUtils.copy(input, output);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
	}

	@Override
	public CompositeId addAttachment(CompositeId attachmentId, DataSource dataSource) {
		Assert.notNull(attachmentId);
		Assert.notNull(dataSource);

		if (!attachmentId.getFolder().startsWith(folderRepository.getDraft().getId())) {
			throw new ServiceException("");
		}

		CompositeId appendAttachment = attachmentRepository.appendAttachment(attachmentId, dataSource);

        MailMessage oldMessage = mailRepository.read(attachmentId);
        MailMessage newMessage = mailRepository.read(appendAttachment);

        //TODO check if all flags are written back
        newMessage.setAnswered(oldMessage.getAnswered());
        newMessage.setAskForDispositionNotification(oldMessage.getAskForDispositionNotification());
        newMessage.setDeleted(oldMessage.getDeleted());
        newMessage.setDispositionNotification(oldMessage.getDispositionNotification());
        newMessage.setForwarded(oldMessage.getForwarded());
        newMessage.setHighPriority(oldMessage.getHighPriority());
        newMessage.setMdnSent(oldMessage.getMdnSent());
        newMessage.setRead(oldMessage.getRead());
        newMessage.setReceipt(oldMessage.getReceipt());
        newMessage.setStarred(oldMessage.getStarred());

        mailRepository.updateFlags(newMessage);
        mailRepository.delete(attachmentId);

		return appendAttachment;
	}

	@Override
	public CompositeId deleteAttachment(CompositeAttachmentId attachmentId) {
		Assert.notNull(attachmentId);

		if (!attachmentId.getFolder().startsWith(folderRepository.getDraft().getId())) {
			throw new ServiceException("");
		}

        Mime4jMessage message = mailRepository.read(attachmentId.getFolder(), attachmentId.getMessageId());

        message.deleteAttachment(attachmentId.getFileName());

        String id = mailRepository.save(message, message.getId().getFolder());

        MailMessage mm = mailRepository.readPojo(message.getId().getFolder(), id);
        mm.setRead(true);
        mailRepository.updateFlags(mm);

        mailRepository.delete(attachmentId);

        CompositeId newId = new CompositeId();
        newId.setFolder(attachmentId.getFolder());
        newId.setMessageId(id);

        return newId;
	}
}
