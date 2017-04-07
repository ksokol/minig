package org.minig.server.service;

import org.minig.server.MailAttachment;
import org.minig.server.MailMessage;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
public class AttachmentService {

    private final MailRepository mailRepository;
    private final AttachmentRepository attachmentRepository;
    private final FolderRepository folderRepository;

    public AttachmentService(MailRepository mailRepository, AttachmentRepository attachmentRepository, FolderRepository folderRepository) {
        this.mailRepository = requireNonNull(mailRepository, "mailRepository is null");
        this.attachmentRepository = requireNonNull(attachmentRepository, "attachmentRepository is null");
        this.folderRepository = requireNonNull(folderRepository, "folderRepository is null");
    }

    public List<MailAttachment> findAttachments(CompositeId id) {
        Assert.notNull(id);

        MailMessage message = mailRepository.read(id);

        if (message != null) {
            return attachmentRepository.readMetadata(message);
        } else {
            return Collections.emptyList();
        }
    }

    public MailAttachment findById(CompositeAttachmentId id) {
        MimeMessage mimeMessage = mailRepository.findByCompositeId(id).orElseThrow(NotFoundException::new);
        return new Mime4jMessage(mimeMessage).getAttachment(id).map(MailAttachment::new).orElseThrow(NotFoundException::new);
    }

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
