package com.github.freshchen.keeping.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author darcy
 * @since 2021/10/13
 */
public class TestInterceptor implements HandlerInterceptor {

    /**
     * 不影响请求返回
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        throw new IllegalArgumentException();
    }
}
