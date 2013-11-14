package integration;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.TestConstants;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private SmtpAndImapMockServer mockServer;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void test1() throws Exception {
        MimeMessage mm = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mockServer.prepareMailBox("INBOX", mm);

        mockMvc.perform(get(PREFIX + "/message/INBOX|" + mm.getMessageID())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("INBOX|" + mm.getMessageID()))
                .andExpect(jsonPath("$.subject").value("Pingdom Monthly Report 2013-04-01 to 2013-04-30"));

    }
}
