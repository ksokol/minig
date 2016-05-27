package org.minig.server.resource.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.RessourceTestConfig;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Kamill Sokol
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RessourceTestConfig.class)
@ActiveProfiles("test")
public class ExceptionAdviceTest {

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
    public void testAnnotationResponseStatus() throws Exception {
        mockMvc.perform(get(PREFIX + "/attachment")).andExpect(status().isNotFound());
    }

    @Test
    public void testCustomExceptionResolver() throws Exception {
        when(folderServiceMock.findBySubscribed(Matchers.<Boolean> anyObject())).thenThrow(new RuntimeException());

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
