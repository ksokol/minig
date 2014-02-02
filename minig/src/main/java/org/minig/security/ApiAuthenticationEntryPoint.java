package org.minig.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dev@sokol-web.de <Kamill Sokol>
 */
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException {
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
    }
}