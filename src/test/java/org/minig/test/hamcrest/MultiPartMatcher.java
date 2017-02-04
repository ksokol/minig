package org.minig.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;

/**
 * @author Kamill Sokol
 */
final class MultiPartMatcher extends TypeSafeDiagnosingMatcher<MimePart> {

    private final String expectedMimeType;
    private final Matcher<MimePart>[] itemMatchers;

    MultiPartMatcher(String expectedMimeType, Matcher<MimePart>...itemMatchers) {
        this.expectedMimeType = expectedMimeType;
        this.itemMatchers = itemMatchers;
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

            Multipart body = (Multipart) item.getContent();

            List<BodyPart> parts = new ArrayList<>(body.getCount());

            for (int i = 0; i < body.getCount(); i++) {
                BodyPart bodyPart = body.getBodyPart(i);
                parts.add(bodyPart);
            }

            Matcher<Iterable<? extends MimePart>> containsMatcher = contains(this.itemMatchers);

            if (containsMatcher.matches(parts)) {
                return true;
            }

            containsMatcher.describeMismatch(parts, mismatchDescription);

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
        description
                .appendText("a ")
                .appendValue(expectedMimeType)
                .appendText(" message with body part(s) ")
                .appendValue(Arrays.stream(itemMatchers).collect(toList()));
    }
}
