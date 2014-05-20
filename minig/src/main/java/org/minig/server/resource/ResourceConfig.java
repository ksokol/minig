package org.minig.server.resource;

import java.util.List;

import org.codehaus.jackson.Version;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.minig.server.resource.argumentresolver.CompositeIdHandlerMethodArgumentResolver;
import org.minig.server.resource.argumentresolver.StringIdHandlerMethodArgumentResolver;
import org.minig.server.resource.config.CompositeAttachmentIdSerializer;
import org.minig.server.service.CompositeAttachmentId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = "org.minig.server.resource")
@EnableWebMvc
@Profile({ "test", "prod" })
public class ResourceConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON).ignoreAcceptHeader(true).useJaf(false).favorPathExtension(false);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new StringIdHandlerMethodArgumentResolver());
		argumentResolvers.add(new CompositeIdHandlerMethodArgumentResolver());
	}

	@Bean(name = "multipartResolver")
	public MultipartResolver getMultipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		// multipartResolver.setMaxUploadSize(100000);
		return multipartResolver;
	}

	@Override
	public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
		converters.add(new StringHttpMessageConverter());
		converters.add(converter());
	}

	private MappingJacksonHttpMessageConverter converter() {
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setObjectMapper(objectMapper());
		return mappingJacksonHttpMessageConverter;
	}

	private ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		SimpleModule testModule = new SimpleModule("MinigModule", new Version(1, 0, 0, null));
		testModule.addSerializer(CompositeAttachmentId.class, new CompositeAttachmentIdSerializer());
		objectMapper.registerModule(testModule);

		return objectMapper;
	}
}
