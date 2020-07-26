package org.minig.server.service.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UriComponentsBuilderResolverTest {

    private final UriComponentsBuilderResolver resolver = new UriComponentsBuilderResolver();

    @Before
    @After
    public void setUp() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldResolveWithoutHttpServletRequest() {
        var uriComponentsBuilder = resolver.resolveAttachmentUri();

        assertThat(uriComponentsBuilder.toUriString(), is("/api/1/attachment"));
    }

    @Test
    public void shouldResolveWithCurrentHttpServletRequest() {
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setContextPath("/minig");
        mockHttpServletRequest.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        var uriComponentsBuilder = resolver.resolveAttachmentUri();

        assertThat(uriComponentsBuilder.toUriString(), is("http://localhost:8080/minig/api/1/attachment"));
    }
}
