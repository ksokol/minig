package org.minig.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.RepositoryException;
import org.minig.server.service.impl.helper.MessageMapper;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class MailRepositoryImpl implements MailRepository {

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
        int count = 0;

        try {
            Folder storeFolder = mailContext.getFolder(id.getFolder(), true);

            if (storeFolder.exists()) {
                Message[] search = storeFolder.search(new MessageIDTerm(id.getMessageId()));
                count = search.length;

                if (search.length == 1 && search[0] != null) {
                    return mapper.convertFull(search[0]);
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        if (count > 1) {
            // TODO
            throw new RuntimeException("more than one message found");
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
    public MailMessage saveInFolder(MailMessage source, String folder) {
        Assert.notNull(source, "message is null");
        // Assert.notNull(source.getId(), "message.id is null");
        Assert.hasText(folder);

        try {
            Message target = mapper.toMessage(source);
            Folder storeFolder = mailContext.getFolder(folder, true);
            storeFolder.appendMessages(new Message[] { target });

            Message[] search = storeFolder.search(new MessageIDTerm(target.getHeader("Message-ID")[0]));

            if (search != null && search.length == 1 && search[0] != null) {
                MailMessage convertShort = mapper.convertShort(search[0]);
                return convertShort;
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new RepositoryException("error during save");
    }

    @Override
    public String save(Mime4jMessage message, String folder) {
        // Assert.notNull(source, "message is null");
        // Assert.notNull(source.getId(), "message.id is null");
        // Assert.hasText(folder);

        try {
            MimeMessage target = mapper.toMimeMessage(message);
            target.saveChanges();
            Folder storeFolder = mailContext.openFolder(folder);
            storeFolder.appendMessages(new Message[] { target });

            // Message[] search = storeFolder.search(new
            // MessageIDTerm(target.getHeader("Message-ID")[0]));
            //
            // if (search != null && search.length == 1 && search[0] != null) {
            // MailMessage convertShort = mapper.convertShort(search[0]);
            // return convertShort;
            // }

            return target.getHeader("Message-ID")[0];
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        // throw new RepositoryException("error during save");
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

}
