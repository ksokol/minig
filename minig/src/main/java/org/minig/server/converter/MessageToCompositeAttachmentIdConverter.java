package org.minig.server.converter;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.minig.server.service.CompositeAttachmentId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamill Sokol
 */
@Component
public class MessageToCompositeAttachmentIdConverter implements Converter<Message, List<CompositeAttachmentId>> {

    @Override
    public List<CompositeAttachmentId> convert(Message source) {
        return convertInternal(source);
    }

    private List<CompositeAttachmentId> convertInternal(Message source) {
        try {
            List<String> attachmentIdList = getAttachmentIds(source);
            List<CompositeAttachmentId> idList = new ArrayList<>();

            for (String attachmentId : attachmentIdList) {
                CompositeAttachmentId id = new CompositeAttachmentId();

                id.setMessageId(source.getHeader("Message-ID")[0]);
                id.setFolder(source.getFolder().getFullName());
                id.setFileName(attachmentId);

                idList.add(id);
            }

            return idList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static List<String> getAttachmentIds(Part p) throws MessagingException, IOException {
        List<String> names = new ArrayList<>();

        boolean isAttachment = p.getDisposition() != null && p.getDisposition().equalsIgnoreCase(Part.ATTACHMENT);
        boolean hasFileName = p.getFileName() != null;

        if (isAttachment && hasFileName) {
            String fileName = DecoderUtil.decodeEncodedWords(p.getFileName(), null);
            names.add(fileName);
        }

        if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                List<String> s = getAttachmentIds(mp.getBodyPart(i));
                names.addAll(s);
            }
        }

        if(p.isMimeType("message/rfc822")) {
            MimeMessage mp = (MimeMessage) p.getContent();
            names.addAll(extractFileName(mp));
        }

        return names;
    }

    private static List<String> extractFileName(Message message) throws MessagingException {
        if(message.isMimeType("text/plain") && message.getSubject() != null) {
            return Arrays.asList(DecoderUtil.decodeEncodedWords(String.format("%s.eml", message.getSubject()), null));
        }
        return Collections.emptyList();
    }
}
