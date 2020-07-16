package integration;

import config.ServiceTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.Starter;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageAddress;
import org.minig.server.TestConstants;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.test.WithAuthenticatedUser;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.read;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.minig.server.TestConstants.MOCK_USER;
import static org.minig.test.JsonRequestPostProcessors.jsonBody;
import static org.minig.test.JsonRequestPostProcessors.jsonFromClasspath;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Starter.class, ServiceTestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@WithAuthenticatedUser
public class IntegrationTest {

    private static final String PREFIX = "/1";
    private static final String JSON_DRAFT_MESSAGE = "json/draft.json";

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

    @Test
    public void testFetchMailById() throws Exception {
        MimeMessage mimeMessage = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mailboxRule.append("INBOX", mimeMessage);

        mockMvc.perform(get(PREFIX + "/message/INBOX|" + mimeMessage.getMessageID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("INBOX%257C%253C1367760625.51865ef16e3f6%2540swift.generated%253E")))
                .andExpect(jsonPath("$.subject", is("Pingdom Monthly Report 2013-04-01 to 2013-04-30")));
    }

    @Test
    public void testCreateDraftAndAddAttachment() throws Exception {
        MvcResult draftCreated = mockMvc.perform(post(PREFIX + "/message/draft")
                .with(jsonFromClasspath(JSON_DRAFT_MESSAGE)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String draftId = extractMessageId(draftCreated, "$.id");

        mockMvc.perform(get(PREFIX + "/message/" + draftId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(encode(draftId))));

        MockMultipartFile attachment = new MockMultipartFile("draft.json", "draft.json", APPLICATION_JSON_VALUE, "attachment".getBytes());

        MvcResult attachmentAppended = mockMvc.perform(fileUpload(PREFIX + "/attachment/" + draftId)
                .file(attachment)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn();

        String draftIdAfterAppendAttachment = extractMessageId(attachmentAppended, "$.id.id");

        assertThat(draftIdAfterAppendAttachment, notNullValue());
        assertThat(draftIdAfterAppendAttachment, not(equalTo(draftId)));
    }

    @Test
    public void testSendDraftMessageWithAttachment() throws Exception {
        MvcResult draftCreated = mockMvc.perform(post(PREFIX + "/message/draft")
                .with(jsonFromClasspath(JSON_DRAFT_MESSAGE)))
                .andReturn();

        Message message = mailboxRule.getFirstInFolder("INBOX.Drafts").orElseThrow(() -> new AssertionError("expected draft message"));
        Mime4jMessage mime4jMessage = new Mime4jMessage(message);

        assertThat(mime4jMessage.getSender(), is(MOCK_USER));

        String draftId = extractMessageId(draftCreated, "$.id");
        MailMessage mailMessage = new MailMessage();
        mailMessage.setId(decode(draftId));
        mailMessage.setSender(new MailMessageAddress(MOCK_USER));
        mailMessage.setTo(Collections.singletonList(new MailMessageAddress("recipient@localhost")));

        mockMvc.perform(post(PREFIX + "/submission")
                .with(jsonBody(mailMessage)))
                .andExpect(status().isCreated());

        assertThat(mailboxRule.getAllInFolder("INBOX.Drafts"), hasSize(0));

        List<Message> sentFolder = mailboxRule.getAllInFolder("INBOX.Sent");
        assertThat(sentFolder, hasSize(1));
        assertThat(sentFolder.get(0).getFlags().contains(Flags.Flag.SEEN), is(true));
    }

    @Test
    public void testDeleteAttachedFileFromDraftMessage() throws Exception {
        MimeMessage mailWithAttachment = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        mailboxRule.append("INBOX.Drafts", mailWithAttachment);

        mockMvc.perform(get(PREFIX + "/message/INBOX.Drafts|" + mailWithAttachment.getMessageID()))
                .andExpect(jsonPath("$.attachments..fileName", contains("1.png", "2.png")));

        String mailId = "INBOX.Drafts|" + mailWithAttachment.getMessageID();
        String attachmentId = mailId + "|1.png";

        MvcResult mvcResult = mockMvc.perform(delete(PREFIX + "/attachment/" + attachmentId))
                .andReturn();

        String mailIdAfterAttachmentDelete = extractMessageId(mvcResult, "$.id.id");

        mockMvc.perform(get(PREFIX + "/message/" + mailIdAfterAttachmentDelete))
                .andExpect(jsonPath("$.attachments..fileName", contains("2.png")))
                .andExpect(jsonPath("$.attachments", hasSize(1)))
                .andExpect(jsonPath("$.read", is(true)));

        assertThat(mailId, not(equalTo(decode(mailIdAfterAttachmentDelete))));
    }

    private String extractMessageId(MvcResult mvcResult, String jsonPath) throws UnsupportedEncodingException {
        String draftId = read(mvcResult.getResponse().getContentAsString(), jsonPath);
        return URLDecoder.decode(draftId, UTF_8.name());
    }

    private String encode(String encoded) throws UnsupportedEncodingException {
        return URLEncoder.encode(encoded, UTF_8.name());
    }

    private String decode(String encoded) throws UnsupportedEncodingException {
        return URLDecoder.decode(URLDecoder.decode(encoded, UTF_8.name()), UTF_8.name());
    }
}
