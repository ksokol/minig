package org.minig.server.resource.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.RessourceTestConfig;
import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;
import org.minig.server.TestConstants;
import org.minig.server.service.FolderService;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
public class FolderResourceTest {

    private static final String PREFIX = "/1";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FolderService folderServiceMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).build();
        reset(folderServiceMock);
    }

    @Test
    public void testFindBySubscribed_params() throws Exception {
        when(folderServiceMock.findBySubscribed(Matchers.<Boolean> anyObject())).thenReturn(new MailFolderList());

        mockMvc.perform(get(PREFIX + "/folder")).andExpect(status().isOk()).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8));
        verify(folderServiceMock).findBySubscribed(null);

        reset(folderServiceMock);

        when(folderServiceMock.findBySubscribed(Matchers.<Boolean> anyObject())).thenReturn(new MailFolderList());

        mockMvc.perform(get(PREFIX + "/folder").param("subscribed", "true")).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderServiceMock).findBySubscribed(true);

        reset(folderServiceMock);

        when(folderServiceMock.findBySubscribed(Matchers.<Boolean> anyObject())).thenReturn(new MailFolderList());

        mockMvc.perform(get(PREFIX + "/folder").param("subscribed", "false")).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderServiceMock).findBySubscribed(false);
    }

    @Test
    public void testFindById() throws Exception {
        when(folderServiceMock.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX/test")).andExpect(status().isOk());
        verify(folderServiceMock).findById("INBOX/test");

        reset(folderServiceMock);

        when(folderServiceMock.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX.test")).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderServiceMock).findById("INBOX.test");

        reset(folderServiceMock);

        when(folderServiceMock.findById(anyString())).thenReturn(new MailFolder());

        mockMvc.perform(get(PREFIX + "/folder/INBOX.test%20test")).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(folderServiceMock).findById("INBOX.test test");
    }

    @Test
    public void testFindById_hasJson() throws Exception {
        MailFolder mailFolder = new MailFolder();
        mailFolder.setId("INBOX");

        when(folderServiceMock.findById(anyString())).thenReturn(mailFolder);

        mockMvc.perform(get(PREFIX + "/folder/INBOX")).andExpect(content().contentType(TestConstants.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("INBOX"));
        verify(folderServiceMock).findById("INBOX");
    }

    @Test
    public void testCreateFolder() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolder("INBOX/createme/nested1");

        String content = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post(PREFIX + "/folder").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isCreated());
        verify(folderServiceMock).createFolderInInbox("INBOX/createme/nested1");
    }

    @Test
    public void testCreateFolderInParent() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolder("nested2");
        MailFolder mailFolder = new MailFolder();
        mailFolder.setId("INBOX/createme/nested2");

        when(folderServiceMock.createFolderInParent("INBOX/createme", "nested2")).thenReturn(mailFolder);

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

        mockMvc.perform(put(PREFIX + "/folder/INBOX/createme/nested3").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(
                status().isOk());

        verify(folderServiceMock).updateFolder(
                argThat(org.hamcrest.Matchers.<MailFolder> hasProperty("id", IsEqual.<String> equalTo("INBOX/createme/nested3"))));
        verify(folderServiceMock).updateFolder(
                argThat(org.hamcrest.Matchers.<MailFolder> hasProperty("subscribed", IsEqual.<Boolean> equalTo(Boolean.TRUE))));
    }

    @Test
    public void testDeleteFolder() throws Exception {
        mockMvc.perform(delete(PREFIX + "/folder/INBOX/createme/nested5")).andExpect(status().isOk());

        verify(folderServiceMock).deleteFolder("INBOX/createme/nested5");
    }
}
