package org.minig.test.mime4j;

import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author dev@sokol-web.de <Kamill Sokol>
 */
public class Mime4jTestHelper {

    public static Mime4jMessage convertMimeMessage(Message mimeMessage) {
        try {
            MessageBuilder builder = new MessageServiceFactoryImpl().newMessageBuilder();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mimeMessage.writeTo(output);
            InputStream decodedInput = new ByteArrayInputStream((output).toByteArray());
            MessageImpl parseMessage = (MessageImpl) builder.parseMessage(decodedInput);
            return new Mime4jMessage(parseMessage);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
