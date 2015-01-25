package org.minig.config.jawr.config;

/**
 * @author Kamill Sokol
 */
public class CssConfigPropertiesSource extends CommonConfigPropertiesSource {

    public CssConfigPropertiesSource() {
        super("styles", Type.CSS);
        jar("/static/css/minig.css");
    }
}
