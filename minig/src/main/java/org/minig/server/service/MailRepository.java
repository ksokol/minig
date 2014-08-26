package org.minig.server.service;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;

import java.util.List;

/**
 * @author Kamill Sokol
 */
public interface MailRepository {

    MailMessageList findByFolder(String folder, int page, int pageLength);

    @Deprecated
    MailMessage read(CompositeId id);

    Mime4jMessage read(String folder, String messageId);

    List<CompositeId> findByMessageId(String messageId);

    @Deprecated
    MailMessage readPojo(String folder, String messageId);

    void delete(CompositeId id);

    String save(Mime4jMessage message, String folder);

    void updateFlags(MailMessage message);

    void moveMessage(CompositeId message, String folder);

    void copyMessages(String source, String target);

    void copyMessage(CompositeId id, String target);

    void setAnsweredFlag(CompositeId id, boolean answered);

    void setForwardedFlag(CompositeId id, boolean answered);
}
