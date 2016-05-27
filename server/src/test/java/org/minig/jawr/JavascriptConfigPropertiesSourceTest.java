package org.minig.jawr;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;

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
