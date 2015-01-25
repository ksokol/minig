package org.minig.config.jawr.config;

import org.junit.Test;
import org.minig.config.jawr.config.CommonConfigPropertiesSource;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class JavascriptConfigPropertiesSourceTest {

    @Test
    public void test() {
        CommonConfigPropertiesSource config = new CommonConfigPropertiesSource("test", CommonConfigPropertiesSource.Type.JAVASCRIPT);
        config.jar("test1");
        config.webjar("test2");
        Properties configProperties = config.getConfigProperties();
        assertThat(configProperties.getProperty("jawr.js.bundle.test.mappings"), is("jar:test1,webjars:test2"));
    }
}