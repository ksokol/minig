package org.minig.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

/**
 * @author Kamill Sokol
 */
final class AttachmentNameMatcher extends TypeSafeDiagnosingMatcher<MimePart> {

    private final Matcher<String> nameMatcher;

    AttachmentNameMatcher(Matcher<String> nameMatcher) {
        this.nameMatcher = nameMatcher;
    }

    @Override
    protected boolean matchesSafely(MimePart item, Description mismatchDescription) {
        try {
           String name = new ContentType(MimeUtility.decodeText(item.getContentType())).getParameter("name");
            if(nameMatcher.matches(name)) {
                return true;
            }
            nameMatcher.describeMismatch(item, mismatchDescription);
        } catch (MessagingException | UnsupportedEncodingException exception) {
            mismatchDescription
                    .appendText("got exception with message ")
                    .appendValue(exception.getMessage());
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        nameMatcher.describeTo(description);
    }
}
