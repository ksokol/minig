package org.minig.server.service.submission;

import org.minig.server.MailMessage;
import org.minig.server.service.CompositeId;

public interface SubmissionService {

    void sendMessage(MailMessage message);

    void sendMessage(MailMessage message, CompositeId replyTo);

    void forwardMessage(MailMessage message, CompositeId forwardedMessage);

}
