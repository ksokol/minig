package org.minig.config.jawr.config;

import org.minig.jawr.CommonConfigPropertiesSource;

/**
 * @author Kamill Sokol
 */
public class CssConfigPropertiesSource extends CommonConfigPropertiesSource {

    public CssConfigPropertiesSource() {
        super("styles", Type.CSS);
        jar("/static/main.css");
    }
}
