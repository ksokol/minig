package org.minig.server.resource.attachment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailAttachment;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.test.WithAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AttachmentResource.class)
@WithAuthenticatedUser
public class AttachmentResourceTest {

    private static final String PREFIX = "/1";

    @MockBean
    private AttachmentService attachmentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDownloadAttachment() throws Exception {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("INBOX/test", "<id@localhost>", "1.png");
        MailAttachment mailAttachment = new MailAttachment(
                compositeAttachmentId,
                TEXT_PLAIN_VALUE,
                null,
                "attachment",
                new ByteArrayInputStream("data".getBytes())
        );

        when(attachmentService.findById(compositeAttachmentId)).thenReturn(mailAttachment);

        mockMvc.perform(get(PREFIX + "/attachment/INBOX%2Ftest%7C%3Cid@localhost%3E%7C1.png"))
                .andExpect(status().isOk())
                .andExpect(content().string("data"))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=\"1.png\""))
                .andExpect(header().string(CONTENT_TYPE, TEXT_PLAIN_VALUE));
    }

    @Test
    public void shouldDownloadInlineAttachment() throws Exception {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("INBOX/test", "<id@localhost>", "1.png");
        MailAttachment mailAttachment = new MailAttachment(
                compositeAttachmentId,
                TEXT_PLAIN_VALUE,
                "contentId",
                "inline",
                new ByteArrayInputStream("data".getBytes())
        );

        when(attachmentService.findById(compositeAttachmentId)).thenReturn(mailAttachment);

        mockMvc.perform(get(PREFIX + "/attachment/INBOX%2Ftest%7C%3Cid@localhost%3E%7C1.png"))
                .andExpect(status().isOk())
                .andExpect(content().string("data"))
                .andExpect(header().string(CONTENT_DISPOSITION, "inline; filename=\"1.png\""))
                .andExpect(header().string(CONTENT_TYPE, TEXT_PLAIN_VALUE));
    }

    @Test
    public void shouldUploadAttachment() throws Exception {
        CompositeId compositeId = new CompositeId("INBOX/test", "id");

        when(attachmentService.addAttachment(any(), any())).thenReturn(compositeId);

        MailAttachment mailAttachment = new MailAttachment(
                new CompositeAttachmentId(compositeId.getFolder(), compositeId.getMessageId(), "file.html"),
                TEXT_HTML_VALUE,
                null,
                "attachment",
                null);

        when(attachmentService.findAttachments(compositeId)).thenReturn(Collections.singletonList(mailAttachment));

        mockMvc.perform(fileUpload(PREFIX + "/attachment/INBOX%2Ftest%7Cid")
                .file("file.html", "data".getBytes()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id.id").value("INBOX%252Ftest%257Cid"))
                .andExpect(jsonPath("$.id.messageId").value("id"))
                .andExpect(jsonPath("$.id.folder").value("INBOX/test"))
                .andExpect(jsonPath("$.attachments[0].id").value("INBOX%252Ftest%257Cid%257Cfile.html"))
                .andExpect(jsonPath("$.attachments[0].messageId").value("id"))
                .andExpect(jsonPath("$.attachments[0].folder").value("INBOX/test"))
                .andExpect(jsonPath("$.attachments[0].fileName").value("file.html"))
                .andExpect(jsonPath("$.attachments[0].mime").value(TEXT_HTML_VALUE));

        verify(attachmentService).addAttachment(argThat(hasProperty("messageId", equalTo("id"))), any());
    }

    @Test
    public void shouldDownloadAttachmentWithEncodedFilename() throws Exception {
        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("INBOX/test", "1", "umlaut ä.png");
        MailAttachment mailAttachment = new MailAttachment(
                compositeAttachmentId,
                IMAGE_PNG_VALUE,
                null,
                "attachment",
                new ByteArrayInputStream("data".getBytes())
        );

        when(attachmentService.findById(new CompositeAttachmentId("INBOX/test", "1", "umlaut ä.png"))).thenReturn(mailAttachment);

        mockMvc.perform(get(PREFIX + "/attachment/INBOX%2Ftest%7C1%7Cumlaut%20%C3%A4.png"))
                .andExpect(status().isOk())
                .andExpect(content().string("data"))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=\"umlaut ä.png\""))
                .andExpect(header().string(CONTENT_TYPE, IMAGE_PNG_VALUE));
    }
}
