package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.minig.server.service.MimeMessageBuilder;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.io.*;

/**
 * @author Kamill Sokol
 */
public final class Mime4jTestHelper {
	private Mime4jTestHelper() {}

	private static final MessageServiceFactoryImpl messageServiceFactory = new MessageServiceFactoryImpl();

	public static Mime4jMessage freshMime4jMessage(String fromTestMail) throws Exception {
		MimeMessage build = new MimeMessageBuilder().build(fromTestMail);
		return convertMimeMessage(build);
	}

	public static MessageImpl freshMessageImpl(String fromTestMail) throws Exception {
		MessageBuilder newMessageBuilder = messageServiceFactory.newMessageBuilder();
		InputStream decodedInput = new FileInputStream(new File(fromTestMail));
		MessageImpl parseMessage = (MessageImpl) newMessageBuilder.parseMessage(decodedInput);
		return parseMessage;
	}

	public static Mime4jMessage convertMimeMessage(Message mimeMessage) {
		return Mime4jMessageFactory.from(mimeMessage);
	}
}
