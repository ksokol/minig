package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.minig.server.service.CompositeId;
import org.springframework.util.Assert;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
public final class Mime4jMessageFactory {
	private Mime4jMessageFactory() {}

	private static final MessageServiceFactoryImpl messageServiceFactory = new MessageServiceFactoryImpl();

	public static Mime4jMessage from(Message message) {
		Assert.notNull(message);
		try {
			MessageBuilder builder = messageServiceFactory.newMessageBuilder();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			message.writeTo(output);
			InputStream decodedInput = new ByteArrayInputStream((output).toByteArray());
			MessageImpl parseMessage = (MessageImpl) builder.parseMessage(decodedInput);

			String folder = getFolder(message);
			String messageId = getMessageId(message);

			return new Mime4jMessage(parseMessage, new CompositeId(folder, messageId));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static String getFolder(Message message) throws MessagingException {
		Assert.notNull(message.getFolder());
		Assert.notNull(message.getFolder().getFullName());
		return message.getFolder().getFullName();
	}

	private static String getMessageId(Message message) throws MessagingException {
		String[] messageId = message.getHeader("Message-ID");
		Assert.notNull(messageId);
		Assert.notNull(messageId[0]);
		return messageId[0];
	}
}
