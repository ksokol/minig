package org.minig.config.jawr.config;

import org.minig.jawr.CommonConfigPropertiesSource;

/**
 * @author Kamill Sokol
 */
public class JavascriptConfigPropertiesSource extends CommonConfigPropertiesSource {

    public JavascriptConfigPropertiesSource() {
        super("javascript", Type.JAVASCRIPT);
        jar("/static/main.js");
    }
}
