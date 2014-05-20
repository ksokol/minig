package integration;

import javax.mail.Flags;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.TestConstants;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.minig.server.service.impl.helper.mime.Mime4jTestHelper;
import org.minig.test.javamail.Mailbox;
import org.minig.test.javamail.MailboxBuilder;
import org.minig.test.javamail.MailboxHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestConfig.class)
@ActiveProfiles("test")
public class IntegrationTest {

    private static final String PREFIX = "/1";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).build();
        MailboxHolder.reset();
    }

    @Test
    public void testFetchMailById() throws Exception {
        MimeMessage mm = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        Mailbox inbox = new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        inbox.add(mm);

        mockMvc.perform(get(PREFIX + "/message/INBOX|" + mm.getMessageID())).andExpect(status().isOk())
				.andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("INBOX|" + mm.getMessageID()))
                .andExpect(jsonPath("$.subject").value("Pingdom Monthly Report 2013-04-01 to 2013-04-30"));
    }

    @Test
    public void testCreateDraftAndAddAttachment() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("json/draft.json");
        String draftJson = FileUtils.readFileToString(classPathResource.getFile());

        new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").subscribed().exists().build();

        MvcResult draftCreated = mockMvc.perform(post(PREFIX + "/message/draft").content(draftJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
				.andExpect(content().contentType("application/json; charset=UTF-8"))
                .andReturn();

        String draftId = read(draftCreated.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get(PREFIX + "/message/" + draftId)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(draftId));

        MockMultipartFile attachment = new MockMultipartFile("draft.json", "draft.json", "application/json", draftJson.getBytes());

        MvcResult attachmentAppended = mockMvc.perform(fileUpload(PREFIX + "/attachment/" + draftId)
                .file(attachment)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn();

        String draftIdAfterAppendAttachment = attachmentAppended.getResponse().getContentAsString();

        assertThat(draftIdAfterAppendAttachment, notNullValue());
        assertThat(draftIdAfterAppendAttachment, not(equalTo(draftId)));
    }

    @Test
    public void testSendDraftMessageWithAttachment() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("json/draft.json");
        String draftJson = FileUtils.readFileToString(classPathResource.getFile());

        new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        Mailbox draftBox = new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").subscribed().exists().build();
        Mailbox sendBox = new MailboxBuilder("testuser@localhost").mailbox("INBOX.Sent").subscribed().exists().build();

        MvcResult draftCreated = mockMvc.perform(post(PREFIX + "/message/draft")
                .content(draftJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Mime4jMessage mime4jMessage = Mime4jTestHelper.convertMimeMessage(draftBox.get(0));

        assertThat(draftBox, hasSize(1));
        assertThat(mime4jMessage.getSender(), is("testuser@localhost"));

        ClassPathResource draftSend = new ClassPathResource("json/draft-send.json");
        Map<String, Object> map = om.readValue(draftSend.getInputStream(), Map.class);

        String draftId = read(draftCreated.getResponse().getContentAsString(), "$.id");

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("clientMessage", map);
        map.put("id", draftId);

        String s = om.writeValueAsString(stringObjectHashMap);

        mockMvc.perform(post(PREFIX + "/submission")
                .content(s)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertThat(draftBox, hasSize(0));
        assertThat(sendBox, hasSize(1));
		assertThat(sendBox.get(0).getFlags().contains(Flags.Flag.SEEN), is(true));
    }

    @Test
    public void testDeleteAttachedFileFromDraftMessage() throws Exception {
        MimeMessage mailWithAttachment = new MimeMessageBuilder().setFolder("INBOX.Drafts").build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        new MailboxBuilder("testuser@localhost").mailbox("INBOX").subscribed().exists().build();
        Mailbox draftBox = new MailboxBuilder("testuser@localhost").mailbox("INBOX.Drafts").subscribed().exists().build();
        draftBox.add(mailWithAttachment);

         mockMvc.perform(get(PREFIX + "/message/INBOX.Drafts|" + mailWithAttachment.getMessageID()))
                .andExpect(jsonPath("$.attachments..fileName").value(contains("1.png", "2.png")));

        String attachmentId = "INBOX.Drafts|" + mailWithAttachment.getMessageID() + "|1.png";

        MvcResult mvcResult = mockMvc.perform(delete(PREFIX + "/attachment/" + attachmentId)).andReturn();
        Map<String, String> response = om.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        String idAfterAttachmentWasRemoved = response.get("id");

        mockMvc.perform(get(PREFIX + "/message/" + idAfterAttachmentWasRemoved))
                .andExpect(jsonPath("$.attachments..fileName").value(contains("2.png")))
                .andExpect(jsonPath("$.read").value(true));

        assertThat(mailWithAttachment.getMessageID(), not(equalTo(idAfterAttachmentWasRemoved)));
    }
}
