package org.minig.server.resource.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.minig.server.MailAttachment;
import org.minig.server.service.CompositeAttachmentId;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Kamill Sokol
 *
 * TODO remove this custom serialzer after a clear separation between entities and value objects has been established.
 */
public class CompositeAttachmentIdSerializer extends JsonSerializer<CompositeAttachmentId> {

	@Override
	public void serialize(CompositeAttachmentId source, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
		String encode = URLEncoder.encode(source.getFileName(), "UTF-8");

		CompositeAttachmentId target = new CompositeAttachmentId();
		target.setFolder(source.getFolder());
		target.setMessageId(source.getMessageId());
		target.setFileName(encode);

		jgen.writeStartObject();
		jgen.writeObjectField("id", target.getId());
		jgen.writeObjectField("messageId", source.getMessageId());
		jgen.writeObjectField("folder", source.getFolder());
		jgen.writeObjectField("fileName", source.getFileName());

		if(source instanceof MailAttachment) {
			MailAttachment ma = (MailAttachment) source;
			jgen.writeObjectField("mime", ma.getMime());
			jgen.writeObjectField("size", ma.getSize());
		}

		jgen.writeEndObject();
	}
}
