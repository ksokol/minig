package org.minig.config;

import config.ServiceTestConfig;
import org.apache.tika.metadata.HttpHeaders;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.TestConstants;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.minig.server.TestConstants.MOCK_USER;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SecurityConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

    @Test
    public void shouldDisableXFrameOptionsOnMessageHtmlEndpoint() throws Exception {
        MimeMessage mimeMessage = new MimeMessageBuilder().setFolder("INBOX").setMessageId("1").mock();
        mailboxRule.append("INBOX", mimeMessage);

        ResponseEntity<String> messageHtmlBody = restTemplate.exchange("/api/1/message/INBOX%257C1/html", HttpMethod.GET, withSessionCookie(), String.class);

        assertThat(messageHtmlBody.getStatusCode(), is(HttpStatus.OK));
        assertThat(messageHtmlBody.getHeaders().get("X-Frame-Options"), nullValue());

        ResponseEntity<Void> loginPage = restTemplate.exchange("/login", HttpMethod.GET, withoutSessionCookie(), Void.class);

        assertThat(loginPage.getStatusCode(), is(HttpStatus.OK));
        assertThat(loginPage.getHeaders().get("X-Frame-Options"), contains("DENY"));
    }

    @Test
    public void shouldDisallowUntrustedJavascriptInMessageHtmlBody() throws Exception {
        MimeMessage mimeMessage = new MimeMessageBuilder().setFolder("INBOX").setMessageId("1").mock();
        mailboxRule.append("INBOX", mimeMessage);

        ResponseEntity<String> messageHtmlBody = restTemplate.exchange("/api/1/message/INBOX%257C1/html", HttpMethod.GET, withSessionCookie(), String.class);

        assertThat(messageHtmlBody.getStatusCode(), is(HttpStatus.OK));
        assertThat(messageHtmlBody.getHeaders().get("Content-Security-Policy"), contains("script-src 'self'"));
    }

    @Test
    public void shouldPreventJavascriptFromReadingSessionCookie() throws Exception {
        ResponseEntity<Void> afterProcessingLogin = restTemplate.exchange("/check", HttpMethod.POST, withUserCredentials(), Void.class);

        assertThat(afterProcessingLogin.getHeaders().get("Set-Cookie"), contains(containsString("HttpOnly")));
    }

    private HttpEntity<Void> withSessionCookie() {
        return new HttpEntity<>(buildSessionIdCookieHeader());
    }

    private HttpEntity<Void> withoutSessionCookie() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", MediaType.ALL_VALUE);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Object> withUserCredentials() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return new HttpEntity<>(String.format("username=%s&password=irrelevant", TestConstants.MOCK_USER), headers);
    }

    private MultiValueMap<String, String> buildSessionIdCookieHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Cookie", doLogin());
        return headers;
    }

    private String doLogin() {
        ResponseEntity<Void> response = restTemplate.exchange("/check", POST, credentialsBody(), Void.class);
        return extractSessionId(response);
    }

    private HttpEntity<MultiValueMap<String, String>> credentialsBody() {
        return new HttpEntity<>(credentials(), formUrlEncodedContentType());
    }

    private MultiValueMap<String, String> credentials() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", MOCK_USER);
        body.add("password", "irrelevant");
        return body;
    }

    private MultiValueMap<String, String> formUrlEncodedContentType() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return headers;
    }

    private String extractSessionId(ResponseEntity<Void> response) {
        List<String> setCookieHeader = response.getHeaders().get("Set-Cookie");
        if(setCookieHeader == null) {
            throw new AssertionError("Set-Cookie header not set");
        }
        return extractJSessionIdFromString(setCookieHeader.get(0));
    }

    private String extractJSessionIdFromString(String setCookieHeader) {
        Pattern sessionIdPattern = Pattern.compile("(JSESSIONID=[\\pL\\pN]*);.*");
        Matcher sessionIdMatcher = sessionIdPattern.matcher(setCookieHeader);
        if (sessionIdMatcher.matches()) {
            return sessionIdMatcher.group(1);
        }
        throw new AssertionError("JSESSIONID not matched");
    }
}
