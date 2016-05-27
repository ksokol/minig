package org.minig.config.jawr.config;

import org.minig.jawr.CommonConfigPropertiesSource;

/**
 * @author Kamill Sokol
 */
public class JavascriptConfigPropertiesSource extends CommonConfigPropertiesSource {

    public JavascriptConfigPropertiesSource() {
        super("javascript", Type.JAVASCRIPT);
        webjar("jquery/2.0.3/jquery.js");
        webjar("angularjs/1.2.23/angular.js");
        webjar("angularjs/1.2.23/angular-resource.js");
        webjar("angularjs/1.2.23/angular-route.min.js");
        webjar("angularjs/1.2.23/angular-sanitize.js");
        jar("/static/js/angular-local-storage.min.js");
        webjar("textAngular/1.2.0/textAngular.min.js");
        webjar("momentjs/2.7.0/min/moment-with-langs.js");
        jar("/static/js/he.js");
        jar("/static/js/config.js");
        jar("/static/js/service.js");
        jar("/static/js/filter.js");
        jar("/static/js/directive.js");
        jar("/static/js/resource.js");
        jar("/static/js/controller.js");
    }
}
