package org.minig.server.service.impl;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.RepositoryException;
import org.minig.server.service.impl.helper.MessageMapper;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author Kamill Sokol
 */
@Component
class MailRepositoryImpl implements MailRepository {

    private static final Logger log = LoggerFactory.getLogger(MailRepositoryImpl.class);

    @Autowired
    private MailContext mailContext;

    @Autowired
    private MessageMapper mapper;

    @Override
    public MailMessageList findByFolder(String folder, int page, int pageLength) {
        Assert.notNull(folder, "folder is null");

        if (page < 1 || pageLength < 1) {
            return new MailMessageList();
        }

        try {
            List<MailMessage> messageList = new ArrayList<MailMessage>();
            Folder storeFolder = mailContext.openFolder(folder);
            int messageCount = storeFolder.getMessageCount();

            if (messageCount == 0) {
                return new MailMessageList();
            }

            int end = messageCount - (page - 1) * pageLength;
            int start = Math.max(end - pageLength + 1, 1);

            if (end < 0) {
                return new MailMessageList();
            }

            Message[] messages = storeFolder.getMessages(start, end);

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add("X-Mozilla-Draft-Info");
            fp.add("$MDNSent");
            fp.add("$Forwarded");
            fp.add("X-PRIORITY");

            storeFolder.fetch(messages, fp);

            for (Message m : messages) {
                MailMessage message = mapper.convertShort(m);
                messageList.add(message);
            }

            Collections.reverse(messageList);
            return new MailMessageList(messageList, page, messageCount);
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public MailMessage read(CompositeId id) {
        Assert.notNull(id);

        try {
            Folder storeFolder = mailContext.getFolder(id.getFolder(), true);

            if (storeFolder.exists()) {
                Message[] search = storeFolder.search(new MessageIDTerm(id.getMessageId()));
                if (search.length > 0) {
                    return mapper.convertFull(search[0]);
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Mime4jMessage read(String folder, String messageId) {

        try {
            Folder storeFolder = mailContext.openFolder(folder);
            Message[] search = storeFolder.search(new MessageIDTerm(messageId));

            if (search != null && search.length == 1 && search[0] != null) {
                Mime4jMessage mime4jMessage = mapper.toMessageImpl(search[0]);
                mime4jMessage.setId(new CompositeId(folder, messageId));
                return mime4jMessage;
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new NotFoundException();
    }

    @Override
    public List<CompositeId> findByMessageId(String messageId) {
        return findByMessageIdAndFolder(new MessageIDTerm(messageId), mailContext.getInbox());
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public String save(Mime4jMessage message, String folder) {
        Assert.notNull(message, "message is null");
        Assert.hasText(folder, "folder is null");

        try {
            MimeMessage target = mapper.toMimeMessage(message);
            target.saveChanges();
            Folder storeFolder = mailContext.openFolder(folder);
            storeFolder.appendMessages(new Message[] { target });
            storeFolder.close(false);

            return target.getHeader("Message-ID")[0];
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void setAnsweredFlag(CompositeId id, boolean answered) {
        if(id == null) {
            return;
        }

        try {
            log.info("setting flagAsAnswered to {} on message {}", answered, id);
            Folder folder = mailContext.openFolder(id.getFolder());
            Message[] messages = folder.search(new MessageIDTerm(id.getMessageId()));

            for (Message m : messages) {
                m.setFlag(Flags.Flag.ANSWERED, answered);
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private List<CompositeId> findByMessageIdAndFolder(MessageIDTerm searchTerm, Folder folder) {
        List<CompositeId> messages = new ArrayList<>();
        try {
            if(!folder.exists()) {
                log.info("{} folder does not exist", folder.getFullName());
                return messages;
            }

            if(!mailContext.isSystemFolder(folder)) {
                log.info("{} folder is system folder", folder.getFullName());
                return messages;
            }

            Folder[] list = folder.list();
            for (Folder childFolder : list) {
                messages.addAll(findByMessageIdAndFolder(searchTerm, childFolder));
            }

            if(folder.getType() == Folder.HOLDS_FOLDERS) {
                log.info("{} can not contain messages. type {}", folder.getFullName(), folder.getType());
                return messages;
            }

            if(!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }

            Message[] search = folder.search(searchTerm);

            for (Message message : search) {
                messages.add(new CompositeId(folder.getFullName(), message.getHeader("Message-ID")[0]));
            }

            if(search.length == 0) {
                folder.close(false);
            }
        } catch (MessagingException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
        return messages;
    }
}
