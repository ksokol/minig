package org.minig.server.resource.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.resource.folder.FolderResource;
import org.minig.server.service.FolderService;
import org.minig.test.WithAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FolderResource.class)
@WithAuthenticatedUser
public class ExceptionAdviceTest {

    private static final String PREFIX = "/api/1";

    @MockBean
    private FolderService folderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAnnotationResponseStatus() throws Exception {
        mockMvc.perform(get(PREFIX + "/attachment")).andExpect(status().isNotFound());
    }

    @Test
    public void testCustomExceptionResolver() throws Exception {
        when(folderService.findBySubscribed(isNull())).thenThrow(new RuntimeException());

        var map = new HashMap<>();
        map.put("status", 500);
        map.put("message", "Internal Server Error");

        var content = new ObjectMapper().writeValueAsString(map);

        mockMvc.perform(get(PREFIX + "/folder"))
                .andExpect(status().is(500))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(content));
    }

    @Test
    public void testMissingServletRequestParameterException() throws Exception {
        mockMvc.perform(get(PREFIX + "/attachment")).andExpect(status().isNotFound());
    }
}
