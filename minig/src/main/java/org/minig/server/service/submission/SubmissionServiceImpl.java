package org.minig.server.service.submission;

import org.minig.server.MailMessage;
import org.minig.server.service.FolderRepository;
import org.minig.server.service.MailService;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Kamill Sokol
 */
@Component
class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private MailService mailService;

    @Autowired
    private Submission submission;

    @Autowired
    private FolderRepository folderRepository;

    @Override
    public void sendMessage(MailMessage message) {
        Assert.notNull(message);

        Mime4jMessage mime4jMessage = null;

        if (message.getId() == null) {
            MailMessage mm = mailService.createDraftMessage(message);
            mime4jMessage = mailService.findById(mm);
        } else if (message.getId().startsWith(folderRepository.getDraft().getId())) {
            MailMessage mm = mailService.updateDraftMessage(message);
            mime4jMessage = mailService.findById(mm);
        }

        submission.submit(mime4jMessage);

        mailService.moveMessageToFolder(mime4jMessage.getId(), folderRepository.getSent().getId());

        if(message.getInReplyTo() != null) {
            mailService.flagAsAnswered(message.getInReplyTo());
        }
        if(message.getForwardedMessageId() != null) {
            mailService.flagAsForwarded(message.getForwardedMessageId());
        }
    }
}
