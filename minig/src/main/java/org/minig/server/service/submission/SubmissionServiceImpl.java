package org.minig.server.service.submission;

import org.minig.server.MailMessage;
import org.minig.server.service.CompositeId;
import org.minig.server.service.FolderRepository;
import org.minig.server.service.MailService;
import org.minig.server.service.impl.helper.MessageMapper;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.mail.internet.MimeMessage;

@Component
class SubmissionServiceImpl implements SubmissionService {

    // private static final Logger logger =
    // Logger.getLogger(SubmissionServiceImpl.class.getName());

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private Submission submission;

    @Autowired
    private FolderRepository folderRepository;

    @Override
    public void sendMessage(MailMessage message) {
        sendMessage(message, null);
    }

    @Override
    public void sendMessage(MailMessage message, CompositeId replyTo) {
        Assert.notNull(message);

        Mime4jMessage mime4jMessage = null;

        if (message.getId() == null) {
            MailMessage mm = mailService.createDraftMessage(message);
            mime4jMessage = mailService.findById(mm);
        } else if (message.getId().startsWith(folderRepository.getDraft().getId())) {
            MailMessage mm = mailService.updateDraftMessage(message);
            mime4jMessage = mailService.findById(mm);
        }

        if (mime4jMessage.isDispositionNotification()) {
            submission.submitWithDSN(mime4jMessage);
        } else {
            submission.submit(mime4jMessage);
        }

        mailService.moveMessageToFolder(mime4jMessage.getId(), folderRepository.getSent().getId());

        if (replyTo != null && replyTo.getId() != null) {
            MailMessage updateFlag = mailService.findMessage(replyTo);
            updateFlag.setAnswered(true);
            mailService.updateMessageFlags(updateFlag);
        }
    }

    @Override
    public void forwardMessage(MailMessage message, CompositeId forwardedMessage) {
        Assert.notNull(forwardedMessage);
        Assert.notNull(message);

        MailMessage mm;
        MailMessage findMessage;
        Mime4jMessage mime4jMessage = null;

        if (message.getId() == null) {
            mm = mailService.createDraftMessage(message);
            findMessage = mailService.findMessage(mm);
            mime4jMessage = messageMapper.toMime4jMessage(findMessage);
        } else if (message.getId().startsWith(folderRepository.getDraft().getId())) {
            mm = mailService.updateDraftMessage(message);
            findMessage = mailService.findMessage(mm);
            mime4jMessage = messageMapper.toMime4jMessage(findMessage);
        }

        submission.submit(mime4jMessage);

        mailService.moveMessageToFolder(mime4jMessage.getId(), folderRepository.getSent().getId());

        if (forwardedMessage.getId() != null) {
            // mime4jMessage = messageMapper.toMime4jMessage(message);
            MailMessage updateFlag = mailService.findMessage(forwardedMessage);
            updateFlag.setForwarded(true);
            mailService.updateMessageFlags(updateFlag);
        }
    }
}
