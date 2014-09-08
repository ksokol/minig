package org.minig.server.resource;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.minig.server.resource.argumentresolver.CompositeIdHandlerMethodArgumentResolver;
import org.minig.server.resource.argumentresolver.StringIdHandlerMethodArgumentResolver;
import org.minig.server.resource.config.CompositeAttachmentIdSerializer;
import org.minig.server.service.CompositeAttachmentId;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author Kamill Sokol
 */
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
		multipartResolver.setMaxUploadSize(20971520); // 20MB
		return multipartResolver;
	}

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(configuredMappingJacksonHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
    }

    private MappingJackson2HttpMessageConverter configuredMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJacksonHttpMessageConverter.setObjectMapper(objectMapper());
        return mappingJacksonHttpMessageConverter;
    }

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new ISO8601DateFormat());

		SimpleModule testModule = new SimpleModule("MinigModule", new Version(1, 0, 0, null));
		testModule.addSerializer(CompositeAttachmentId.class, new CompositeAttachmentIdSerializer());
		objectMapper.registerModule(testModule);

		return objectMapper;
	}
}
