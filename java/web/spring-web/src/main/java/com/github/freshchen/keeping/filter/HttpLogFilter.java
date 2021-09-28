package com.github.freshchen.keeping.filter;

import org.springframework.beans.factory.annotation.Autowired;
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
public class HttpLogFilter extends OncePerRequestFilter {

    @Autowired
    private LogWriter logWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        logWriter.logRequest(httpServletRequest);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        logWriter.logResponse(httpServletRequest, httpServletResponse);
    }
}
