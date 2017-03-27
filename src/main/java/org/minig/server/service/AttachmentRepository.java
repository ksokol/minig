package org.minig.server.service;

import org.minig.server.MailAttachment;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.MessageIDTerm;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AttachmentRepository {

    private final MailContext mailContext;

    public AttachmentRepository(MailContext mailContext) {
        this.mailContext = mailContext;
    }

    public List<MailAttachment> readMetadata(CompositeId id) {
        Assert.notNull(id);
        Mime4jMessage mime4jMessage = readInternal(id);

        if (mime4jMessage == null) {
            return Collections.emptyList();
        }

        List<MailAttachment> metaDataList = new ArrayList<>();
        List<Mime4jAttachment> mime4jAttachments = mime4jMessage.getAttachments();

        for (Mime4jAttachment attachment : mime4jAttachments) {
            MailAttachment metaData = new MailAttachment(attachment.getId(), attachment.getMimeType(), attachment.getContentId(), attachment.getDispositionType(), null);
            metaDataList.add(metaData);
        }

        return metaDataList;
    }

    public MailAttachment read(CompositeAttachmentId id) {
        Assert.notNull(id);

        try {
            Folder folder = mailContext.openFolder(id.getFolder());
            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));
            if (search.length == 0) {
                return null;
            }
            Mime4jAttachment attachment = new Mime4jMessage(search[0]).getAttachment(id.getFileName());
            MailAttachment mailAttachment = null;

            if(attachment != null) {
                mailAttachment = new MailAttachment(attachment.getId(), attachment.getMimeType(), attachment.getContentId(), attachment.getDispositionType(), null);
            }
            return mailAttachment;
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public InputStream readAttachmentPayload(CompositeAttachmentId id) {
        Assert.notNull(id);

        try {
            Folder folder = mailContext.getFolder(id.getFolder());
            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null && search[0] != null) {
                Mime4jAttachment attachment = new Mime4jMessage(search[0]).getAttachment(id.getFileName());

                if (attachment != null) {
                    return attachment.getData();
                }
            }

        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new NotFoundException();
    }

    public CompositeId appendAttachment(CompositeId compositeId, DataSource dataSource) {
        Assert.notNull(compositeId);
        Assert.notNull(dataSource);

        try {
            Folder mailFolder = mailContext.getFolder(compositeId.getFolder());
            Message[] messages = mailFolder.search(new MessageIDTerm(compositeId.getMessageId()));

            if (messages == null || messages.length != 1) {
                throw new RepositoryException(String.format("no message or no unique message found for %s", compositeId.getMessageId()));
            }

            Mime4jMessage mime4jMessage = new Mime4jMessage(messages[0]);
            mime4jMessage.addAttachment(dataSource);
            Message mimeMessage = mime4jMessage.toMessage();

            mimeMessage.saveChanges();

            mailFolder.appendMessages(new Message[]{mimeMessage});
            mailFolder.close(false);

            return new CompositeId(compositeId.getFolder(), mimeMessage.getHeader("Message-ID")[0]);
        } catch (MessagingException exception) {
            throw new RepositoryException(exception.getMessage(), exception);
        }
    }

    public List<Mime4jAttachment> read(CompositeId id) {
        Assert.notNull(id);
        Mime4jMessage mime4jMessage = readInternal(id);
        if (mime4jMessage == null) {
            return Collections.emptyList();
        }
        return mime4jMessage.getAttachments();
    }

    private Mime4jMessage readInternal(CompositeId id) {
        Assert.notNull(id);

        try {
            Folder folder = mailContext.getFolder(id.getFolder());
            if (!folder.exists()) {
                return null;
            }

            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));
            if (search != null && search.length > 0 && search[0] != null) {
                return new Mime4jMessage(search[0]);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
        return null;
    }
}
