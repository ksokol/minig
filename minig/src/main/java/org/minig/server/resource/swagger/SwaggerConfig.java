package org.minig.server.resource.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.DefaultDocumentationTransformer;
import com.mangofactory.swagger.DocumentationTransformer;
import com.mangofactory.swagger.EndpointComparator;
import com.mangofactory.swagger.OperationComparator;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.SwaggerConfigurationExtension;
import com.mangofactory.swagger.configuration.DefaultConfigurationModule;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.Jackson2SchemaDescriptor;
import com.mangofactory.swagger.models.SchemaDescriptor;

@Configuration
public class SwaggerConfig {

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();

        ClassPathResource classpathResource = new ClassPathResource("/META-INF/swagger/swagger.properties");
        propertyPlaceholderConfigurer.setLocation(classpathResource);

        return propertyPlaceholderConfigurer;
    }

    @Bean
    public CustomDocumentationController documentationController() {
        return new CustomDocumentationController();
    }

    @Bean
    @Autowired
    public SwaggerConfiguration swaggerConfiguration(DefaultConfigurationModule defaultConfig, ExtensibilityModule extensibility,
            @Value("${documentation.services.basePath}") String basePath, @Value("${documentation.services.version}") String apiVersion) {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration(apiVersion, basePath);
        return extensibility.apply(defaultConfig.apply(swaggerConfiguration));
    }

    @Bean
    @Autowired
    DefaultConfigurationModule defaultConfigurationModule() {
        return new DefaultConfigurationModule();
    }

    @Bean
    public ExtensibilityModule extensibilityModule() {
        return new ExtensibilityModule();
    }

    @Bean
    public SwaggerConfigurationExtension swaggerConfigurationExtension() {
        return new SwaggerConfigurationExtension();
    }

    @Bean
    public DocumentationTransformer documentationTransformer() {
        return new DefaultDocumentationTransformer(endPointComparator(), operationComparator());
    }

    @Bean
    public OperationComparator operationComparator() {
        return null;
    }

    @Bean
    public EndpointComparator endPointComparator() {
        return null;
    }

    @Bean
    @Autowired
    DocumentationSchemaProvider documentationSchemaProvider(TypeResolver typeResolver, SchemaDescriptor schemaDescriptor) {
        return new DocumentationSchemaProvider(typeResolver, schemaDescriptor);
    }

    @Bean
    @Autowired
    public SchemaDescriptor schemaDescriptor(ObjectMapper documentationObjectMapper) {
        return new Jackson2SchemaDescriptor(documentationObjectMapper);
    }

    @Bean
    public ObjectMapper documentationObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TypeResolver typeResolver() {
        return new TypeResolver();
    }
}
