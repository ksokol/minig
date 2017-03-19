package org.minig.server.service.mail;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import static org.minig.MinigConstants.API_VERSION;
import static org.minig.MinigConstants.RESOURCE_ATTACHMENT;

/**
 * @author Kamill Sokol
 */
@Component
class UriComponentsBuilderResolver {

    public UriComponentsBuilder resolveAttachmentUri() {
        return resolveFromCurrentServletMapping().pathSegment(API_VERSION, RESOURCE_ATTACHMENT);
    }

    private UriComponentsBuilder resolveFromCurrentServletMapping() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes instanceof ServletRequestAttributes
                ? ServletUriComponentsBuilder.fromServletMapping(((ServletRequestAttributes) requestAttributes).getRequest())
                : UriComponentsBuilder.newInstance();
    }
}
