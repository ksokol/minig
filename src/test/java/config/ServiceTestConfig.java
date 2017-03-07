package config;

import org.minig.security.MailAuthenticationToken;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.submission.TestJavaMailSenderFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@Profile({ "test" })
public class ServiceTestConfig implements InitializingBean {

    @Bean(name = "mailContext")
    public MailContext mailContext() {
        return new MailContext();
    }

    @Bean(name = "javaMailSenderFactory")
    public TestJavaMailSenderFactory javaMailSenderFactory() {
        return new TestJavaMailSenderFactory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MailAuthenticationToken mailAuthenticationToken =
                new MailAuthenticationToken("testuser", "login", AuthorityUtils.createAuthorityList("ROLE_USER"), "localhost", '.');

        SecurityContextHolder.getContext().setAuthentication(mailAuthenticationToken);
    }
}
