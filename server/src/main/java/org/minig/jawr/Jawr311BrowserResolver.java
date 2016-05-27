package org.minig.jawr;

import javax.servlet.http.HttpServletRequest;

import net.jawr.web.resource.bundle.variant.resolver.BrowserResolver;

/**
 * @author icefox
 *
 * https://java.net/jira/browse/JAWR-311?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel&showAll=true
 */
public class Jawr311BrowserResolver extends BrowserResolver {

    @Override
    public String resolveVariant(HttpServletRequest request) {
        String browser = super.resolveVariant(request);
        return browser == null ? "other" : browser;
    }
}
