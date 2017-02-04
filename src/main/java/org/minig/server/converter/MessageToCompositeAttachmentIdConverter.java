package org.minig.server.converter;

import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.core.convert.converter.Converter;

import javax.mail.Message;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kamill Sokol
 */
public class MessageToCompositeAttachmentIdConverter implements Converter<Message, List<CompositeAttachmentId>> {

    @Override
    public List<CompositeAttachmentId> convert(Message source) {
        return convertInternal(source);
    }

    private List<CompositeAttachmentId> convertInternal(Message source) {
        return new Mime4jMessage(source).getAttachments().stream().map(Mime4jAttachment::getId).collect(Collectors.toList());
    }
}
