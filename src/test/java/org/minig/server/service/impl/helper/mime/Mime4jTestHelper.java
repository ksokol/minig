package org.minig.server.service.impl.helper.mime;

import org.minig.server.service.MimeMessageBuilder;

import javax.mail.internet.MimeMessage;

/**
 * @author Kamill Sokol
 */
public final class Mime4jTestHelper {
	private Mime4jTestHelper() {}

	public static Mime4jMessage freshMime4jMessage(String fromTestMail) throws Exception {
		MimeMessage build = new MimeMessageBuilder().build(fromTestMail);
		return new Mime4jMessage(build);
	}
}
