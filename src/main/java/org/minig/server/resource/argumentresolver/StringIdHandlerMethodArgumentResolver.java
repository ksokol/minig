package org.minig.server.resource.argumentresolver;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.minig.server.resource.Id;
import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class StringIdHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private AntPathMatcher apm = new AntPathMatcher();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean assignableFrom = String.class.isAssignableFrom(parameter.getParameterType());
        boolean hasIdAnnotation = false;
        Annotation[] methodAnnotations = parameter.getParameterAnnotations();

        if (methodAnnotations != null) {
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof Id) {
                    hasIdAnnotation = true;
                    break;
                }
            }
        }

        return assignableFrom && hasIdAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String path = (String) nativeRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) nativeRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        if (path != null && bestMatchPattern != null) {
            String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

            if (finalPath != null && !finalPath.isEmpty()) {
                String value = finalPath.trim();

                try {
                    return URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        }

        return null;
    }
}
