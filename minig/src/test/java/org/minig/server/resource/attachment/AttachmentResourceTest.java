package org.minig.server.resource.attachment;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.RessourceTestConfig;
import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.TestConstants;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RessourceTestConfig.class)
@ActiveProfiles("test")
public class AttachmentResourceTest {

    private static final String PREFIX = "/1";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AttachmentService attachmentServiceMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).build();
        reset(attachmentServiceMock);
    }

    @Test
    public void testReadAttachment_noAttachmentIdGiven() throws Exception {
        mockMvc.perform(get(PREFIX + "/attachment")).andExpect(status().isNotFound());
    }

    @Test
    public void testReadAttachment_hasAttachment() throws Exception {
        List<MailAttachment> l = new ArrayList<MailAttachment>();
        MailAttachment ma = new MailAttachment();

        ma.setFileName("filename");
        ma.setId("id");
        ma.setMime("mime");
		ma.setMessageId("messageId");
		ma.setFolder("folder");
        l.add(ma);

        when(attachmentServiceMock.findAttachments(Matchers.<CompositeId> anyObject())).thenReturn(new MailAttachmentList(l));

        mockMvc.perform(get(PREFIX + "/attachment/INBOX/test|1")).andExpect(status().isOk())
				.andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("attachmentMetadata[0].fileName").value("filename"))
                .andExpect(jsonPath("attachmentMetadata[0].id").value("folder|messageId|filename"))
                .andExpect(jsonPath("attachmentMetadata[0].mime").value("mime"));

        verify(attachmentServiceMock).findAttachments(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("messageId", IsEqual.<String> equalTo("1"))));
        verify(attachmentServiceMock).findAttachments(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("folder", IsEqual.<String> equalTo("INBOX/test"))));
    }

    @Test
    public void testDownloadAttachment() throws Exception {
        final byte[] expected = IOUtils.toByteArray(new FileInputStream(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        MailAttachment ma = new MailAttachment();

        ma.setFileName("filename");
        ma.setId("id");
        ma.setMime("mime");

        when(attachmentServiceMock.findAttachment(Matchers.<CompositeAttachmentId> anyObject())).thenReturn(ma);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream out = (OutputStream) invocation.getArguments()[1];
                IOUtils.copy(new ByteArrayInputStream(expected), out);
                return null;
            }
        }).when(attachmentServiceMock).readAttachment(Matchers.<CompositeAttachmentId> anyObject(), Matchers.<OutputStream> anyObject());

        mockMvc.perform(get(PREFIX + "/attachment/INBOX/test|<id@localhost>|1.png").param("download", "true")).andExpect(status().isOk())
                .andExpect(content().bytes(expected)).andExpect(header().string("Content-Disposition", "attachment; filename=\"filename\""))
                .andExpect(header().string("Content-Type", "mime"));
    }

    @Test
    public void testUploadAttachment() throws Exception {
        CompositeAttachmentId compositeId = new CompositeAttachmentId("INBOX/test", "id", "data.txt");

        when(attachmentServiceMock.addAttachment(Matchers.<CompositeId>anyObject(), Matchers.<MultipartfileDataSource>anyObject()))
                .thenReturn(compositeId);

        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setFileName("file.html");
        mailAttachment.setMessageId(compositeId.getMessageId());
        mailAttachment.setFolder(compositeId.getFolder());
        mailAttachment.setMime("text/html");

        MailAttachmentList mailAttachmentList = new MailAttachmentList();
        mailAttachmentList.setAttachmentMetadata(Arrays.asList(mailAttachment));

        when(attachmentServiceMock.findAttachments(compositeId)).thenReturn(mailAttachmentList);

        mockMvc.perform(fileUpload(PREFIX + "/attachment/INBOX/test|id").file("data.txt", "data".getBytes()))
                .andExpect(content().string("{\"id\":{\"id\":\"INBOX/test|id|data.txt\",\"messageId\":\"id\",\"folder\":\"INBOX/test\",\"fileName\":\"data.txt\"},\"attachmentMetadata\":[{\"id\":\"INBOX/test|id|file.html\",\"messageId\":\"id\",\"folder\":\"INBOX/test\",\"fileName\":\"file.html\",\"mime\":\"text/html\"}]}"));

        verify(attachmentServiceMock).addAttachment(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("messageId", IsEqual.<String> equalTo("id"))),
                Matchers.<DataSource> anyObject());
    }

	@Test
	public void testReadAttachment_encodedFilename() throws Exception {
		List<MailAttachment> l = new ArrayList<MailAttachment>();
		MailAttachment ma = new MailAttachment();

		ma.setFolder("folder");
		ma.setMessageId("messageId");
		ma.setFileName("umlaut ä.png");

		l.add(ma);

		when(attachmentServiceMock.findAttachments(Matchers.<CompositeId> anyObject())).thenReturn(new MailAttachmentList(l));

		mockMvc.perform(get(PREFIX + "/attachment/INBOX/test|1"))
				.andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("attachmentMetadata[0].fileName").value("umlaut ä.png"))
				.andExpect(jsonPath("attachmentMetadata[0].id").value("folder|messageId|umlaut%2B%25C3%25A4.png"));
	}
}
