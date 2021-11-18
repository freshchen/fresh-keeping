package com.github.freshchen.keeping.filter;

import com.github.freshchen.keeping.util.TraceIdContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author darcy
 * @since 2020/08/09
 **/
public class LogTraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tid = request.getHeader("tid");
        if (StringUtils.isBlank(tid)) {
            TraceIdContextHolder.initTraceId();
        }
        filterChain.doFilter(request, response);
    }

}
