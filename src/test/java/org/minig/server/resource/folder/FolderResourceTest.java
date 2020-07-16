package org.minig.server.resource.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailFolder;
import org.minig.server.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FolderResource.class, secure = false)
public class FolderResourceTest {

    private static final String PREFIX = "/1";

    @MockBean
    private FolderService folderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindBySubscribed_params() throws Exception {
        when(folderService.findBySubscribed(anyBoolean())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(PREFIX + "/folder"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(folderService).findBySubscribed(null);

        reset(folderService);

        when(folderService.findBySubscribed(anyBoolean())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(PREFIX + "/folder")
                .param("subscribed", "true"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderService).findBySubscribed(true);

        reset(folderService);
        when(folderService.findBySubscribed(anyBoolean())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(PREFIX + "/folder")
                .param("subscribed", "false"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderService).findBySubscribed(false);
    }

    @Test
    public void testFindById() throws Exception {
        when(folderService.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX/test"))
                .andExpect(status().isOk());
        verify(folderService).findById("INBOX/test");

        reset(folderService);
        when(folderService.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX.test"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderService).findById("INBOX.test");

        reset(folderService);
        when(folderService.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX.test%20test"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderService).findById("INBOX.test test");
    }

    @Test
    public void testFindById_hasJson() throws Exception {
        MailFolder mailFolder = new MailFolder();
        mailFolder.setId("INBOX");

        when(folderService.findById(anyString())).thenReturn(mailFolder);

        mockMvc.perform(get(PREFIX + "/folder/INBOX"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INBOX"));
        verify(folderService).findById("INBOX");
    }

    @Test
    public void testCreateFolder() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolder("INBOX/createme/nested1");

        String content = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post(PREFIX + "/folder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated());
        verify(folderService).createFolderInInbox("INBOX/createme/nested1");
    }

    @Test
    public void testCreateFolderInParent() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolder("nested2");
        MailFolder mailFolder = new MailFolder();
        mailFolder.setId("INBOX/createme/nested2");

        when(folderService.createFolderInParent("INBOX/createme", "nested2")).thenReturn(mailFolder);

        String content = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post(PREFIX + "/folder/INBOX/createme")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("INBOX/createme/nested2")));
    }

    @Test
    public void testUpdateFolder() throws Exception {
        MailFolder mf = new MailFolder();
        mf.setSubscribed(true);

        String content = new ObjectMapper().writeValueAsString(mf);

        mockMvc.perform(put(PREFIX + "/folder/INBOX/createme/nested3")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk());

        verify(folderService).updateFolder(argThat(hasProperty("id", equalTo("INBOX/createme/nested3"))));
        verify(folderService).updateFolder(argThat(hasProperty("subscribed", equalTo(Boolean.TRUE))));
    }

    @Test
    public void testDeleteFolder() throws Exception {
        mockMvc.perform(delete(PREFIX + "/folder/INBOX/createme/nested5"))
                .andExpect(status().isOk());

        verify(folderService).deleteFolder("INBOX/createme/nested5");
    }
}
