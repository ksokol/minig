package org.minig.server.resource.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.FullMailMessage;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.PartialMailMessage;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.NotFoundException;
import org.minig.server.service.mail.MailService;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.minig.test.JsonRequestPostProcessors.jsonBody;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MailResource.class, secure = false)
public class MailResourceTest {

    private static final String PREFIX = "/1";

    @MockBean
    private MailService mailService;

    @Autowired
    private MockMvc mockMvc;

    @Ignore
    @Test
    public void testFindMessagesByFolder_invalidArguments() throws Exception {
        mockMvc.perform(get(PREFIX + "/message")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnPagedMessageList() throws Exception {
        PartialMailMessage partialMailMessage = new PartialMailMessage(new MimeMessageBuilder().build(TestConstants.HTML));
        when(mailService.findMessagesByFolder(anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.singletonList(partialMailMessage), new PageRequest(0, 1), 1));

        mockMvc.perform(get(PREFIX + "/message").param("folder", "INBOX"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.mailList[0].date").value("2013-07-20T16:33:20Z"))
                .andExpect(jsonPath("$.mailList[0].folder").value("folder"))
                .andExpect(jsonPath("$.mailList[0].messageId").value("<51EABBD0.3060000@localhost>"))
                .andExpect(jsonPath("$.mailList[0].answered").value(false))
                .andExpect(jsonPath("$.mailList[0].read").value(false))
                .andExpect(jsonPath("$.mailList[0].starred").value(false))
                .andExpect(jsonPath("$.mailList[0].subject").value("test"))
                .andExpect(jsonPath("$.mailList[0].sender.email").value("testuser@localhost"))
                .andExpect(jsonPath("$.mailList[0].sender.displayName").value("Test"))
                .andExpect(jsonPath("$.mailList[0].sender.display").value("Test"))
                .andExpect(jsonPath("$.mailList[0].deleted").value(false))
                .andExpect(jsonPath("$.mailList[0].id").value("folder%257C%253C51EABBD0.3060000%2540localhost%253E"))
                .andExpect(jsonPath("$.fullLength").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    public void shouldReturnHtmlBodyOnGetForKnownId() throws Exception {
        when(mailService.findHtmlBodyByCompositeId(any())).thenReturn("html");

        mockMvc.perform(get(PREFIX + "/message/INBOX%2Ftest%7C1@example.com/html")
                .accept(TEXT_HTML)
                .accept(TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(content().string(is("html")));

        verify(mailService).findHtmlBodyByCompositeId(
                argThat(
                        allOf(
                                hasProperty("folder", is("INBOX/test")),
                                hasProperty("messageId", is("1@example.com"))
                        )
                )
        );
    }

    @Test
    public void shouldReturn404OnGetHtmlForUnknownId() throws Exception {
        when(mailService.findHtmlBodyByCompositeId(any())).thenThrow(new NotFoundException());

        mockMvc.perform(get(PREFIX + "/message/INBOX%7Cunknown/html")
                .accept(TEXT_HTML))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindMessagesByFolder_defaultArguments() throws Exception {
        when(mailService.findMessagesByFolder(anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.emptyList(), new PageRequest(1, 1), 1));

        mockMvc.perform(get(PREFIX + "/message").param("folder", "INBOX"));

        verify(mailService).findMessagesByFolder("INBOX", 1, 10);
    }

    @Test
    public void testFindMessagesByFolder_explicitArguments() throws Exception {
        when(mailService.findMessagesByFolder(anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.emptyList(), new PageRequest(3, 5), 5));

        mockMvc.perform(get(PREFIX + "/message").param("folder", "INBOX").param("page", "7").param("page_length", "11"));

        verify(mailService).findMessagesByFolder("INBOX", 7, 11);
    }

    @Test
    public void testFindMessage_slashAsFolderSeparator() throws Exception {
        FullMailMessage message = new FullMailMessage(MimeMessageBuilder.withSource(TestConstants.MULTIPART_WITH_ATTACHMENT)
                .setFolder("INBOX/deep/folder/structure")
                .setMessageId("1").spy());

        when(mailService.findByCompositeId(new CompositeId("INBOX/deep/folder/structure", "1")))
                .thenReturn(message);

        mockMvc.perform(get(PREFIX + "/message/INBOX%2Fdeep%2Ffolder%2Fstructure%7C1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is("INBOX%252Fdeep%252Ffolder%252Fstructure%257C1")));
    }

    @Test
    public void testFindMessage_dotAsFolderSeparator() throws Exception {
        FullMailMessage message = new FullMailMessage(MimeMessageBuilder.withSource(TestConstants.MULTIPART_WITH_ATTACHMENT)
                .setFolder("INBOX.deep.folder.structure")
                .setMessageId("1").spy());

        when(mailService.findByCompositeId(new CompositeId("INBOX.deep.folder.structure", "1")))
                .thenReturn(message);

        mockMvc.perform(get(PREFIX + "/message/INBOX.deep.folder.structure|1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is("INBOX.deep.folder.structure%257C1")));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        doNothing().when(mailService).deleteMessage(anyObject());

        mockMvc.perform(delete(PREFIX + "/message/INBOX%2Ffolder%7C1"))
                .andExpect(status().isOk());

        verify(mailService).deleteMessage(argThat(allOf(
                hasProperty("messageId", is("1")),
                hasProperty("folder", is("INBOX/folder"))
        )));
    }

    @Test
    public void testDeleteMessageDoubleEncodedId() throws Exception {
        doNothing().when(mailService).deleteMessage(anyObject());

        mockMvc.perform(delete(PREFIX + "/message/INBOX%25252Ffolder%25257C1"))
                .andExpect(status().isOk());

        verify(mailService).deleteMessage(argThat(allOf(
                hasProperty("messageId", is("1")),
                hasProperty("folder", is("INBOX/folder"))
        )));
    }

    @Test
    public void testUpdateMessageFlags() throws Exception {
        doNothing().when(mailService).updateMessageFlags(any(MailMessage.class));

        mockMvc.perform(put(PREFIX + "/message/flag/INBOX%2Ffolder%7C1")
                .with(jsonBody(new MailMessage())))
                .andExpect(status().isOk());

        verify(mailService).updateMessageFlags(argThat(hasProperty("id", is("INBOX/folder|1"))));
    }

    @Test
    public void testUpdateMessagesFlags() throws Exception {
        String content = new ObjectMapper().writeValueAsString(new MailMessageList());

        doNothing().when(mailService).updateMessagesFlags(any(MailMessageList.class));

        mockMvc.perform(put(PREFIX + "/message/flag").contentType(TestConstants.APPLICATION_JSON_UTF8).content(content)).andExpect(status().isOk());

        verify(mailService).updateMessagesFlags(any(MailMessageList.class));
    }

    @Test
    public void testMoveMessagesToFolder() throws Exception {
        String content = new ObjectMapper().writeValueAsString(new MessageCopyOrMoveRequest());

        doNothing().when(mailService).moveMessagesToFolder(Matchers.<List<CompositeId>> anyObject(), anyString());

        mockMvc.perform(put(PREFIX + "/message/move").contentType(TestConstants.APPLICATION_JSON_UTF8).content(content)).andExpect(status().isOk());

        verify(mailService).moveMessagesToFolder(Matchers.<List<CompositeId>> anyObject(), anyString());
    }

    @Test
    public void testCopyMessagesToFolder() throws Exception {
        MessageCopyOrMoveRequest request = new MessageCopyOrMoveRequest();
        request.setFolder("INBOX");
        request.setMessageIdList(Collections.EMPTY_LIST);

        String content = new ObjectMapper().writeValueAsString(request);

        doNothing().when(mailService).copyMessagesToFolder(any(List.class), anyString());

        mockMvc.perform(put(PREFIX + "/message/copy").contentType(TestConstants.APPLICATION_JSON_UTF8).content(content)).andExpect(status().isOk());

        verify(mailService).copyMessagesToFolder(request.getMessageIdList(), "INBOX");
    }

    @Test
    public void testDeleteMessagesToFolder() throws Exception {
        DeleteMessageRequest request = new DeleteMessageRequest();
        request.setMessageIdList(Collections.EMPTY_LIST);

        String content = new ObjectMapper().writeValueAsString(request);

        doNothing().when(mailService).deleteMessages(any(List.class));

        mockMvc.perform(put(PREFIX + "/message/delete").contentType(TestConstants.APPLICATION_JSON_UTF8).content(content))
                .andExpect(status().isOk());

        verify(mailService).deleteMessages(request.getMessageIdList());
    }

    @Test
    public void testCreateDraftMessage() throws Exception {
        MailMessage mm = new MailMessage();
        mm.setSubject("draft");

        String content = new ObjectMapper().writeValueAsString(mm);

        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId("1");

        when(mailService.createDraftMessage(Matchers.<MailMessage> anyObject())).thenReturn(mm);
        when(mailService.findMessage(Matchers.<CompositeId> anyObject())).thenReturn(mm);

        mockMvc.perform(post(PREFIX + "/message/draft")
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("draft"));
    }

    @Test
    public void testUpdateDraftMessage() throws Exception {
        MailMessage mailMessage = new MailMessage();
        mailMessage.setSubject("draft");

        when(mailService.updateDraftMessage(anyObject())).thenReturn(mailMessage);
        when(mailService.findMessage(anyObject())).thenReturn(mailMessage);

        mockMvc.perform(put(PREFIX + "/message/draft/INBOX%2FDrafts%7C1")
                .with(jsonBody(mailMessage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject", is("draft")));

        verify(mailService).updateDraftMessage(
                argThat(hasProperty("id", is("INBOX/Drafts|1"))));
    }
}
