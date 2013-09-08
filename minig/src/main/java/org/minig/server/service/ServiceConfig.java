package org.minig.server.service;

import org.minig.XTrustProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
@ComponentScan(basePackages = "org.minig.server.service")
@Profile({ "prod" })
public class ServiceConfig {

	static {
		// TODO
		XTrustProvider.install();
	}

	// TODO
	@Bean(name = "javaMail")
	public JavaMailSender javaMail() {
		return new JavaMailSenderImpl();
	}

}
