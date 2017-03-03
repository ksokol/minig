package org.minig.server.converter;

import org.minig.server.MailAttachment;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Kamill Sokol
 */
public class Mime4jAttachmentToMailAttachmentConverter implements Converter<Mime4jAttachment, MailAttachment> {

    @Override
    public MailAttachment convert(Mime4jAttachment source) {
        MailAttachment target = new MailAttachment();

        target.setCompositeAttachmentId(source.getId());
        target.setMime(source.getMimeType());
        target.setFileName(source.getFilename());

        return target;
    }
}
