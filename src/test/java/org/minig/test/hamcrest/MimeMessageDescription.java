package org.minig.test.hamcrest;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamill Sokol
 */
final class MimeMessageDescription extends StringDescription {

    @Override
    public Description appendValue(Object value) {
        if(value instanceof MimePart) {
            MimePart part = (MimePart) value;

            try {
                if(part.getContent() instanceof Multipart) {
                    return super.appendValue(collectMimeTypes((Multipart) part.getContent()));
                } else {
                    return super
                            .appendText("a body part with mime type ")
                            .appendValue(new ContentType(part.getContentType()).getBaseType())
                            .appendText(" and body ")
                            .appendValue(IOUtils.toString(part.getInputStream()));
                }
            } catch (IOException | MessagingException exception) {
                throw new RuntimeException(exception.getMessage(), exception);
            }
        }

        return super.appendValue(value);
    }

    private static List<String> collectMimeTypes(Multipart mimePart) throws MessagingException {
        List<String> bodyPartContentTypes = new ArrayList<>();

        for (int i = 0; i < mimePart.getCount(); i++) {
            BodyPart bodyPart = mimePart.getBodyPart(i);
            bodyPartContentTypes.add(new ContentType(bodyPart.getContentType()).getBaseType());
        }

        return bodyPartContentTypes;
    }
}
