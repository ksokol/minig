package org.minig.test.hamcrest;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePart;
import java.io.IOException;

/**
  @author Kamill Sokol
 */
class MimePartMatcher extends TypeSafeDiagnosingMatcher<MimePart> {

    private final String expectedMimeType;
    private final Matcher<String> bodyMatcher;

    MimePartMatcher(String expectedMimeType, Matcher<String> bodyMatcher) {
        this.expectedMimeType = expectedMimeType;
        this.bodyMatcher = bodyMatcher;
    }

    @Override
    protected boolean matchesSafely(MimePart item, Description mismatchDescription) {
        try {
            if (!item.isMimeType(expectedMimeType)) {
                mismatchDescription
                        .appendValue(new ContentType(item.getContentType()).getBaseType())
                        .appendText(" does not match ")
                        .appendValue(expectedMimeType);
                return false;
            }

            String actualBody = IOUtils.toString(item.getInputStream());

            if(bodyMatcher.matches(actualBody)) {
                return true;
            }

            mismatchDescription
                    .appendText("body ")
                    .appendValue(actualBody)
                    .appendText(" with mime type ")
                    .appendValue(expectedMimeType)
                    .appendText(" does not match body ");

            bodyMatcher.describeMismatch(item, mismatchDescription);

            if(item instanceof MimeBodyPart) {
                mismatchDescription
                        .appendText(" of parent body ")
                        .appendValue(new ContentType(((MimeBodyPart) item).getParent().getContentType()).getBaseType());
            } else {
                mismatchDescription.appendText(" of message");
            }

            return false;
        } catch (MessagingException | IOException exception) {
            mismatchDescription
                    .appendText("got exception with message ")
                    .appendValue(exception.getMessage());
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("body ");

        bodyMatcher.describeTo(description);

        description
                .appendText(" with mime type ")
                .appendValue(expectedMimeType);
    }
}
