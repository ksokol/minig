package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamill Sokol
 */
final class Mime4jAttachmentDataExtractor {
	private Mime4jAttachmentDataExtractor() {}

	static List<Mime4jAttachment> extract(MessageImpl message) {
		if (!message.isMultipart()) {
			return Collections.emptyList();
		}
		return extractFromMultipart((Multipart) message.getBody());
	}

    private static List<Mime4jAttachment> extractFromMultipart(Multipart multipart) {
        List<BodyPart> rawAttachments = getAttachments(multipart);
        List<Mime4jAttachment> attachments = new ArrayList<>(rawAttachments.size());
        for (BodyPart bodyPart : rawAttachments) {
            attachments.addAll(toMime4jAttachment(bodyPart));
        }
        return attachments;
    }

	private static List<BodyPart> getAttachments(Multipart multipart) {
		List<BodyPart> attachments = new ArrayList<>();

		for (Entity e : multipart.getBodyParts()) {
			BodyPart part = (BodyPart) e;
            if ("attachment".equalsIgnoreCase(part.getDispositionType())) {
				attachments.add(part);
			}

			if (part.isMultipart()) {
				List<BodyPart> getAttachments = getAttachments((Multipart) part.getBody());
				attachments.addAll(getAttachments);
			}
            if("message/rfc822".equals(part.getMimeType())) {
                attachments.add(part);
            }
		}

		return attachments;
	}

	private static List<Mime4jAttachment> toMime4jAttachment(BodyPart bodyPart) {
		if(bodyPart.getBody() instanceof SingleBody) {
			return Collections.singletonList(extractFromSingleBody(bodyPart));
		}
        if(bodyPart.getBody() instanceof MessageImpl) {
            Mime4jAttachment mime4jAttachmentData = extractFromMessage((MessageImpl) bodyPart.getBody());
            if(mime4jAttachmentData.getFilename() == null) {
                mime4jAttachmentData.setFilename(getFileName(bodyPart));
            }
            return Collections.singletonList(mime4jAttachmentData);
        }
		throw new IllegalArgumentException("unknown bodyPart " + bodyPart.getClass());
	}

	private static Mime4jAttachment extractFromSingleBody(BodyPart bodyPart) {
		SingleBody source = (SingleBody) bodyPart.getBody();
        String mimeType = bodyPart.getMimeType();
        String filename = getFileName(bodyPart);
        InputStream data;

		try(InputStream in = source.getInputStream()) {
			data = in;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

        return new Mime4jAttachment(filename, mimeType, data);
	}

    private static Mime4jAttachment extractFromMessage(Message message) {
        if("text/plain".equals(message.getMimeType())) {
            return extractFromSingleBody(message);
        }
        return null;
    }

    private static Mime4jAttachment extractFromSingleBody(Message message) {
        SingleBody source = (SingleBody) message.getBody();
        String mimeType = message.getMimeType();
        String filename = String.format("%s.eml", message.getSubject());
        InputStream data;

        try(InputStream in = source.getInputStream()) {
            data = in;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return new Mime4jAttachment(filename, mimeType, data);
    }

    private static String getFileName(BodyPart bodyPart) {
        if(bodyPart.getFilename() != null) {
            return DecoderUtil.decodeEncodedWords(bodyPart.getFilename(), null);
        } else {
            //TODO remove me after https://issues.apache.org/jira/browse/MIME4J-109 has been implemented
            Field field = bodyPart.getHeader().getField("Content-Disposition");
            return new RFC2231Decoder().parse(field.getBody());
        }
    }
}
