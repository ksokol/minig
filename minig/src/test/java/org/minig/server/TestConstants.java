package org.minig.server;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;

public class TestConstants {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application","json", Charset.forName("UTF-8"));

    public static final String MULTIPART_WITH_ATTACHMENT = "src/test/resources/testAttachmentId.mail";
    public static final String MULTIPART_WITH_PLAIN_AND_HTML = "src/test/resources/testBody.mail";
    public static final String MULTIPART_WITH_PLAIN_AND_ATTACHMENT = "src/test/resources/multipart_plain_attachment.mail";
    public static final String MULTIPART_WITH_HTML_AND_ATTACHMENT = "src/test/resources/multipart_html_attachment.mail";
	public static final String MULTIPART_ATTACHMENT_BINARY = "src/test/resources/multipart_attachment_binary.mail";
	public static final String MULTIPART_ATTACHMENT_PLAINTEXT = "src/test/resources/multipart_attachment_plaintext.mail";
	public static final String PLAIN_DSN_HEADER = "src/test/resources/plain_dsn_header.mail";
	public static final String MULTIPART_RFC_2231 = "src/test/resources/multipart_rfc2231.mail";
	public static final String MULTIPART_RFC_2231_2 = "src/test/resources/multipart_rfc2231_2.mail";

    public static final String PLAIN = "src/test/resources/testAppendPlainAttachment.mail";
    public static final String HTML = "src/test/resources/testAppendHtmlAttachment.mail";

    public static final String ATTACHMENT_IMAGE_PNG = "src/test/resources/1.png";
	public static final String MULTIPART_RFC_2231_2_IMAGE = "src/test/resources/multipart_rfc2231_2_image.png";

}
