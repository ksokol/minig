package org.minig.server.service.submission;

import org.minig.server.MailMessage;
import org.minig.server.service.CompositeId;

/**
 * @author Kamill Sokol
 */
public interface SubmissionService {

    void sendMessage(MailMessage message);

}
