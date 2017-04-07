package org.minig.server.service.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class UriComponentsBuilderResolverTest {

    private UriComponentsBuilderResolver resolver = new UriComponentsBuilderResolver();

    @Before
    @After
    public void setUp() throws Exception {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldResolveWithoutHttpServletRequest() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = resolver.resolveAttachmentUri();

        assertThat(uriComponentsBuilder.toUriString(), is("/1/attachment"));
    }

    @Test
    public void shouldResolveWithCurrentHttpServletRequest() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setContextPath("/minig");
        mockHttpServletRequest.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        UriComponentsBuilder uriComponentsBuilder = resolver.resolveAttachmentUri();

        assertThat(uriComponentsBuilder.toUriString(), is("http://localhost:8080/minig/1/attachment"));
    }
}
