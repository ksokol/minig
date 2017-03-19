package org.minig.server.service.mail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.service.CompositeId;
import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Kamill Sokol
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentIdSanitizerTest {

    @InjectMocks
    private ContentIdSanitizer sanitizer;

    @Mock
    private UriComponentsBuilderResolver resolver;

    @Before
    public void setUp() throws Exception {
        when(resolver.resolveAttachmentUri()).thenAnswer(invocation -> {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
            uriComponentsBuilder.pathSegment("attachment", "junit");
            return uriComponentsBuilder;
        });
    }

    @Test
    public void shouldReplaceCidAndMidWithAbsoluteUrl() throws Exception {
        String htmlBody = "<a href='cid:contentId2'></a><a href='mid:contentId1'></a>";

        String sanitizedHtmlBody = sanitizer.sanitize(htmlBody, Arrays.asList(anAttachment("1"), anAttachment("2")));

        assertThat(sanitizedHtmlBody, is("<a href='/attachment/junit/folder|messageId2|contentId2'></a><a href='/attachment/junit/folder|messageId1|contentId1'></a>"));
    }

    private Mime4jAttachment anAttachment(String idx) {
        CompositeId compositeId = new CompositeId("folder", "messageId" + idx);
        return new Mime4jAttachment(compositeId, "fileName" + idx, "contentId" + idx, "inline", "mimeType", null);
    }

}
