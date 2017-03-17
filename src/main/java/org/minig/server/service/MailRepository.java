package org.minig.server.service;

import com.sun.mail.imap.IMAPFolder;
import org.minig.server.MailMessage;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.helper.MessageMapper;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Kamill Sokol
 */
@Component
public class MailRepository {

    private static final Logger log = LoggerFactory.getLogger(MailRepository.class);

    @Autowired
    private MailContext mailContext;

    @Autowired
    private MessageMapper mapper;

    public Page<MimeMessage> findByFolderOrderByDateDesc(String folder, Pageable pageable) {
        Objects.requireNonNull(folder, "folder is null");
        Objects.requireNonNull(pageable, "pageable is null");

        try {
            List<MimeMessage> mimeMessages = new ArrayList<>();
            Folder storeFolder = mailContext.getFolder(folder);
            int messageCount = storeFolder.getMessageCount();

            if (messageCount == 0) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }

            int end = Math.max(messageCount - pageable.getPageNumber() * pageable.getPageSize(), 0);
            int start = Math.max(end - pageable.getPageSize() + 1, 1);

            if (end < 0) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }

            Message[] messages = storeFolder.getMessages(start, end);
            storeFolder.fetch(messages, fullMailProfile());

            for (Message m : messages) {
                mimeMessages.add((MimeMessage) m);
            }

            Collections.reverse(mimeMessages);

            return new PageImpl<>(mimeMessages, pageable, messageCount);
        } catch (Exception exception) {
            throw new RepositoryException(exception.getMessage(), exception);
        }
    }

    public MailMessage read(CompositeId id) {
        Assert.notNull(id);

        Folder storeFolder = mailContext.openFolder(id.getFolder());

        try {
            if (!storeFolder.exists()) {
                return null;
            }

            Message[] search = storeFolder.search(new MessageIDTerm(id.getMessageId()));
            storeFolder.fetch(search, fullMailProfile());

            if (search.length > 0) {
                return mapper.convertFull(search[0]);
            }

        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        return null;
    }

    public Mime4jMessage read(String folder, String messageId) {

        try {
            Folder storeFolder = mailContext.openFolder(folder);
            Message[] search = storeFolder.search(new MessageIDTerm(messageId));

            if (search != null && search.length == 1 && search[0] != null) {
                return new Mime4jMessage(search[0]);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new NotFoundException();
    }

    public CompositeId findByMessageId(String messageId) {
        return findByMessageIdAndFolder(new MessageIDTerm(messageId), mailContext.getInbox());
    }

    public MailMessage readPojo(String folder, String messageId) {

        try {
            Folder storeFolder = mailContext.getFolder(folder);

            if (storeFolder.exists()) {
                Message[] search = storeFolder.search(new MessageIDTerm(messageId));

                if (search != null && search.length == 1 && search[0] != null) {

                    return mapper.convertFull(search[0]);
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new NotFoundException();
    }

    public void updateFlags(MailMessage message) {
        Assert.notNull(message, "message is null");

        try {
            Folder folder = mailContext.getFolder(message.getFolder());
            Message[] search = folder.search(new MessageIDTerm(message.getMessageId()));

            if (search != null) {
                for (Message m : search) {
                    m.setFlag(Flags.Flag.SEEN, message.getRead());
                    m.setFlag(Flags.Flag.FLAGGED, message.getStarred());
                    m.setFlag(Flags.Flag.ANSWERED, message.getAnswered());

                    // TODO
                    if (message.getForwarded()) {
                        Flags forwardedFlag = new Flags("$Forwarded");
                        m.setFlags(forwardedFlag, true);
                    }

                    if (message.getMdnSent()) {
                        Flags mdnSentFlag = new Flags("$MDNSent");
                        m.setFlags(mdnSentFlag, true);
                    }
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void moveMessage(CompositeId message, String folder) {
        Assert.notNull(message);
        Assert.hasText(folder);

        try {
            if (!folder.equals(message.getFolder())) {
                Folder targetFolder = mailContext.getFolder(folder);
                Folder sourceFolder = mailContext.getFolder(message.getFolder());

                Message[] search = sourceFolder.search(new MessageIDTerm(message.getMessageId()));
                sourceFolder.copyMessages(search, targetFolder);

                if (search != null) {
                    for (Message m : search) {
                        m.setFlag(Flags.Flag.DELETED, true);
                    }
                }

                // expunge
                sourceFolder.close(true);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void delete(CompositeId id) {
        try {
            Folder storeFolder = mailContext.getFolder(id.getFolder());
            Message[] search = storeFolder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null) {
                for (Message msg : search) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                }
            }

            // expunge
            storeFolder.close(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void delete(String folder, String messageId) {
        delete(new CompositeId(folder, messageId));
    }

    public String save(Mime4jMessage message, String folder) {
        Assert.notNull(message, "message is null");
        Assert.hasText(folder, "folder is null");

        try {
            Message target = message.toMessage();
            target.saveChanges();
            Folder storeFolder = mailContext.openFolder(folder);
            storeFolder.appendMessages(new Message[] { target });
            storeFolder.close(false);

            return target.getHeader("Message-ID")[0];
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void copyMessages(String source, String target) {
        Assert.hasText(source);
        Assert.hasText(target);

        try {
            Folder sourceFolder = mailContext.getFolder(source);
            Folder targetFolder = mailContext.getFolder(target);

            if (sourceFolder.exists() && targetFolder.exists()) {
                sourceFolder.copyMessages(sourceFolder.getMessages(), targetFolder);
            }
        } catch (MessagingException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void copyMessage(CompositeId id, String target) {
        Assert.notNull(id);
        Assert.hasText(target);

        try {
            Folder sourceFolder = mailContext.getFolder(id.getFolder());
            Message[] search = sourceFolder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null) {
                Folder targetFolder = mailContext.getFolder(target);

                if (targetFolder.exists()) {
                    sourceFolder.copyMessages(search, targetFolder);
                }
            }
        } catch (MessagingException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void setAnsweredFlag(CompositeId id, boolean answered) {
        if(id == null) {
            return;
        }

        try {
            log.debug("setting flagAsAnswered to {} on message {}", answered, id);
            Folder folder = mailContext.openFolder(id.getFolder());
            Message[] messages = folder.search(new MessageIDTerm(id.getMessageId()));

            for (Message m : messages) {
                m.setFlag(Flags.Flag.ANSWERED, answered);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public void setForwardedFlag(CompositeId id, boolean answered) {
        if(id == null) {
            return;
        }

        try {
            log.debug("setting flagAsForwarded to {} on message {}", answered, id);
            Folder folder = mailContext.openFolder(id.getFolder());
            Message[] messages = folder.search(new MessageIDTerm(id.getMessageId()));

            for (Message m : messages) {
                Flags forwardedFlag = new Flags("$Forwarded");
                m.setFlags(forwardedFlag, true);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    public String save(Mime4jMessage message) {
        Assert.notNull(message, "message is null");
        return save(message, message.getId().getFolder());
    }

    private CompositeId findByMessageIdAndFolder(MessageIDTerm searchTerm, Folder folder) {
        CompositeId compositeId = null;
        try {
            if(!folder.exists()) {
                log.debug("{} folder does not exist", folder.getFullName());
                return compositeId;
            }

            if(!mailContext.isSystemFolder(folder)) {
                log.debug("{} folder is system folder", folder.getFullName());
                return compositeId;
            }

            Folder[] list = folder.list();
            for (Folder childFolder : list) {
                compositeId = findByMessageIdAndFolder(searchTerm, childFolder);

                if(compositeId != null) {
                    log.debug("found {} in folder {}", searchTerm.getPattern(), folder.getFullName());
                    return compositeId;
                }
            }

            if(folder.getType() == Folder.HOLDS_FOLDERS) {
                log.debug("{} can not contain messages. type {}", folder.getFullName(), folder.getType());
                return compositeId;
            }

            if(!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }

            Message[] search = folder.search(searchTerm);

            for (Message message : search) {
                compositeId = new CompositeId(folder.getFullName(), message.getHeader("Message-ID")[0]);
            }

            if(search.length == 0) {
                folder.close(false);
            }
        } catch (MessagingException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
        return compositeId;
    }

    private static FetchProfile fullMailProfile() {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        fp.add(IMAPFolder.FetchProfileItem.MESSAGE);
        fp.add("X-Mozilla-Draft-Info");
        fp.add("$MDNSent");
        fp.add("$Forwarded");
        fp.add("X-PRIORITY");
        fp.add("Message-ID");
        fp.add("Disposition-Notification-To");
        fp.add("In-Reply-To");
        fp.add("X-Forwarded-Message-Id");
        fp.add("User-Agent");
        return fp;
    }
}
