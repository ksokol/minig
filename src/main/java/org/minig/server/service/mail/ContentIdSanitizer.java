package org.minig.server.service.mail;

import org.minig.server.service.impl.helper.mime.Mime4jAttachment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Kamill Sokol
 */
@Component
public class ContentIdSanitizer {

    private final UriComponentsBuilderResolver uriComponentsBuilderResolver;

    public ContentIdSanitizer(UriComponentsBuilderResolver uriComponentsBuilderResolver) {
        this.uriComponentsBuilderResolver = uriComponentsBuilderResolver;
    }

    public String sanitize(String htmlBody, List<Mime4jAttachment> attachments) {
        String sanitizedHtmlBody = htmlBody;
        for (Mime4jAttachment attachment : attachments) {
            sanitizedHtmlBody = sanitize(sanitizedHtmlBody, attachment);
        }
        return sanitizedHtmlBody;
    }

    private String sanitize(String htmlBody, Mime4jAttachment attachment) {
        String contentUrl = uriComponentsBuilderResolver.resolveAttachmentUri().pathSegment(attachment.getId().toString()).build().toUriString();
        String replacedCid = htmlBody.replaceAll("cid:" + attachment.getContentId(), contentUrl);
        return replacedCid.replaceAll("mid:" + attachment.getContentId(), contentUrl);
    }
}
