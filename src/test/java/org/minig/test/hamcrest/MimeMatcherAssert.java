package org.minig.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

/**
 * @author Kamill Sokol
 */
public final class MimeMatcherAssert {

    public static void assertMimeMessage(MimeMessage actual, Matcher<MimePart> matcher) {
        if (!matcher.matches(actual)) {
            Description description = new MimeMessageDescription();
            description.appendText("\nExpected: ")
                    .appendDescriptionOf(matcher)
                    .appendText("\n     but: ");
            matcher.describeMismatch(actual, description);

            throw new AssertionError(description.toString());
        }
    }
}
