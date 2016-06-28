package config;

import org.minig.config.ResourceConfig;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.FolderService;
import org.minig.server.service.MailService;
import org.minig.server.service.submission.DispositionService;
import org.minig.server.service.submission.SubmissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@Import(ResourceConfig.class)
@EnableWebMvc
@Profile({ "test" })
public class RessourceTestConfig {

    @Bean
    public MailService mailService() {
        return mock(MailService.class);
    }

    @Bean
    public FolderService folderService() {
        return mock(FolderService.class);
    }

    @Bean
    public AttachmentService attachmentService() {
        return mock(AttachmentService.class);
    }

    @Bean
    public SubmissionService submissionService() {
        return mock(SubmissionService.class);
    }

    @Bean
    public DispositionService dispositionService() {
        return mock(DispositionService.class);
    }
}
