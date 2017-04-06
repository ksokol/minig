package org.minig.server;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;

/**
 * @author Kamill Sokol
 */
public class TestConstants {

    public static final String MOCK_USER = "testuser@localhost";

    /**
     * @deprecated Use {@link MediaType#APPLICATION_JSON_UTF8} instead.
     */
    @Deprecated
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application","json", Charset.forName("UTF-8"));

    public static final String MULTIPART_WITH_ATTACHMENT = "src/test/resources/mime/testAttachmentId.mail";
    public static final String MULTIPART_WITH_PLAIN_AND_HTML = "src/test/resources/mime/testBody.mail";
    public static final String MULTIPART_WITH_INLINE_PLAIN_AND_INLINE_HTML = "src/test/resources/mime/testBodyInline.mail";
    public static final String MULTIPART_WITH_PLAIN_AND_ATTACHMENT = "src/test/resources/mime/multipart_plain_attachment.mail";
    public static final String MULTIPART_WITH_HTML_AND_ATTACHMENT = "src/test/resources/mime/multipart_html_attachment.mail";
    public static final String PLAIN_DSN_HEADER_1 = "src/test/resources/mime/plain_dsn_header_1.mail";
    public static final String PLAIN_DSN_HEADER_2 = "src/test/resources/mime/plain_dsn_header_2.mail";
    public static final String PLAIN_DSN_HEADER_3 = "src/test/resources/mime/plain_dsn_header_3.mail";
    public static final String MULTIPART_ATTACHMENT_BINARY = "src/test/resources/mime/multipart_attachment_binary.mail";
	public static final String MULTIPART_ATTACHMENT_PLAINTEXT = "src/test/resources/mime/multipart_attachment_plaintext.mail";
	public static final String MULTIPART_RFC_2231 = "src/test/resources/mime/multipart_rfc2231.mail";
	public static final String MULTIPART_RFC_2231_2 = "src/test/resources/mime/multipart_rfc2231_2.mail";
    public static final String PLAIN = "src/test/resources/mime/testAppendPlainAttachment.mail";
    public static final String HTML = "src/test/resources/mime/testAppendHtmlAttachment.mail";
    public static final String DISPOSITION_NOTIFICATION = "src/test/resources/mime/testDispositionNotification.mail";
    public static final String NESTED_MESSAGE = "src/test/resources/mime/testNestedMessage.mail";
    public static final String ALTERNATIVE = "src/test/resources/mime/alternative.mail";
    public static final String PLAIN_ATTACHMENT = "src/test/resources/mime/plain_attachment.mail";

    public static final String ATTACHMENT_IMAGE_1_PNG = "src/test/resources/image/1.png";
    public static final String ATTACHMENT_IMAGE_FOLDER_GIF = "src/test/resources/image/folder.gif";
    public static final String MULTIPART_RFC_2231_2_IMAGE = "src/test/resources/image/multipart_rfc2231_2_image.png";
}
