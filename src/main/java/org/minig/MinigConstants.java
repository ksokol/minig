package org.minig;

/**
 * @author Kamill Sokol
 */
public final class MinigConstants {

    private MinigConstants() {
        // prevent instantiation
    }

    public static final String API_VERSION = "1";
    public static final String RESOURCE_ATTACHMENT = "attachment";

    public static final String PRIMARY_TYPE_TEXT = "text";
    public static final String PRIMARY_TYPE_MULTIPART = "multipart";

    public static final String SUB_TYPE_PLAIN = "plain";
    public static final String SUB_TYPE_HTML = "html";
    public static final String SUB_TYPE_MIXED = "mixed";
    public static final String SUB_TYPE_ALTERNATIVE = "alternative";

    public static final String MIME_TYPE_TEXT_PLAIN = PRIMARY_TYPE_TEXT + "/" + SUB_TYPE_PLAIN;
    public static final String MIME_TYPE_TEXT_HTML = PRIMARY_TYPE_TEXT + "/" + SUB_TYPE_HTML;
    public static final String MIME_TYPE_MULTIPART_MIXED = PRIMARY_TYPE_MULTIPART + "/" + SUB_TYPE_MIXED;
    public static final String MIME_TYPE_MULTIPART_ALTERNATIVE = PRIMARY_TYPE_MULTIPART + "/" + SUB_TYPE_ALTERNATIVE;
    public static final String MIME_TYPE_MULTIPART_RELATED = PRIMARY_TYPE_MULTIPART + "/related";
    public static final String MIME_TYPE_MESSAGE_RFC_822 = "message/rfc822";

    public static final String MESSAGE_ID = "Message-ID";
    public static final String X_DRAFT_INFO = "X-Mozilla-Draft-Info";
    public static final String X_PRIORITY = "X-PRIORITY";
    public static final String IN_REPLY_TO = "In-Reply-To";
    public static final String REFERENCES = "References";
    public static final String FORWARDED_MESSAGE_ID = "X-Forwarded-Message-Id";
    public static final String MDN_SENT = "$MDNSent";
    public static final String FORWARDED = "$Forwarded";
}
