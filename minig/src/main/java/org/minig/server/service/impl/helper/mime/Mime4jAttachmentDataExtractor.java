package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.dom.*;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamill Sokol
 */
final class Mime4jAttachmentDataExtractor {
	private Mime4jAttachmentDataExtractor() {}

	static List<Mime4jAttachmentData> extract(MessageImpl message) {
		if (!message.isMultipart()) {
			return Collections.emptyList();
		}
		return extractFromMultipart((Multipart) message.getBody());
	}

    private static List<Mime4jAttachmentData> extractFromMultipart(Multipart multipart) {
        List<BodyPart> rawAttachments = getAttachments(multipart);
        List<Mime4jAttachmentData> attachments = new ArrayList<>(rawAttachments.size());
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

	private static List<Mime4jAttachmentData> toMime4jAttachment(BodyPart bodyPart) {
		if(bodyPart.getBody() instanceof SingleBody) {
			return Arrays.asList(extractFromSingleBody(bodyPart));
		}
        if(bodyPart.getBody() instanceof MessageImpl) {
            return extractFromMessage((MessageImpl) bodyPart.getBody());
        }
		throw new IllegalArgumentException("unknown bodyPart " + bodyPart.getClass());
	}

	private static Mime4jAttachmentData extractFromSingleBody(BodyPart bodyPart) {
		SingleBody source = (SingleBody) bodyPart.getBody();
		Mime4jAttachmentData target = new Mime4jAttachmentData();

		target.setMimeType(bodyPart.getMimeType());

		if(bodyPart.getFilename() != null) {
			target.setFilename(DecoderUtil.decodeEncodedWords(bodyPart.getFilename(), null));
		} else {
			//TODO remove me after https://issues.apache.org/jira/browse/MIME4J-109 has been implemented
			Field field = bodyPart.getHeader().getField("Content-Disposition");
			String parse = new RFC2231Decoder().parse(field.getBody());
			target.setFilename(parse);
		}

		try(InputStream in = source.getInputStream()) {
			target.setSize(in.available());
			target.setData(source.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return target;
	}

    private static List<Mime4jAttachmentData> extractFromMessage(Message message) {
        if("text/plain".equals(message.getMimeType())) {
            return Arrays.asList(extractFromSingleBody(message));
        }
        if (!message.isMultipart()) {
            return Collections.emptyList();
        }
        return extractFromMultipart((Multipart) message.getBody());
    }

    private static Mime4jAttachmentData extractFromSingleBody(Message message) {
        SingleBody source = (SingleBody) message.getBody();
        Mime4jAttachmentData target = new Mime4jAttachmentData();

        target.setMimeType(message.getMimeType());
        target.setFilename(String.format("%s.eml",message.getSubject()));

        try(InputStream in = source.getInputStream()) {
            target.setSize(in.available());
            target.setData(source.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return target;
    }
}
