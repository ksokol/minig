package org.minig.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

/**
 * @author Kamill Sokol
 */
final class AttachmentMatcher extends TypeSafeDiagnosingMatcher<MimePart> {

    private final String dispositionType;
    private final Matcher<MimePart> nameMatcher;

    AttachmentMatcher(String dispositionType, Matcher<MimePart> nameMatcher) {
        this.dispositionType = dispositionType;
        this.nameMatcher = nameMatcher;
    }

    @Override
    protected boolean matchesSafely(MimePart item, Description mismatchDescription) {
        try {
            if (!dispositionType.equals(item.getDisposition())) {
                mismatchDescription
                        .appendValue(item.getDisposition())
                        .appendText(" does not match ")
                        .appendValue(dispositionType);
                return false;
            }

            if(nameMatcher.matches(item)) {
                return true;
            }

            nameMatcher.describeMismatch(item, mismatchDescription);
        } catch (MessagingException exception) {
            mismatchDescription
                    .appendText("got exception with message ")
                    .appendValue(exception.getMessage());
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("attachment with disposition type ")
                .appendValue(dispositionType)
                .appendText(" name ");

        nameMatcher.describeTo(description);
    }
}
