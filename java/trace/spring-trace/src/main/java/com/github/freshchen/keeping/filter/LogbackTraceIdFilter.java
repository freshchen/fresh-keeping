package com.github.freshchen.keeping.filter;

import com.github.freshchen.keeping.context.TraceContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.freshchen.keeping.context.TraceContextHolder.TRACE_ID;

/**
 * @author darcy
 * @since 2020/08/09
 **/
public class LogbackTraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = TraceContextHolder.genTraceId();
        }
        TraceContextHolder.setTraceId(traceId);
        response.addHeader(TRACE_ID, TraceContextHolder.getTraceId());
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContextHolder.clear();
        }

    }

}
