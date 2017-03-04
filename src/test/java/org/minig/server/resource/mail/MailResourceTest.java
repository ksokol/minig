package org.minig.server.resource.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailService;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void testFindMessagesByFolder_defaultArguments() throws Exception {
        MailMessageList mailMessageList = new MailMessageList(Collections.EMPTY_LIST, 1, 1);

        when(mailService.findMessagesByFolder(anyString(), anyInt(), anyInt())).thenReturn(mailMessageList);

        mockMvc.perform(get(PREFIX + "/message").param("folder", "INBOX")).andExpect(status().isOk())
                .andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.fullLength").value(1))
                .andExpect(jsonPath("$.page").value(1));

        verify(mailService).findMessagesByFolder("INBOX", 1, 10);
    }

    @Test
    public void testFindMessagesByFolder_explicitArguments() throws Exception {
        MailMessageList mailMessageList = new MailMessageList(Collections.EMPTY_LIST, 3, 5);

        when(mailService.findMessagesByFolder(anyString(), anyInt(), anyInt())).thenReturn(mailMessageList);

        mockMvc.perform(get(PREFIX + "/message").param("folder", "INBOX").param("page", "7").param("page_length", "11"))
                .andExpect(status().isOk()).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fullLength").value(5)).andExpect(jsonPath("$.page").value(3));

        verify(mailService).findMessagesByFolder("INBOX", 7, 11);
    }

    @Test
    public void testFindMessage_slashAsFolderSeparator() throws Exception {
        MailMessage mm = new MailMessage();
        mm.setMessageId("1");
        mm.setFolder("INBOX/deep/folder/structure");

        when(mailService.findMessage(Matchers.<CompositeId> anyObject())).thenReturn(mm);

        mockMvc.perform(get(PREFIX + "/message/INBOX/deep/folder/structure|1")).andExpect(status().isOk())
                .andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value("INBOX/deep/folder/structure|1"));

        verify(mailService).findMessage(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("messageId", IsEqual.<String> equalTo("1"))));
        verify(mailService)
                .findMessage(
                        argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("folder",
                                IsEqual.<String> equalTo("INBOX/deep/folder/structure"))));
    }

    @Test
    public void testFindMessage_dotAsFolderSeparator() throws Exception {
        MailMessage mm = new MailMessage();
        mm.setMessageId("1");
        mm.setFolder("INBOX.deep.folder.structure");

        when(mailService.findMessage(Matchers.<CompositeId> anyObject())).thenReturn(mm);

        mockMvc.perform(get(PREFIX + "/message/INBOX.deep.folder.structure|1")).andExpect(status().isOk())
                .andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value("INBOX.deep.folder.structure|1"));

        verify(mailService).findMessage(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("messageId", IsEqual.<String> equalTo("1"))));
        verify(mailService)
                .findMessage(
                        argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("folder",
                                IsEqual.<String> equalTo("INBOX.deep.folder.structure"))));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        doNothing().when(mailService).deleteMessage(Matchers.<CompositeId> anyObject());

        mockMvc.perform(delete(PREFIX + "/message/INBOX/folder|1")).andExpect(status().isOk());

        verify(mailService).deleteMessage(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("messageId", IsEqual.<String> equalTo("1"))));
        verify(mailService).deleteMessage(
                argThat(org.hamcrest.Matchers.<CompositeId> hasProperty("folder", IsEqual.<String> equalTo("INBOX/folder"))));
    }

    @Test
    public void testUpdateMessageFlags() throws Exception {
        String expectedId = "INBOX/folder|1";

        String content = new ObjectMapper().writeValueAsString(new MailMessage());

        doNothing().when(mailService).updateMessageFlags(any(MailMessage.class));

        mockMvc.perform(put(PREFIX + "/message/flag/INBOX/folder|1").contentType(TestConstants.APPLICATION_JSON_UTF8).content(content)).andExpect(
                status().isOk());

        verify(mailService).updateMessageFlags(
                argThat(org.hamcrest.Matchers.<MailMessage> hasProperty("id", IsEqual.<String> equalTo(expectedId))));
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

    //
    // @Test
    // public void testCreateMessage() throws Exception {
    // String expectedFolder = "INBOX";
    //
    // MailMessage mm = new MailMessage();
    // mm.setFolder(expectedFolder);
    //
    // String content = new ObjectMapper().writeValueAsString(mm);
    //
    // doNothing().when(mailService).createMessage(any(MailMessage.class));
    //
    // mockMvc.perform(post(PREFIX +
    // "/message").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isCreated());
    //
    // verify(mailService).createMessage(
    // argThat(org.hamcrest.Matchers.<MailMessage> hasProperty("folder",
    // IsEqual.<String> equalTo(expectedFolder))));
    // }

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

        mockMvc.perform(post(PREFIX + "/message/draft").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.subject").value("draft"));
    }

    @Test
    public void testUpdateDraftMessage() throws Exception {
        MailMessage mm = new MailMessage();
        mm.setSubject("draft");

        String content = new ObjectMapper().writeValueAsString(mm);

        when(mailService.updateDraftMessage(Matchers.<MailMessage> anyObject())).thenReturn(mm);
        when(mailService.findMessage(Matchers.<CompositeId> anyObject())).thenReturn(mm);

        mockMvc.perform(put(PREFIX + "/message/draft/INBOX/Drafts|1").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andExpect(jsonPath("$.subject").value("draft"));

        verify(mailService).updateDraftMessage(
                argThat(org.hamcrest.Matchers.<MailMessage> hasProperty("id", IsEqual.<String> equalTo("INBOX/Drafts|1"))));
    }
}
