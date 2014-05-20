package org.minig.server.resource.config;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.minig.server.MailAttachment;
import org.minig.server.service.CompositeAttachmentId;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Kamill Sokol
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
