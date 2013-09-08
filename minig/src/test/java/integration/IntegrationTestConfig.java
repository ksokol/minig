package integration;

import org.minig.server.resource.ResourceConfig;
import org.minig.server.service.ServiceTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({ ServiceTestConfig.class, ResourceConfig.class })
@Profile({ "test" })
public class IntegrationTestConfig {

}
