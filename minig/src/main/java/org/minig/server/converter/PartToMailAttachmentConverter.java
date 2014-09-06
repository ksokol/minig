package org.minig.server.converter;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.minig.server.MailAttachment;
import org.minig.server.service.CompositeAttachmentId;
import org.springframework.core.convert.converter.Converter;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 * @author Kamill Sokol
 */
public class PartToMailAttachmentConverter implements Converter<BodyPart, MailAttachment> {

    private static final String DEFAULT_MIMETYPE = "application/content-stream";
    private static final int ASCII_UPPER_A = 65;
    private static final int ASCII_UPPER_B = 90;
    private static final int ASCII_LOWER_A = 97;
    private static final int ASCII_LOWER_B = 122;
    private static final int ASCII_SLASH = 47;

    @Override
    public MailAttachment convert(BodyPart source) {
        try {
            return convertInternal(source);
        } catch (Exception e) {
           throw new RuntimeException(e.getMessage(), e);
        }
    }

    private MailAttachment convertInternal(BodyPart source) throws MessagingException {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId();

        String messageId = extractFromMessage(source, new MessageIdExtract());
        compositeAttachmentId.setMessageId(messageId);

        String folder = extractFromMessage(source, new FolderExtract());
        compositeAttachmentId.setFolder(folder);

        if(source.getFileName() != null) {
            String decodedFileName = DecoderUtil.decodeEncodedWords(source.getFileName(), null);
            compositeAttachmentId.setFileName(decodedFileName);
        }

        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setCompositeId(compositeAttachmentId);
        mailAttachment.setMime(parseContentType(source.getContentType()));
        return mailAttachment;
    }

    private static String extractFromMessage(BodyPart source, ExtractFn fn) throws MessagingException {
        Multipart bp = source.getParent();
        while(bp != null) {
            return extractMessageId(bp, fn);
        }
        return null;
    }

    private static String extractMessageId(Multipart source, ExtractFn fn) throws MessagingException {
        Part bp = source.getParent();
        if(bp instanceof BodyPart) {
            return extractFromMessage((BodyPart) bp, fn);
        }
        return fn.extract((Message) bp);
    }

    private static String parseContentType(String contentType) {
        if(contentType == null || contentType.isEmpty()) {
            return DEFAULT_MIMETYPE;
        }
        char[] chars = contentType.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char aChar : chars) {
            if(!(aChar >= ASCII_UPPER_A && aChar <= ASCII_UPPER_B) && !(aChar >= ASCII_LOWER_A && aChar <= ASCII_LOWER_B) && !(aChar == ASCII_SLASH)) {
                break;
            }
            sb.append(aChar);
        }

        String mimeType = sb.toString().toLowerCase();
        if(mimeType.endsWith("/") || mimeType.isEmpty()) {
            return DEFAULT_MIMETYPE;
        }
        return mimeType;
    }

    private interface ExtractFn {
        String extract(Message m) throws MessagingException;
    }

    private class MessageIdExtract implements ExtractFn {
        @Override
        public String extract(Message m) throws MessagingException {
            return m.getHeader("Message-ID")[0];
        }
    }

    private class FolderExtract implements ExtractFn {
        @Override
        public String extract(Message m) {
            return m.getFolder().getFullName();
        }
    }

}
