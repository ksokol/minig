package org.minig.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class AjaxExceptionTranslationFilter extends ExceptionTranslationFilter {

    @Autowired
    public AjaxExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationEntryPoint);
    }

    @Override
    protected void sendStartAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            AuthenticationException reason) throws ServletException, IOException {

        boolean hasXMLHttpRequestHeader = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean hasGwtRpcHeader = false;

        String header = request.getHeader("Content-Type");
        if (header != null && header.startsWith("text/x-gwt-rpc;")) {
            hasGwtRpcHeader = true;
        }

        if (hasXMLHttpRequestHeader || hasGwtRpcHeader) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
            super.sendStartAuthentication(request, response, chain, reason);
        }
    }
}
