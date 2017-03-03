package org.minig.test.hamcrest;

import org.hamcrest.Matcher;

import javax.mail.internet.MimePart;

/**
 * @author Kamill Sokol
 */
public final class Mime4jMessageMatchers {

    public static Matcher<MimePart> textBody(Matcher<String> bodyMatcher) {
        return new MimePartMatcher("text/plain", bodyMatcher);
    }

    public static Matcher<MimePart> htmlBody(Matcher<String> expectedBody) {
        return new MimePartMatcher("text/html", expectedBody);
    }

    public static Matcher<MimePart> octetStreamBody(Matcher<String> expectedBody) {
        return new MimePartMatcher("application/octet-stream", expectedBody);
    }

    public static Matcher<MimePart> inlineAttachment(Matcher<MimePart> nameMatcher) {
        return new AttachmentMatcher("inline", nameMatcher);
    }

    public static Matcher<MimePart> attachment(Matcher<MimePart> nameMatcher) {
        return new AttachmentMatcher("attachment", nameMatcher);
    }

    public static Matcher<MimePart> attachmentName(Matcher<String> nameMatcher) {
        return new AttachmentNameMatcher(nameMatcher);
    }

    public static Matcher<MimePart> mixed(Matcher<MimePart>...itemMatchers) {
        return new MultiPartMatcher("multipart/mixed", itemMatchers);
    }

    public static Matcher<MimePart> alternative(Matcher<MimePart>...itemMatchers) {
        return new MultiPartMatcher("multipart/alternative", itemMatchers);
    }

    public static Matcher<MimePart> related(Matcher<MimePart>...itemMatchers) {
        return new MultiPartMatcher("multipart/related", itemMatchers);
    }
}
