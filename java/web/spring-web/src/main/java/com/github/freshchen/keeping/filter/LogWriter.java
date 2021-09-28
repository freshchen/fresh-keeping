package com.github.freshchen.keeping.filter;

import com.github.freshchen.keeping.model.HttpRequestLog;
import com.github.freshchen.keeping.model.HttpResponseLog;
import com.github.freshchen.keeping.model.MultiReadRequestWrapper;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author darcy
 * @since 2020/08/09
 **/
@Component
@Slf4j
public class LogWriter {

    public void logRequest(HttpServletRequest request) {
        if (request instanceof MultiReadRequestWrapper) {
            HttpRequestLog.HttpRequestLogBuilder logBuilder = HttpRequestLog.builder();
            // protocol
            logBuilder.protocol(request.getProtocol());
            // method
            logBuilder.method(request.getMethod());
            // url
            final String url = getUrl(request);
            logBuilder.url(url);
            // headers
            List<String> headerNames = Collections.list(request.getHeaderNames());
            logBuilder.headers(getHeaders(headerNames, request));
            try {
                // body
                String body = IOUtils.toString(request.getInputStream(), "UTF-8");
                if (StringUtils.hasText(body)) {
                    logBuilder.body(body);
                }
                log.info(logBuilder.build().toString());
            } catch (Exception e) {
                log.error(String.format("Failed to log response. request url: %s", url), e);
            }
        }
    }

    public void logResponse(HttpServletRequest request,
                            javax.servlet.http.HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper contentCachingResponse = (ContentCachingResponseWrapper) response;

            HttpResponseLog.HttpResponseLogBuilder logBuilder = HttpResponseLog.builder();
            // url
            final String url = getUrl(request);
            logBuilder.url(url);

            // http status code
            logBuilder.code(contentCachingResponse.getStatus());

            // headers
            Collection<String> headers = contentCachingResponse.getHeaderNames();
            logBuilder.headers(getHeaders(headers, request));

            try {
                // body
                String body = new String(contentCachingResponse.getContentAsByteArray(), Charsets.UTF_8);
                logBuilder.body(body);

                log.info(logBuilder.build().toString());

            } catch (Exception e) {
                log.error(String.format("Failed to log response. request url: %s", url), e);
            } finally {
                try {
                    contentCachingResponse.copyBodyToResponse();
                } catch (Exception e) {
                    log.error(String.format("Failed to copy contentCachingResponse. request url: %s", url), e);
                }
            }
        }
    }


    private String getUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        String url = request.getRequestURL().toString();
        return query == null ? url : url + "?" + query;
    }

    private Collection<String> getHeaders(Collection<String> headerNames, HttpServletRequest request) {
        return headerNames.stream()
                .map(header -> String.format("%s:%s", header, request.getHeader(header)))
                .collect(Collectors.toList());
    }
}
