package org.minig.server.resource.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.resource.folder.FolderResource;
import org.minig.server.service.FolderService;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FolderResource.class, secure = false)
public class ExceptionAdviceTest {

    private static final String PREFIX = "/1";

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
        when(folderService.findBySubscribed(Matchers.<Boolean> anyObject())).thenThrow(new RuntimeException());

        Map<String, Object> map = new HashMap<>();
        map.put("status", 500);
        map.put("message", "Internal Server Error");

        String content = new ObjectMapper().writeValueAsString(map);

        mockMvc.perform(get(PREFIX + "/folder"))
                .andExpect(status().is(500))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(content));
    }

    @Test
    public void testMissingServletRequestParameterException() throws Exception {
        mockMvc.perform(get(PREFIX + "/attachment")).andExpect(status().isNotFound());
    }
}
