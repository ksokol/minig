package org.minig.config.jawr.config;

import java.util.Properties;

import net.jawr.web.resource.bundle.factory.util.ConfigPropertiesSource;

import org.springframework.util.Assert;

/**
 * @author Kamill Sokol
 */
public class CommonConfigPropertiesSource implements ConfigPropertiesSource {

    private final String id;
    private final String mapping;
    private final Properties properties;

    public CommonConfigPropertiesSource(String id, Type type) {
        Assert.hasText(id);
        Assert.notNull(type);

        this.id = String.format("jawr.%s.bundle.%s.id", type.getType(), id);
        this.mapping = String.format("jawr.%s.bundle.%s.mappings", type.getType(), id);

        properties = new Properties();
        properties.put("jawr.debug.on", "false");
        properties.put("jawr.factory.use.orphans.mapper", "false");
        properties.put("jawr.css.postprocessor.base64ImageEncoder.encode.sprite", "true");
        properties.put("jawr.css.bundle." + id + ".filepostprocessors", "base64ImageEncoder");

        id("/bundles/" + id + "." + type.getType());
    }

    @Override
    public final Properties getConfigProperties() {
        return properties;
    }

    @Override
    public boolean configChanged() {
        return false;
    }

    protected void jar(String path) {
        Assert.hasText(path);
        mapping("jar:" + path);
    }

    protected void webjar(String path) {
        Assert.hasText(path);
        mapping("webjars:" + path);
    }

    protected void mapping(String value) {
        Assert.hasText(value);
        add(mapping, value);
    }

    private void id(String value) {
        Assert.hasText(value);
        add(id, value);
    }

    private void add(String key, Object value) {
        Assert.notNull(key);
        Assert.notNull(value);

        Object propertyValue = properties.get(key);

        if(propertyValue == null) {
            properties.put(key, value);
        } else {
            properties.put(key, propertyValue.toString() + "," + value);
        }
    }

    public enum Type {

        CSS("css"), JAVASCRIPT("js");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        String getType() {
            return type;
        }
    }
}
