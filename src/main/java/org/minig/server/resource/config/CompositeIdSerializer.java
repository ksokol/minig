package org.minig.server.resource.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.minig.util.PercentEscaper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Kamill Sokol
 */
public class CompositeIdSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String source, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeString(escape(source));
    }

    //http://tools.ietf.org/html/rfc3986#section-2.2
    private String escape(String s) throws UnsupportedEncodingException {
        PercentEscaper percentEscaper = new PercentEscaper("-_.*", true);
        return URLEncoder.encode(percentEscaper.escape(s), UTF_8.name());
    }


}
