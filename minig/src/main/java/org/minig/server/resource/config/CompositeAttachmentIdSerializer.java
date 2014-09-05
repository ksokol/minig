package org.minig.server.resource.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.minig.server.MailAttachment;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.util.PercentEscaper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Kamill Sokol
 *
 * TODO remove this custom serialzer after a clear separation between entities and value objects has been established.
 */
public class CompositeAttachmentIdSerializer extends JsonSerializer<CompositeAttachmentId> {

	@Override
	public void serialize(CompositeAttachmentId source, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
		CompositeAttachmentId target = new CompositeAttachmentId();
		target.setFolder(source.getFolder());
		target.setMessageId(escape(source.getMessageId()));
		target.setFileName(escape(source.getFileName()));

		jgen.writeStartObject();
		jgen.writeObjectField("id", target.getId());
		jgen.writeObjectField("messageId", target.getMessageId());
		jgen.writeObjectField("folder", target.getFolder());
		jgen.writeObjectField("fileName", source.getFileName());

		if(source instanceof MailAttachment) {
			MailAttachment ma = (MailAttachment) source;
			jgen.writeObjectField("mime", ma.getMime());
			jgen.writeObjectField("size", ma.getSize());
		}

		jgen.writeEndObject();
	}

    //http://tools.ietf.org/html/rfc3986#section-2.2
    private String escape(String s) throws UnsupportedEncodingException {
        PercentEscaper percentEscaper = new PercentEscaper("-_.*", true);
        return URLEncoder.encode(percentEscaper.escape(s), "UTF-8");
    }


}
